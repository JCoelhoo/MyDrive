package pt.tecnico.mydrive.presentation;



public class KeyCommand extends MyDriveCommand{

	public KeyCommand(MyDriveShell myDriveShell) {
		super(myDriveShell,"token", "Allows the interpreter to switch between active sessions" );
	}
	
	public void execute(String[] args) {
		try{
			if (args.length == 0) {
				System.out.println("Token: " + (currentToken = shell().getToken()) );
				System.out.println("Username: " + (username = shell().getUsername(currentToken)) );
				System.out.println("use 'token [username]' to change current user");
			}
			else {
				currentToken = shell().getToken(args[0]);
				username = args[0];
				shell().setToken(currentToken);
				System.out.println("Username: " + username);
				System.out.println("Token: " + currentToken);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

}
