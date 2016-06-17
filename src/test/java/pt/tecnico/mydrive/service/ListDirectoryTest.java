package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.service.dto.EntryDto;
import pt.tecnico.mydrive.exception.*;

public class ListDirectoryTest extends AbstractServiceTest {
	private long token;
	private long regUser;
	private MyDrive mydrive;
	private Directory dir ,permDir, test, testSize;
	private Link link;
	private App app;
	private PlainFile plainFile;
	private Calendar cal;
	private String date;
	private User user;
	

	protected void populate() {
		mydrive = MyDrive.getInstance();
		token = (new Login(mydrive, "root", "***")).getToken();
		User root = mydrive.getUserByUsername("root");
		
		user = new User(mydrive, "joao", "joao", "joao123123", (byte) (0b1111_1110), root);
		regUser = (new Login(mydrive, "joao", "joao123123")).getToken();
		dir = root.getHome();

		plainFile = new PlainFile(mydrive, "hello", (byte) (0b1111_1111), root, dir, "world");
		permDir = new Directory(mydrive, "testPerm", (byte) (0b0000_0000), root, dir);
		testSize = new Directory(mydrive, "TestSize", (byte) (0b1111_1111), root, dir);
		test = new Directory(mydrive, "test", (byte) (0b1111_1111), root, dir);
		link = new Link(mydrive, "link", (byte) (0b1111_1111), root, dir, "/home/root");
		app = new App(mydrive, "app", (byte) (0b1111_1111), root, dir, "class.method");
	}

	@Test
	public void success() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();

		assertEquals(8, service.result().size());
	}

	@Test
	public void successWithEmptyDirectory() {
		ListDirectoryService service = new ListDirectoryService(token);
		mydrive.changeCurrentDir("/home/root/TestSize", token);
		service.execute();

		assertEquals(2, ((List<EntryDto>) service.result()).size());
	}
	
	@Test
	public void alphabeticalOrder() {
		String string = "";
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();

		for(EntryDto e: service.result()){
			string+= e.getName()+ ' ';
		}
		assertEquals(". .. app hello link->/home/root test testPerm TestSize ", string);
	}

	@Test
	public void timeOfCreation() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		long l = ((List<EntryDto>) service.result()).get(5).getLastModified().getMillis();
		assertTrue(test.getLastModified().getMillis() + 5 > l && l > test.getLastModified().getMillis()-5);
	}
	
	@Test
	public void correctPermissions() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("rwxdrwxd", ((List<EntryDto>) service.result()).get(4).getPermissions());
	}
	
	@Test
	public void correctDirType() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("Directory", ((List<EntryDto>) service.result()).get(5).getType());
	}
	
	@Test
	public void correctAppType() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("App", ((List<EntryDto>) service.result()).get(2).getType());
	}

	@Test
	public void correctLinkType() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("Link", ((List<EntryDto>) service.result()).get(4).getType());
	}
	
	@Test
	public void correctPlainFileType() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("PlainFile", ((List<EntryDto>) service.result()).get(3).getType());
	}
	
	@Test
	public void correctTarget() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("link->/home/root", ((List<EntryDto>) service.result()).get(4).getName());
	}
	
	@Test 
	public void correctUsername() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("root", ((List<EntryDto>) service.result()).get(4).getOwner());
	}
	
	@Test
	public void correctEntryId() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		assertEquals(test.getEntryID(), ((List<EntryDto>) service.result()).get(5).getEntryID());
	}
	
	@Test
	public void correctName() {
		ListDirectoryService service = new ListDirectoryService(token);
		service.execute();
		
		assertEquals("test", ((List<EntryDto>) service.result()).get(5).getName());
	}
	
	@Test(expected = TokenDoesNotExistException.class)
	public void tokenDoesNotExist() {
		ListDirectoryService service = new ListDirectoryService(token + 100);
		service.execute();
	}

	@Test(expected = PermissionDeniedException.class)
	public void permissionDenied() {
		ListDirectoryService service = new ListDirectoryService(regUser);
		mydrive.changeCurrentDir(permDir.getPath(), regUser);
		service.execute();
	}



}
