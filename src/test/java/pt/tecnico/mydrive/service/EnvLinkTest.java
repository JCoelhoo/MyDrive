package pt.tecnico.mydrive.service;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.*;

@RunWith(JMockit.class)
public class EnvLinkTest extends AbstractServiceTest {	
	private MyDrive mydrive;
	private Directory homedir;
	private Login login;
	private long token;
	
	protected void populate() {
		mydrive = MyDrive.getInstance();
		User root = mydrive.getUserByUsername("root");

		User user = new User(mydrive,  "joao", "joao", "joao123123", (byte) (0b1111_1111), root);
		login = new Login(mydrive, "joao", "joao123123");
		token = login.getToken();
		login.addEnvVar("HOME", "/home");
		login.addEnvVar("USER", "joao");
		login.addEnvVar("EMPTY", "");
		
		homedir = user.getHome();
		
		new PlainFile(mydrive, "plain_file", (byte) (0b1111_1111), root, homedir, "");
	}
	
	// #1
	@Test
	public void userLink() {
		new MockUp<WriteFileService>() {
			@Mock
			String getPath() { return "/home/" + login.getEnvValue("USER") + "/plain_file"; }
		};
		new WriteFileService(token, "/home/joao/user_link", "test").execute();
		assertEquals("test", ((PlainFile)mydrive.getEntry("/home/joao/plain_file", login.getUser(), login.getCurrentDir())).getContent());
	}
		
	// #2
	@Test
	public void homeLink() {
		new MockUp<WriteFileService>() {
			@Mock
			String getPath() { return login.getEnvValue("HOME") + "/joao/plain_file"; }
		};
		new WriteFileService(token, "/home/joao/home_link", "test").execute();
		assertEquals("test", ((PlainFile)mydrive.getEntry("/home/joao/plain_file", login.getUser(), login.getCurrentDir())).getContent());
	}
	
	// #3
	@Test (expected = VariableDoesNotExistException.class)
	public void nullLink() {
		new MockUp<WriteFileService>() {
			@Mock
			String getPath() { return "/home/" + login.getEnvValue("NULL") + "/plain_file"; }
		};
		
		new WriteFileService(token, "/home/joao/user_link", "test").execute();
		assertEquals("test", ((PlainFile)mydrive.getEntry("/home/joao/plain_file", login.getUser(), login.getCurrentDir())).getContent());
	}
	
	// #4
	@Test
	public void emptyLink() {
		new MockUp<WriteFileService>() {
			@Mock
			String getPath() { return "/home/joao/" + login.getEnvValue("EMPTY") + "plain_file"; }
		};
		
		new WriteFileService(token, "/home/joao/empty_link", "test").execute();
		assertEquals("test", ((PlainFile)mydrive.getEntry("/home/joao/plain_file", login.getUser(), login.getCurrentDir())).getContent());
	}
	
	// #5
	@Test
	public void twoVars() {
		new MockUp<WriteFileService>() {
			@Mock
			String getPath() { return login.getEnvValue("HOME") + "/" + login.getEnvValue("USER") + "/plain_file"; }
		};
		
		new WriteFileService(token, "/home/joao/double_link", "test").execute();
		assertEquals("test", ((PlainFile)mydrive.getEntry("/home/joao/plain_file", login.getUser(), login.getCurrentDir())).getContent());
	}
	
}
