package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.ImportXMLService;

public class MyDriveShell extends Shell {
	protected static long currentToken;

	public static void main(String[] args) throws Exception {
		if (args.length >= 1) {
			(new ImportXMLService(args[0])).execute();
		}
		MyDriveShell sh = new MyDriveShell();
		sh.execute();
	}

	public MyDriveShell() { // add commands here
		super("MyDrive");
		new LoginCommand(this);
		new ChangeWorkingDirectoryCommand(this);
		new ListCommand(this);
		new ExecuteCommand(this);
		new WriteCommand(this);
		new EnvironmentCommand(this);
		new KeyCommand(this);
	}
}
