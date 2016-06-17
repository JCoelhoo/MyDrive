package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.mydrive.domain.EnvironmentVariable;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
import pt.tecnico.mydrive.service.AddEnvVariableService;

public class AddEnvVariableTest extends AbstractServiceTest {

	private MyDrive myDrive;
	long token;
	private User user;
	private Login login;


	protected void populate() {
		myDrive = MyDrive.getInstance();
		token = (new Login(myDrive, "root", "***")).getToken();
		User root = myDrive.getUserByUsername("root");
		user = new User(myDrive,  "joao", "joao", "joao123123", (byte) (0b1111_1010),  root);
		login = new Login(myDrive, "joao", "joao123123");
	}

	@Test
	public void success() {
		AddEnvVariableService service = new AddEnvVariableService(token, "Env", "Var");
		service.execute();
		assertEquals(service.result().get(0).getName(), "Env");
		assertEquals(service.result().get(0).getValue(), "Var");
		assertEquals(service.result().size(), 1);		
	}

	@Test
	public void successWithEmptyEnv() {
		AddEnvVariableService service = new AddEnvVariableService(token, "", "");
		service.execute();
		assertEquals(service.result().get(0).getName(), "");
		assertEquals(service.result().get(0).getValue(), "");
		assertEquals(service.result().size(), 1);
	}

	@Test
	public void updateVar() {
		AddEnvVariableService service = new AddEnvVariableService(token, "Env", "Var");
		service.execute();
		AddEnvVariableService service1 = new AddEnvVariableService(token, "Env", "Var2");
		service1.execute();
		assertEquals(service1.result().get(0).getName(), "Env");
		assertEquals(service1.result().get(0).getValue(), "Var2");
		assertEquals(service1.result().size(), 1);	
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidName() {
		AddEnvVariableService service = new AddEnvVariableService(token, null, "Var");
		service.execute();	
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidValue() {
		AddEnvVariableService service = new AddEnvVariableService(token, "", null);
		service.execute();	
	}
}
