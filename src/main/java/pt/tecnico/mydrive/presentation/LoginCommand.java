package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.InvalidLoginException;
import pt.tecnico.mydrive.exception.RequiredArgumentException;
import pt.tecnico.mydrive.service.LoginUserService;

public class LoginCommand extends MyDriveCommand {
	private long token;
	
	public LoginCommand(MyDriveShell myDriveShell) {
		super(myDriveShell,"login", "Login user" );
	}

	public void execute(String[] args) {
		try{
			if (args.length > 2 || args.length < 1)		
				System.out.println("Usage: login username [password]" );
			else {
				String password = (args.length>=2) ? args[1] : "";
				LoginUserService service = new LoginUserService(args[0], password);
				service.execute();
				token = service.result();
				currentToken = service.result();
				username = args[0];
				shell().addToken(username, token);
				shell().setToken(token);
				System.out.println(username + " is logged in :)");
				
			}
		}
		catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
		}catch(InvalidLoginException e){
			System.out.println(e.getMessage());
	    }
	
	}
	
	public long getToken() {
		return token;
	}
}
