package pt.tecnico.mydrive.service;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;

public class CreateFileTest extends AbstractServiceTest {
	private long token;
	private long regUser, expiredToken;
	private User user, root;
	private MyDrive mydrive;
	private Directory dir, test, testPerm;
	private int id;
	
	protected void populate() {
		mydrive = MyDrive.getInstance();
		token = (new Login(mydrive, "root", "***")).getToken();
		root = mydrive.getUserByUsername("root");


		user = new User(mydrive,  "joao", "joao", "joao123123", (byte) (0b1111_1010), root);
		Login login = new Login(mydrive, "joao", "joao123123");
		regUser = login.getToken();
		dir = root.getHome();
			
		new Directory(mydrive, "test", (byte) (0b1111_1111), root, dir);
		
		new PlainFile(mydrive, "plainFile", (byte) (0b1111_1111), root, dir, null);
		new Directory(mydrive, "testPerm", (byte) (0b0000_0000), root, dir);  
		new Directory(mydrive, "writePerm", (byte) (0b1011_1011), root, dir);  
		Link link = new Link(mydrive, "testLink", (byte) (0b1111_1111), root, dir, "/test");
		id = link.getEntryID();

		Login expiredLogin = new Login(mydrive, "joao", "joao123123");
		expiredLogin.setTimeToLive(this, 999);
		expiredToken = expiredLogin.getToken();
		
	}

	@Test
	public void successPlainFile() {
		CreateFileService service = new CreateFileService(token, "plain", "PlainFile", "success");
		service.execute();
		
		assertTrue(dir.getEntryByName("plain", user) instanceof PlainFile);
	}
	
	@Test
	public void successPlainFileWithoutContent() {
		CreateFileService service = new CreateFileService(token, "plain2", "PlainFile");
		service.execute();
		
		assertTrue(dir.getEntryByName("plain2", user) instanceof PlainFile);
	}
	
	@Test
	public void successLinkFile() {
		CreateFileService service = new CreateFileService(token, "link", "Link", "/home/reguser/plainFile");
		service.execute();
		
		assertTrue(dir.getEntryByName("link", user) instanceof Link);
	}
	
	@Test
	public void successAppFile(){
		CreateFileService service = new CreateFileService(token, "app", "App", "class.method");
		service.execute();
		
		assertTrue(dir.getEntryByName("app", user) instanceof App);
	}
	
	@Test
	public void successDirectory(){
		CreateFileService service = new CreateFileService(token, "dir", "Directory");
		service.execute();
		
		assertTrue(dir.getEntryByName("dir", user) instanceof Directory);
	}
	
	@Test 
	public void timeOfCreation() {
		CreateFileService service = new CreateFileService(token, "dir", "Directory");
		service.execute();

		assertTrue(new DateTime().getMillis() - dir.getEntryByName("dir", user).getLastModified().getMillis() < 5000);
	}
	
	@Test
	public void correctPermissions() {
		CreateFileService service = new CreateFileService(token, "dir", "Directory");
		service.execute();

		assertEquals((byte) (0b1111_1010), dir.getEntryByName("dir", root).getPermissions());
	}
	
	@Test 
	public void correctUsername() {
		CreateFileService service = new CreateFileService(token, "dir", "Directory");
		service.execute();
		
		assertEquals("root", dir.getEntryByName("dir", user).getOwner().getUsername());
	}
	
	@Test
	public void correctEntryId() {
		CreateFileService service = new CreateFileService(token, "dir", "Directory");
		service.execute();
		
		assertEquals(id+1, dir.getEntryByName("dir", user).getEntryID());
	}
	
	@Test
	public void correctName() {
		CreateFileService service = new CreateFileService(token, "test3", "Directory");
		service.execute();
		
		assertEquals("test3", dir.getEntryByName("test3", user).getName());
	}
	
	@Test
	public void nullContent(){
		CreateFileService service = new CreateFileService(token, "null", "PlainFile", "" );
		service.execute();
	}
	
	@Test(expected = EntryAlreadyExistsException.class) 
	public void entryAlreadyExists(){
		CreateFileService service = new CreateFileService(token, "test", "PlainFile", "failed");
		service.execute();
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void permissionDenied(){
		CreateFileService service = new CreateFileService(regUser, "testPerm2", "PlainFile", "content");
		mydrive.changeCurrentDir("/home/root/testPerm", regUser);
		service.execute();
	}
	
	@Test(expected = ExceededPathLengthException.class)
	public void pathLength(){
		CreateFileService service = new CreateFileService(token, "PfIbKGOOhhqeferW29HYWyDuSHm6RlrqMjLWGw44XTan1wGvFumKqIA9ZTSO7pzJBDlBBZBEjItU4su9S8U94z6vRaV2VDgn0rVI29CXO64URRcHHIpzYt507S1jO1HqMgBsK1ciXpVbJter2kzctOeyQbsbBeOgK6rNHpZ5zY3GjM52mqnqJJY75znwPkbab7M7jyGENSgsl2jVURjlu8tger2UPeroKSoGfWFGZzCLwmMOJesFISxXXO1v2vejB2XOFtZMVG2iJfXqWF05aEYbgAUiFkesLIVV3kNyuau3Qr6DBu4JEh18pLRBwqDAIFTGnn8sI86coccXFSj8NfFJQiYxq3QQvEO3lrXfU4iGGLXpn1oabQe37b45riboKiZYgG4lC7YOUzGh5FxEXtZ4h5l7nMn2er8AjpuWnVXpr7bsOBvNssaJjBAKNaXxZJMzTtIyR80FGkgvpBXyqYyoHciQ4PBprhBzQ6OtCFJq0fXM1sobD8JCK5x6vO8c02CFy3AbvTw6ZJR1bPRMsNfKUq42FjyI67jHC03QIPnYyhO4UapOWT9tXv38b5KcVKr6CJqiRNTZInbVMpOKgaWbWPcj5BVy2z3aB5wQYzftMu4ObrK6BcCAhHZJ7KqZDFUvFsgApTvBw2qGoHFveFUzLPe2uPJyLooMtr1YvX7AQuORL50TcHD2ILWjOR5NYhSUQf6pmhmRf4m5BCXXMMxbD91OtFM318s02HHoRJKsmZ6p7pAAuIRMo8MzrR0t5e8UD4uJN5UevBVJkEP5lmcMMPrkyi8ms8tQofFAGuZHCCoZPCC3iyYyccRYwYxO1PrmHcFezJ63xKD4PU2tMN2MH4e6kvA3yr29glwpDGesquJg6XIpcXS4l6ShxAFnFZ1IeapMHn7lsNWbYqxVv4nWMSywmB7jCbkEZQgNqSfgugMOZIzC2rrjlsry2SIjvDvCqtROqgxQuMiOyV7aKkDNNIFOMTrmgXBs1UHOTnWZ3Brn0oDXsAcL0Yv7Pn8", "PlainFile", "content");
		service.execute();
	}
	
	@Test(expected = FileTypeDoesNotExistException.class)
	public void invalidFileType(){
		CreateFileService service = new CreateFileService(token, "invalidFileType", "link", "content");
		service.execute();
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void invalidTarget(){
		CreateFileService service = new CreateFileService(token, "invalidFileType", "Link");
		service.execute();
	}


	@Test(expected = IllegalArgumentException.class)
	public void nullType(){
		CreateFileService service = new CreateFileService(token, "null", null, "hey");
		service.execute();
	}
	
	@Test(expected = InvalidNameException.class)
	public void invalidFileName(){
		CreateFileService service = new CreateFileService(token, "/", "PlainFile");
		service.execute();
	}
	
	@Test(expected = InvalidNameException.class)
	public void invalidFileName2(){
		CreateFileService service = new CreateFileService(token, "\0", "PlainFile");
		service.execute();
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void nullType2(){
		CreateFileService service = new CreateFileService(token, "null", null);
		service.execute();
	}
	


	@Test(expected = IllegalArgumentException.class)
	public void nullName(){
		CreateFileService service = new CreateFileService(token, null, "PlainFile");
		service.execute();
	}

	@Test(expected = pt.tecnico.mydrive.exception.IllegalArgumentException.class)
	public void directoryWithContent(){
		CreateFileService service = new CreateFileService(token, "hey", "Directory", "ups");
		service.execute();
	}
	
	@Test(expected = PermissionDeniedException.class)
	public void permissionDenied2(){
		CreateFileService service = new CreateFileService(regUser, "testPerm2", "PlainFile", "content");
		mydrive.changeCurrentDir("/home/root/writePerm", regUser);
		service.execute();
	}
	
	@Test (expected = TokenDoesNotExistException.class)
	public void expiredToken() throws InterruptedException {
		Thread.sleep(1000);
		CreateFileService service = new CreateFileService(expiredToken, "testPerm2", "PlainFile", "content");
		service.execute();
	}

	@Test (expected = TokenDoesNotExistException.class)
	public void invalidToken() throws InterruptedException {
		CreateFileService service = new CreateFileService(regUser+1, "testPerm2", "PlainFile", "content");
		service.execute();
	}

}
