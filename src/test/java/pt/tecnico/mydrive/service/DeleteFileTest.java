package pt.tecnico.mydrive.service;
import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;

public class DeleteFileTest extends AbstractServiceTest {
	private long token;
	private long regularUserToken;
	private long expiredToken;
	private MyDrive mydrive;
	private Login login;
	
	protected void populate() {
		mydrive = MyDrive.getInstance();
		login = new Login(mydrive, "root", "***");
		token = login.getToken();
		User root = mydrive.getUserByUsername("root");

		User user = new User(mydrive,  "joao", "joao", "joao123123", (byte) (0b1111_1111), root);
		Login login = new Login(mydrive, "joao", "joao123123");
		regularUserToken = login.getToken();
		
		Login expiredLogin = new Login(mydrive, "joao", "joao123123");
		expiredLogin.setTimeToLive(this, 999);
		expiredToken = expiredLogin.getToken();

		Directory dir = user.getHome();
		
		new PlainFile(mydrive, "plain_file", (byte) (0b1111_1111), root, dir, "");
		new PlainFile(mydrive, "forbidden_file", (byte) (0b1111_1110), root, dir, "");
		
		new App(mydrive, "get_instance", (byte) (0b1111_1111), root, dir, "pt.tecnico.mydrive.domain.MyDrive.getInstance");
		
		new Link(mydrive, "plain_link", (byte) (0b1111_1111), root, dir, "/home/joao/plain_file");
		new Link(mydrive, "forbidden_link", (byte) (0b1111_1111), root, dir, "/home/joao/forbidden_file");
		new Link(mydrive, "invalid_link", (byte) (0b1111_1111), root, dir, "/home/joao/ghost");
		
		new Directory(mydrive, "empty", (byte) (0b1111_1111), root, dir);
		new Directory(mydrive, "files", (byte) (0b1111_1111), root, dir);
		new PlainFile(mydrive, "file_01", (byte) (0b1111_1111), root, dir, "");
		new PlainFile(mydrive, "file_02", (byte) (0b1111_1111), root, dir, "");
		
		Directory d1 = new Directory(mydrive, "directories", (byte) (0b1111_1111), root, dir);
		Directory d1_1 = new Directory(mydrive, "directory_01", (byte) (0b1111_1111), root, d1);
		new PlainFile(mydrive, "file_03", (byte) (0b1111_1111), root, d1_1, "");
		new Directory(mydrive, "directory_02", (byte) (0b1111_1111), root, d1);
		new PlainFile(mydrive, "file_04", (byte) (0b1111_1111), root, d1, "");
		
		Directory d2 = new Directory(mydrive, "depth_01", (byte) (0b1111_1111), root, dir);
		new Directory(mydrive, "depth_02", (byte) (0b1111_1111), root, d2);
		
		Directory writeProtectedDir = new Directory(mydrive, "write_protected", (byte) (0b1111_1011), root, dir);
		Directory exeProtectedDir = new Directory(mydrive, "exe_protected", (byte) (0b1111_1101), root, dir);
		new PlainFile(mydrive, "test_file", (byte) (0b1111_1111), root, writeProtectedDir, "");
		new PlainFile(mydrive, "test_file", (byte) (0b1111_1111), root, exeProtectedDir, "");
		
		Directory forbiddenDir = new Directory(mydrive, "forbidden_dir", (byte) (0b0000_0000), root, dir);
		new PlainFile(mydrive, "test_file", (byte) (0b0000_0000), root, forbiddenDir, "");
	}
	
	// #1
	@Test (expected = TokenDoesNotExistException.class)
	public void invalidToken() {
		DeleteFileService service = new DeleteFileService(0, "plain_file");
		service.execute();
	}
	
	// #2
	@Test (expected = TokenDoesNotExistException.class)
	public void expiredToken() throws InterruptedException {
		Thread.sleep(1000);
		DeleteFileService service = new DeleteFileService(expiredToken, "plain_file");
		service.execute();
	}
	
	// #3
	@Test (expected = IllegalArgumentException.class)
	public void emptyName() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "");
		service.execute();
	}
	
	// #4
	@Test (expected = IllegalArgumentException.class)
	public void nullName() {
		DeleteFileService service = new DeleteFileService(regularUserToken, null);
		service.execute();
	}
	
	// #5
	@Test (expected = EntryDoesNotExistException.class)
	public void unknownName() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "ghost_file");
		service.execute();
	}
	
	// #6
	@Test //(expected = EntryDoesNotExistException.class)
	public void pathName() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "./plain_file");
		service.execute();
	}
	
	// #7
	@Test
	public void plainFile() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "plain_file");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("plain_file"));
	}
	
	// #8
	@Test (expected = PermissionDeniedException.class)
	public void forbiddenFile() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "forbidden_file");
		service.execute();
	}
	
	// #9
	@Test (expected = PermissionDeniedException.class)
	public void writeProtectedDir() {
		mydrive.changeCurrentDir("/home/joao/write_protected", regularUserToken);
		DeleteFileService service = new DeleteFileService(regularUserToken, "test_file");
		service.execute();
	}
	
	// #10
	@Test (expected = PermissionDeniedException.class)
	public void exeProtectedDir() {
		mydrive.changeCurrentDir("/home/joao/exe_protected", regularUserToken);
		DeleteFileService service = new DeleteFileService(regularUserToken, "test_file");
		service.execute();
	}
		
	// #11
	@Test
	public void application() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "get_instance");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("get_instance"));
	}
	
	// #12
	@Test
	public void plainLink() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "plain_link");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("plain_link"));
	}
	
	// #13
	@Test
	public void forbiddenLink() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "forbidden_link");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("forbidden_link"));
	}
	
	// #14
	@Test
	public void invalidLink() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "invalid_link");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("invalid_link"));
	}
	
	// #15
	@Test
	public void emptyDirectory() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "empty");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("empty"));
	}
	
	// #16
	@Test
	public void fileDirectory() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "files");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("files"));
	}
	
	// #17
	@Test
	public void directoryDirectory() {
		DeleteFileService service = new DeleteFileService(regularUserToken, "empty");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("empty"));
	}
	
    // #18
	@Test (expected = CannotRemoveEntryException.class)
	public void rootDirectory() {
		mydrive.changeCurrentDir("/", token);
		DeleteFileService service = new DeleteFileService(token, ".");
		service.execute();
	}
	
	// #19
	@Test (expected = CannotRemoveEntryException.class)
	public void currentDirectory() {
		mydrive.changeCurrentDir("/home/joao/depth_01/depth_02", token);
		DeleteFileService service = new DeleteFileService(token, ".");
		service.execute();
	}
	
	// #20
	@Test (expected = CannotRemoveEntryException.class)
	public void parentDirectory() {
		mydrive.changeCurrentDir("/home/joao/depth_01/depth_02", token);
		DeleteFileService service = new DeleteFileService(token, "..");
		service.execute();
	}
	
	// #21
	@Test
	public void rootPermissions() {
		mydrive.changeCurrentDir("/home/joao/forbidden_dir", token);
		DeleteFileService service = new DeleteFileService(token, "test_file");
		service.execute();
		
		assertFalse((login.getCurrentDir()).hasEntry("test_file"));
	}
}
