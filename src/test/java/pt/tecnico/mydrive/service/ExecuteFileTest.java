package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.apache.commons.lang.ArrayUtils;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;

public class ExecuteFileTest extends AbstractServiceTest {
	private Login login;
	private long token;
	private long regularUserToken;
	private long expiredToken;
	private MyDrive mydrive;
	private static boolean executed;
	private static String[] testArgs;
	private final static String EXCEPTION_MSG = "fabricated exception";
	
	protected void populate() {
		executed           = false;
		mydrive            = MyDrive.getInstance();
		login              = new Login(mydrive, "root", "***");
		token              = login.getToken();
		User root          = mydrive.getUserByUsername("root");

		User user          = new User(mydrive, "joao", "joao", "joao123123", (byte) (0b1111_1111), root);
		regularUserToken   = (new Login(mydrive, "joao", "joao123123")).getToken();

		Login expiredLogin = new Login(mydrive, "joao", "joao123123");
		expiredLogin.setTimeToLive(this, 999);
		expiredToken       = expiredLogin.getToken();

		Directory dir      = root.getHome();
		
		new App(mydrive, "test_app", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.service.ExecuteFileTest.test");
		new App(mydrive, "test2_app", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.service.ExecuteFileTest.test2");
		new App(mydrive, "test3_app", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.service.ExecuteFileTest.test3");
		new App(mydrive, "inexistentMethod", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.service.ExecuteFileTest.test0");
		new App(mydrive, "default_app", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.service.ExecuteFileTest");
		new PlainFile(mydrive, "one_arg_app", (byte) (0b1111_1111), root, dir, "/home/root/test_app arg1");
		new PlainFile(mydrive, "multiple_arg_app", (byte) (0b1111_1111), root, dir, "/home/root/test_app arg1 arg2 arg3");
		new PlainFile(mydrive, "empty_file", (byte) (0b1111_1111), root, dir, "");
		new PlainFile(mydrive, "non_existing_file", (byte) (0b1111_1111), root, dir, "/waitwhat arg1");
		new PlainFile(mydrive, "file_file", (byte) (0b1111_1111), root, dir, "/home/root/one_arg_app");
		new PlainFile(mydrive, "link_file", (byte) (0b1111_1111), root, dir, "/home/root/app_link arg0");
		new PlainFile(mydrive, "multiple_line", (byte) (0b1111_1111), root, dir, "/home/root/test_app arg1\n/home/root/test2_app arg2");
		new PlainFile(mydrive, "forbidden_file", (byte) (0b1101_1101), root, user.getHome(), "/home/root/test_app"); 
		
		new Link(mydrive, "app_link", (byte) (0b1111_1111), root, dir, "/home/root/test_app");
		new Link(mydrive, "file_link", (byte) (0b1111_1111), root, dir, "/home/root/one_arg_app");

		new Link(mydrive, "link1", (byte) (0b1111_1111), root, dir, "/home/root/link2");
		new Link(mydrive, "link2", (byte) (0b1111_1111), root, dir, "/home/root/link3");
		new Link(mydrive, "link3", (byte) (0b1111_1111), root, dir, "/home/root/link1");

		new Directory(mydrive, "directory", (byte) (0b1111_1111), root, dir);
		Directory exeProtectedDir = new Directory(mydrive, "exe_protected", (byte) (0b1111_1101), root, dir);
		new PlainFile(mydrive, "test_file", (byte) (0b1111_1111), root, exeProtectedDir, "");
		
		Directory forbiddenDir = new Directory(mydrive, "forbidden_dir", (byte) (0b0000_0000), root, dir);
		new PlainFile(mydrive, "test_file", (byte) (0b0000_0000), root, forbiddenDir, "");
	}
	@After
	public void tearDown() {
		super.tearDown();
		executed = false;
		testArgs = null;
	}

	public static void main(String[] args) {
		executed = true;
		testArgs = args.clone();
	}

	public static void test(String[] args) {
		executed = true;
		testArgs = args.clone();
	}

	public static void test2(String[] args) {
		executed = true;
		testArgs = (String[])ArrayUtils.addAll(testArgs, args);
	}

	public static void exceptionTest(String[] args) {
		throw new RuntimeException(EXCEPTION_MSG);
	}
	// #1
	@Test(expected = TokenDoesNotExistException.class)
	public void invalidToken() {
		ExecuteFileService service = new ExecuteFileService(0 , "test_app", new String[0]);
		service.execute();
	}
	
	// #2
	@Test(expected = TokenDoesNotExistException.class)
	public void expiredToken() throws InterruptedException {
		Thread.sleep(1000);
		ExecuteFileService service = new ExecuteFileService(expiredToken , "test_app", new String[0]);
		service.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void permissionDeniedPlainFile() throws InterruptedException {
		ExecuteFileService service = new ExecuteFileService(regularUserToken , "forbidden_file", new String[0]);
		service.execute();
	}


	@Test
	public void success() {
		ExecuteFileService service = new ExecuteFileService(token , "test_app", new String[0]);
		service.execute();
		assertTrue("the service did not call the correct method", executed);
		assertTrue("the service should call the method with a 0-length string array if no arguments provided", 
			testArgs != null && testArgs.length==0);
	}

	@Test
	public void defaultMainMethod() {
		ExecuteFileService service = new ExecuteFileService(token , "default_app", new String[0]);
		service.execute();
		assertTrue("the service should call main when no method is provided", executed);
	}

	@Test
	public void appWithArguments() {
		String a[] = new String[]{"arg0", "arg1"};
		ExecuteFileService service = new ExecuteFileService(token , "test_app", a);
		service.execute();
		assertTrue("the service did not call the correct method", executed);
		assertTrue("the service should call the method with a 2-length string array if 2 arguments provided in exec service", 
			testArgs != null && testArgs.length==2);
		assertEquals("first argument provided is 'arg0'", "arg0", testArgs[0]);
		assertEquals("second argument provided is 'arg1'","arg1", testArgs[1]);

	}


	@Test(expected=MethodDoesNotExistException.class)
	public void inexistentMethod() {
		ExecuteFileService service = new ExecuteFileService(token , "inexistentMethod", new String[0]);
		service.execute();
	}


	@Test
	public void appLink() {
		ExecuteFileService service = new ExecuteFileService(token , "app_link", new String[0]);
		service.execute();
		assertTrue("execution of a link did not execute its target app", executed);
		assertTrue("the service should call the method with a 0-length string array if no arguments provided", 
			testArgs != null && testArgs.length==0);
	}

	// @Test
	// public void exceptionThrowingApp() {
	// 	ExecuteFileService service = new ExecuteFileService(token , "test3_app", new String[0]);
	// 	try {
	// 		service.execute();
	// 		fail("exceptions should not be caught");
	// 	} catch (RuntimeException e) {
	// 		assertEquals(EXCEPTION_MSG, e.getMessage());
	// 	}
	// }

	@Test
	public void fileLink() {
		ExecuteFileService service = new ExecuteFileService(token , "file_link", new String[0]);
		service.execute();
		assertTrue("execution of a link did not execute its target file", executed);
		assertTrue("the service should call the method with a 1-length string array if one argument provided", 
			testArgs != null && testArgs.length==1 && testArgs[0].equals("arg1"));
	}

	@Test
	public void pathName() {
		ExecuteFileService service = new ExecuteFileService(token , "/home/root/test_app", new String[0]);
		service.execute();
		assertTrue("the service did not call the correct method", executed);
	}
	@Test(expected=NotAnAppException.class)
	public void executeFileCallingANonExistingApp() {
		ExecuteFileService service = new ExecuteFileService(token , "non_existing_file", new String[0]);
		service.execute();
	}
	@Test(expected=NotAnAppException.class)
	public void executeFileCallingAnotherFile() {
		ExecuteFileService service = new ExecuteFileService(token , "file_file", new String[0]);
		service.execute();
	}
	@Test(expected=UnsupportedOperationException.class)
	public void executeDirectory() {
		ExecuteFileService service = new ExecuteFileService(token , "directory", new String[0]);
		service.execute();
	}
	@Test(expected=UnsupportedOperationException.class)
	public void protectedDirectory() {
		ExecuteFileService service = new ExecuteFileService(token , "exe_protected", new String[0]);
		service.execute();
	}
	@Test(expected=EntryDoesNotExistException.class)
	public void inexistentEntry() {
		ExecuteFileService service = new ExecuteFileService(token , "nope", new String[0]);
		service.execute();
	}

	@Test(expected=CyclicLinkException.class)
	public void cyclicLink() {
		ExecuteFileService service = new ExecuteFileService(token , "link1", new String[0]);
		service.execute();
	}
	@Test
	public void oneArgumentPlainFile() {
		ExecuteFileService service = new ExecuteFileService(token , "one_arg_app", new String[0]);
		service.execute();
		assertTrue("the service did not call the correct method", executed);
		assertTrue("the service should call the method with a 1-length string array if one argument provided", 
			testArgs != null && testArgs.length==1 && testArgs[0].equals("arg1"));
	}

	@Test
	public void plainFileExecutesLink() {
		ExecuteFileService service = new ExecuteFileService(token , "link_file", new String[0]);
		service.execute();
		assertTrue("the service did not execute the link even tho it points to an app", executed);
		assertTrue("the service should call the method with a 1-length string array if one argument provided", 
			testArgs != null && testArgs.length==1 && testArgs[0].equals("arg0"));
	}
	@Test
	public void multipleLineFile() {
		ExecuteFileService service = new ExecuteFileService(token , "multiple_line", new String[0]);
		service.execute();
		assertTrue("the service did not call the correct method", executed);
		assertTrue("the service should call both methods and pass their respective arguments", testArgs != null); 
		assertEquals (testArgs.length, 2); 
		assertEquals (testArgs[0], "arg1"); 
		assertEquals (testArgs[1] ,"arg2");
	}

	@Test
	public void multipleArgumentPlainFile() {
		ExecuteFileService service = new ExecuteFileService(token , "multiple_arg_app", new String[0]);
		service.execute();
		assertTrue("the service did not call the correct method", executed);
		assertTrue("the service should call the method with a 3-length string array if three arguments provided", 
			testArgs != null && testArgs.length==3);
		assertEquals("first argument provided is 'arg1'", "arg1", testArgs[0]);
		assertEquals("second argument provided is 'arg2'","arg2", testArgs[1]);
		assertEquals("third argument provided is 'arg3'","arg3", testArgs[2]);
	}
	
  
}