package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.service.*;

public class LoginUserTest extends AbstractServiceTest {

	long token;
	User user1;
	User user2;
	MyDrive mydrive;


	protected void populate() {
		mydrive = MyDrive.getInstance();
		token = (new Login(mydrive, "root", "***")).getToken();
		User root = mydrive.getUserByUsername("root");

		user1=new User(mydrive,  "Rita", "rita", "ritinha123", (byte) (0b1111_1010),  root);

		user2=new User(mydrive,  "Joao", "joao", "joao123123", (byte) (0b1111_1010),  root);
	}


	@Test
	public void success() {
		LoginUserService service = new LoginUserService("rita", "ritinha123");
		service.execute();
	
		assertEquals("Output not correct.",user1, mydrive.getLoginByToken(service.result()).getUser());
	}


	@Test(expected = InvalidLoginException.class)
	public void UserDoesNotExist(){
		LoginUserService service = new LoginUserService("manuel", "manuel123");
		service.execute();
	}

	@Test
	public void MultipleLogin(){
		LoginUserService service = new LoginUserService("rita", "ritinha123");
		service.execute();
		service = new LoginUserService("joao", "joao123123");
		service.execute();
		
		assertEquals("Output not correct.",user2, mydrive.getLoginByToken(service.result()).getUser());

	}
 
	@Test
	public void LoginExpiration(){
		LoginUserService service = new LoginUserService("rita", "ritinha123");
		service.execute();
		assertEquals("Output not correct.",7200000, mydrive.getLoginByToken(service.result()).getTimeToLive());

	}

	@Test(expected = InvalidLoginException.class)
	public void WrongPassword(){
		LoginUserService service = new LoginUserService("rita", "ritinha111");
		service.execute();		
	}
    
    @Test(expected = pt.tecnico.mydrive.exception.IllegalArgumentException.class)
	public void usernameIsNull(){
		LoginUserService service = new LoginUserService(null, "ritinha111");
		service.execute();
	}

	@Test(expected = pt.tecnico.mydrive.exception.IllegalArgumentException.class)
	public void passwordIsNull(){
		LoginUserService service = new LoginUserService("Blabla", null);
		service.execute();
	}

	
}