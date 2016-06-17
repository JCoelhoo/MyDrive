package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;
import pt.tecnico.mydrive.exception.IllegalArgumentException;

public class ReadFileTest extends AbstractServiceTest {
	
	private long token;
	
	protected void populate() {
	
		MyDrive mydrive = MyDrive.getInstance();
		User root = mydrive.getUserByUsername("root");

		User user = new User(mydrive, "joao", "joao", "joao123123", (byte) (0b1111_1110), root);
		token = (new Login(mydrive, "joao", "joao123123")).getToken();
		Directory dir = user.getHome();

		// current dir is /home/Manuel
		new PlainFile(mydrive, "empty_file", (byte) (0b1111_1110), user, dir, "");
		new PlainFile(mydrive, "text_file", (byte) (0b1111_1110), user, dir, "test");
		new PlainFile(mydrive, "forbidden_file", (byte) (0b0000_0000), user, dir, "");
		
		new App(mydrive, "get_instance", (byte) (0b1111_1110), user, dir, "pt.tecnico.mydrive.domain.MyDrive.getInstance");

		new Link(mydrive, "plain_link", (byte) (0b1111_1110), user, dir, "./empty_file");
		new Link(mydrive, "forbidden_link", (byte) (0b1111_1110), user, dir, "./forbidden_file");
		new Link(mydrive, "app_link", (byte) (0b1111_1110), user, dir, "./get_instance");
		new Link(mydrive, "link_link", (byte) (0b1111_1110), user, dir, "./plain_link");
		
		new Directory (mydrive, "directory", (byte) (0b1111_1110), user, dir);
		new Link(mydrive, "directory_link", (byte) (0b1111_1110), user, dir, "./directory");
		
		new Link(mydrive, "invalid_link", (byte) (0b1111_1110), user, dir, "./xpto");		
		
		
	}

	// #1
	@Test (expected = TokenDoesNotExistException.class)
	public void invalidToken() {
		ReadFileService service = new ReadFileService(0, "empty_file");
		service.execute();
	}
	
	// #2
	@Test (expected = EntryDoesNotExistException.class)
	public void unknownName() {
		ReadFileService service = new ReadFileService(token, "ghost_file");
		service.execute();
	}
	
	// #3
	@Test //(expected = EntryDoesNotExistException.class)
	public void pathName() {
		ReadFileService service = new ReadFileService(token, "./empty_file");
		service.execute();
	}
	
	// #4
	@Test (expected = PermissionDeniedException.class)
	public void forbiddenFile() {
		ReadFileService service = new ReadFileService(token, "forbidden_file");
		service.execute();
	}
	
	// #5
	@Test
	public void emptyFile() {
		ReadFileService service = new ReadFileService(token, "empty_file");
		service.execute();
		
		assertEquals(service.result(), "");
	}
	
	// #6
	@Test
	public void textFile() {
		ReadFileService service = new ReadFileService(token, "text_file");
		service.execute();
		
		assertEquals(service.result(), "test");
	}
	
	// #7
	@Test
	public void application() {
		ReadFileService service = new ReadFileService(token, "get_instance");
		service.execute();
		
		assertEquals(service.result(), "pt.tecnico.mydrive.domain.MyDrive.getInstance");
	}
	
	// #8
	@Test
	public void plainLink() {
		ReadFileService service = new ReadFileService(token, "plain_link");
		service.execute();
		
		assertEquals(service.result(), "");
	}
	
	// #9
	@Test (expected = PermissionDeniedException.class)
	public void forbiddenLink() {
		ReadFileService service = new ReadFileService(token, "forbidden_link");
		service.execute();
	}
	
	// #10
	@Test
	public void appLink() {
		ReadFileService service = new ReadFileService(token, "app_link");
		service.execute();
		
		assertEquals(service.result(), "pt.tecnico.mydrive.domain.MyDrive.getInstance");
	}
	
	// #11
	@Test
	public void linkLink() {
		ReadFileService service = new ReadFileService(token, "link_link");
		service.execute();

		assertEquals(service.result(), "");
	}


	// #12
	@Test (expected = UnsupportedOperationException.class)
	public void directoryLink() {
		ReadFileService service = new ReadFileService(token, "directory_link");
		service.execute();
	}
	
	// #13
	@Test (expected = EntryDoesNotExistException.class)
	public void invalidLink() {
		ReadFileService service = new ReadFileService(token, "invalid_link");
		service.execute();
	}
	
	// #14
	@Test (expected = UnsupportedOperationException.class)
	public void directory() {
		ReadFileService service = new ReadFileService(token, "directory");
		service.execute();
	}

	// #15
	@Test (expected = IllegalArgumentException.class)
	public void nullName() {
		ReadFileService service = new ReadFileService(token, null);
		service.execute();
	}

	// #16
	@Test (expected = IllegalArgumentException.class)
	public void emptyName() {
		ReadFileService service = new ReadFileService(token, "");
		service.execute();
	}
	
}
