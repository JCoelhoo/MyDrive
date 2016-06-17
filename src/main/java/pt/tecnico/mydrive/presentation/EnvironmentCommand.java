package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.dto.EnvVarDto;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.service.AddEnvVariableService;
public class EnvironmentCommand extends MyDriveCommand{

	public EnvironmentCommand(MyDriveShell myDriveShell) {
		super(myDriveShell,"env", "Creates or changes the value of the environment variable" );
	}

	public void execute(String[] args) {
		try{
			AddEnvVariableService service;
			if (args.length==0) {
				service = new AddEnvVariableService(currentToken, null, null);
				service.execute();
				for (EnvVarDto var: service.result()) {
					System.out.println(var.getName() + " = " + var.getValue());
				}
			} else if (args.length == 1) {
				service = new AddEnvVariableService(currentToken, null, null);
				service.execute();
				for (EnvVarDto var: service.result()) {
					if (var.getName().equals(args[0])) {
						System.out.println(var.getValue());
						break;
					}
				}
				/* possibly throw an exception if can't find the variable*/
			} else {
				service = new AddEnvVariableService(currentToken, args[0], args[1]);
				service.execute();
			}
		}catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
	}

}
