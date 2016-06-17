package pt.tecnico.mydrive.service;
import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
public class ChangeDirectoryTest extends AbstractServiceTest {
	private long token;
	private long regularUserToken;
	private MyDrive mydrive;
	private Login login;

	protected void populate() {
		mydrive = MyDrive.getInstance();
		User root = mydrive.getUserByUsername("root");


		new User(mydrive,  "joao", "joao", "joao123123", (byte) (0b1111_1010), root);
		login = new Login(mydrive, "joao", "joao123123");
		regularUserToken = login.getToken();

		login = new Login(mydrive, "root", "***");
		token = login.getToken();

		new Directory(mydrive, "test", (byte) (0b1111_1010), root, (Directory) mydrive.getEntry("/home", root, login.getCurrentDir()));
		new Directory(mydrive, "target", (byte) (0b1111_1010), root, (Directory) mydrive.getEntry("/home/test", root, login.getCurrentDir()));
		new Directory(mydrive, "nopermission", (byte) (0b1111_1101), root, (Directory) mydrive.getEntry("/home/test", root, login.getCurrentDir()));
		new Directory(mydrive, "dir", (byte) (0b1111_1010), root, (Directory) mydrive.getEntry("/home/test/nopermission", root, login.getCurrentDir()));
		new PlainFile(mydrive, "plainfile", (byte) (0b1111_1010), root, (Directory) mydrive.getEntry("/home", root, login.getCurrentDir()), "");
		new Link(mydrive, "validlink", (byte) (0b1111_1010), root,(Directory)  mydrive.getEntry("/home", root, login.getCurrentDir()), "/home/test/target");
		new Link(mydrive, "validlinknopermission", (byte) (0b1111_1010), root, (Directory) mydrive.getEntry("/home", root, login.getCurrentDir()), "/home/test/nopermission");
		new Link(mydrive, "testlink", (byte) (0b1111_1010), root, (Directory) mydrive.getEntry("/home", root, login.getCurrentDir()), "/home/test");
		new Link(mydrive, "recursive", (byte) (0b1111_1010) , root, (Directory) mydrive.getEntry("/home", root, login.getCurrentDir()), "/home/recursive");
	}

	@Test(expected = TokenDoesNotExistException.class)
	public void TokenDoesNotExist(){
		ChangeDirectoryService service = new ChangeDirectoryService(0, "/home");
		service.execute();
	}

	@Test
	public void success() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home");
		service.execute();
	
		assertEquals("Did not change to correct directory from absolute path.","/home", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());	
		assertEquals("Output not correct.","/home", service.result());
		
	}
	@Test
	public void relativePath() {
		mydrive.changeCurrentDir("/home", token);
		ChangeDirectoryService service2 = new ChangeDirectoryService(token, "test/target");
		service2.execute();
		
		assertEquals("Did not change to correct directory from relative path.", "/home/test/target", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());
		assertEquals("Output not correct.","/home/test/target", service2.result());
	}

	@Test
	public void sameDirectory() {
		mydrive.changeCurrentDir("/home", token);
		ChangeDirectoryService service2 = new ChangeDirectoryService(token, ".");
		service2.execute();
		
		assertEquals("'.' entry not working.", "/home", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());
		assertEquals("Output not correct.", "/home", service2.result());
	}

	@Test
	public void parentDirectory() {
		mydrive.changeCurrentDir("/home", token);
		ChangeDirectoryService service2 = new ChangeDirectoryService(token, "..");
		service2.execute();
		
		assertEquals("'..' did not change to parent directory", "/", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());
		assertEquals("Output not correct.", "/", service2.result());
	}


	@Test
	public void rootDirectory() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/");
		service.execute();
		
		assertEquals("Could not change to root directory '/'", "/", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());
		assertEquals("Output not correct.", "/", service.result());
	}

	@Test(expected = EntryDoesNotExistException.class)
	public void inexistentDirectory() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home2");
		service.execute();
	}

	@Test(expected = EntryDoesNotExistException.class)
	public void emptyString() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "");
		service.execute();
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullPointer() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, null);
		service.execute();
	}
	
	
	@Test(expected = NotADirectoryException.class)
	public void notADirectory() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/plainfile");
		service.execute();
	}
	
	@Test
	public void permissionNotDeniedRoot() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/test/nopermission/dir");
		service.execute();
		assertEquals("Output not correct.", "/home/test/nopermission/dir", service.result());
	}

	@Test(expected = PermissionDeniedException.class)
	public void permissionDenied() {
		ChangeDirectoryService service = new ChangeDirectoryService(regularUserToken, "/home/test/nopermission/dir");
		service.execute();

	}

	@Test
	public void linkDirectory() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/validlink");
		service.execute();

		assertEquals("Changing to a link directory did not work.", "/home/test/target" , MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());
		assertEquals("Output not correct.", "/home/test/target", service.result());
	}

	@Test
	public void insideLink() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/testlink/target");
		service.execute();

		assertEquals("Changing to a directory inside a link directory did not work.", "/home/test/target", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());
		assertEquals("Output not correct.", "/home/test/target", service.result());
	}

	@Test(expected = PermissionDeniedException.class)
	public void permissionDeniedOnLink() {
		ChangeDirectoryService service = new ChangeDirectoryService(regularUserToken, "/home/validlinknopermission/dir");
		service.execute();
	}
@Test
	public void parentOfRoot() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/../.");
		service.execute();

		assertEquals("Unable to change correctly to parent of root directory.", "/", MyDrive.getInstance().getLoginByToken(token).getCurrentDir().getPath());	
		assertEquals("Output not correct.", "/", service.result());
	}

@Test(expected = CyclicLinkException.class)
	public void recursive() {
		ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/recursive");
		service.execute();
	}

}
