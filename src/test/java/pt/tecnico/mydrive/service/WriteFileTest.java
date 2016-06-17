package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;

public class WriteFileTest extends AbstractServiceTest {
	private Login login;
	private long token;
	private long regularUserToken;
	private long expiredToken;
	private MyDrive mydrive;
	
	protected void populate() {
		mydrive = MyDrive.getInstance();
		login = new Login(mydrive, "root", "***");
		token = login.getToken();
		User root = mydrive.getUserByUsername("root");

		User user = new User(mydrive, "joao", "joao", "joao123123", (byte) (0b1111_1111), root);
		regularUserToken = (new Login(mydrive, "joao", "joao123123")).getToken();

		Login expiredLogin = new Login(mydrive, "joao", "joao123123");
		expiredLogin.setTimeToLive(this, 999);
		expiredToken = expiredLogin.getToken();
		Directory dir = user.getHome();
		
		new PlainFile(mydrive, "empty_file", (byte) (0b1111_1111), root, dir, "");
		new PlainFile(mydrive, "text_file", (byte) (0b1111_1111), root, dir, "test");
		new PlainFile(mydrive, "forbidden_file", (byte) (0b1111_1011), root, dir, "");
		
		new App(mydrive, "get_instance", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.domain.MyDrive.getInstance");
		
		new Link(mydrive, "plain_link", (byte) (0b1111_1111), root, dir, "/home/joao/empty_file");
		new Link(mydrive, "forbidden_link", (byte) (0b1111_1111), root, dir, "/home/joao/forbidden_file");
		new Link(mydrive, "app_link", (byte) (0b1111_1111), root, dir, "/home/joao/get_instance");
		new Link(mydrive, "directory_link", (byte) (0b1111_1111), root, dir, "/home/joao/directory");
		new Link(mydrive, "invalid_link", (byte) (0b1111_1111), root, dir, "/home/joao/invalid_file");
		new Link(mydrive, "link_link", (byte) (0b1111_1111), root, dir, "/home/joao/plain_link");
		new Directory(mydrive, "directory", (byte) (0b1111_1111), root, dir);
		
		Directory exeProtectedDir = new Directory(mydrive, "exe_protected", (byte) (0b1111_1101), root, dir);
		new PlainFile(mydrive, "test_file", (byte) (0b1111_1111), root, exeProtectedDir, "");
		
		Directory forbiddenDir = new Directory(mydrive, "forbidden_dir", (byte) (0b0000_0000), root, dir);
		new PlainFile(mydrive, "test_file", (byte) (0b0000_0000), root, forbiddenDir, "");
	}
	
	// #1
	@Test (expected = TokenDoesNotExistException.class)
	public void invalidToken() {
		WriteFileService service = new WriteFileService(0, "empty_file", "test");
		service.execute();
	}
	
	// #2
	@Test (expected = TokenDoesNotExistException.class)
	public void expiredToken() throws InterruptedException {
		Thread.sleep(1000);
		WriteFileService service = new WriteFileService(expiredToken, "empty_file", "test");
		service.execute();
	}
	
	// #3
	@Test (expected = IllegalArgumentException.class)
	public void emptyName() {
		WriteFileService service = new WriteFileService(regularUserToken, "", "test");
		service.execute();
	}
	
	// #4
	@Test (expected = IllegalArgumentException.class)
	public void nullName() {
		WriteFileService service = new WriteFileService(regularUserToken, null, "test");
		service.execute();
	}
	
	// #5
	@Test (expected = EntryDoesNotExistException.class)
	public void unknownName() {
		WriteFileService service = new WriteFileService(regularUserToken, "ghost_file", "test");
		service.execute();
	}
	
	// #6
	@Test //(expected = EntryDoesNotExistException.class)
	public void pathName() {
		WriteFileService service = new WriteFileService(regularUserToken, "./empty_file", "test");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/empty_file", login.getUser(), login.getCurrentDir())).getContent(), "test");
	}
	
	// #7
	@Test (expected = PermissionDeniedException.class)
	public void forbiddenFile() {
		WriteFileService service = new WriteFileService(regularUserToken, "forbidden_file", "test");
		service.execute();
	}
	
	// #8
	@Test (expected = PermissionDeniedException.class)
	public void exeProtected() {
		mydrive.changeCurrentDir("/home/joao/exe_protected", regularUserToken);
		WriteFileService service = new WriteFileService(regularUserToken, "test_file", "test");
		service.execute();
	}
	
	// #9
	@Test
	public void emptyFile() throws InterruptedException {
		WriteFileService service = new WriteFileService(regularUserToken, "empty_file", "test");
		Thread.sleep(50); // makes sure modification date is properly tested
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/empty_file", login.getUser(), login.getCurrentDir())).getContent(), "test");
		assertTrue((new DateTime()).getMillis()-mydrive.getEntry("/home/joao/empty_file", login.getUser(), login.getCurrentDir()).getLastModified().getMillis() < 50);
	}
	
	// #10
	@Test
	public void textFile() {
		WriteFileService service = new WriteFileService(regularUserToken, "text_file", "changed");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/text_file", login.getUser(), login.getCurrentDir())).getContent(), "changed");
	}
	
	// #11
	@Test
	public void emptyContent() {
		WriteFileService service = new WriteFileService(regularUserToken, "text_file", "");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/text_file", login.getUser(), login.getCurrentDir())).getContent(), "");
	}
	
	// #12
	@Test (expected = IllegalArgumentException.class)
	public void nullContent() {
		WriteFileService service = new WriteFileService(regularUserToken, "text_file", null);
		service.execute();
	}
	
	// #13
	@Test
	public void application() {
		WriteFileService service = new WriteFileService(regularUserToken, "get_instance", "test");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/get_instance", login.getUser(), login.getCurrentDir())).getContent(), "test");
	}
	
	// #14
	@Test
	public void plainLink() {
		WriteFileService service = new WriteFileService(regularUserToken, "plain_link", "test");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/empty_file", login.getUser(), login.getCurrentDir())).getContent(), "test");
	}
	
	// #15
	@Test (expected = PermissionDeniedException.class)
	public void forbiddenLink() {
		WriteFileService service = new WriteFileService(regularUserToken, "forbidden_link", "test");
		service.execute();
	}
	
	// #16
	@Test
	public void appLink() {
		WriteFileService service = new WriteFileService(regularUserToken, "app_link", "test");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/get_instance", login.getUser(), login.getCurrentDir())).getContent(), "test");
	}
	
	// #17
	@Test
	public void linkLink() {
		WriteFileService service = new WriteFileService(regularUserToken, "link_link", "test");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/empty_file", login.getUser(), login.getCurrentDir())).getContent(), "test");
	}
	
	// #18
	@Test (expected = UnsupportedOperationException.class)
	public void directoryLink() {
		WriteFileService service = new WriteFileService(regularUserToken, "directory_link", "test");
		service.execute();
	}
	
	// #19
	@Test (expected = EntryDoesNotExistException.class)
	public void invalidLink() {
		WriteFileService service = new WriteFileService(regularUserToken, "invalid_link", "test");
		service.execute();
	}
	
	// #20
	@Test (expected = UnsupportedOperationException.class)
	public void directory() {
		WriteFileService service = new WriteFileService(regularUserToken, "directory", "test");
		service.execute();
	}
	
	// #21
	@Test
	public void rootPermissions() {
		mydrive.changeCurrentDir("/home/joao/forbidden_dir", token);
		WriteFileService service = new WriteFileService(token, "test_file", "test");
		service.execute();
		assertEquals(((PlainFile)mydrive.getEntry("/home/joao/forbidden_dir/test_file", login.getUser(), login.getCurrentDir())).getContent(), "test");
	}
}