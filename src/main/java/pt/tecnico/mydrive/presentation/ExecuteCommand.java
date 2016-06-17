package pt.tecnico.mydrive.presentation;
import pt.tecnico.mydrive.exception.CyclicLinkException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.MethodDoesNotExistException;
import pt.tecnico.mydrive.exception.NotAnAppException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.service.ExecuteFileService;

public class ExecuteCommand extends MyDriveCommand{

	public ExecuteCommand(MyDriveShell myDriveShell) {
		super(myDriveShell,"do", "Execute the file" );
	}

    @Override
    public void execute(String[] args) {
    	try{
	        if (args.length < 1) {
	        	System.out.println("USAGE: " + name() + " <path> [<args>]");
	        } else {
	            if(args.length == 1)
	                (new ExecuteFileService(currentToken, args[0])).execute();
	            else {
	                String[] executeArgs = new String[args.length - 1];
	                for(int i = 1; i < args.length; i++) {
	                  executeArgs[i - 1] = args[i];   
	                }
	
	                (new ExecuteFileService(currentToken, args[0], executeArgs)).execute();
	            }
	        }
    	}catch(TokenDoesNotExistException e){
			System.out.println(e.getMessage());
		}catch(MethodDoesNotExistException e){
			System.out.println(e.getMessage());
		}catch(NotAnAppException e){
			System.out.println(e.getMessage());
		}catch(UnsupportedOperationException e){
			System.out.println(e.getMessage());
		}catch(EntryDoesNotExistException e){
			System.out.println(e.getMessage());
		}catch(CyclicLinkException e){
			System.out.println(e.getMessage());
		}catch(PermissionDeniedException e){
			System.out.println(e.getMessage());
		}
    }

}
