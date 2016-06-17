package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.service.WriteFileService;

public class WriteCommand extends MyDriveCommand{

	public WriteCommand(MyDriveShell myDriveShell) {
		super(myDriveShell,"update", "Replaces the file contents" );
	}
  @Override
	public void execute(String[] args) {
	  try{
			if (args.length < 2) {
				throw new RuntimeException("USAGE: "+name()+" <path> <text>");
			} else {
				(new WriteFileService(currentToken, args[0], args[1])).execute();
			}
	  }catch(TokenDoesNotExistException e){
			System.out.println(e.getMessage());
		}catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
		}catch(EntryDoesNotExistException e){
			System.out.println(e.getMessage());
		}catch(PermissionDeniedException e){
			System.out.println(e.getMessage());
		}catch(UnsupportedOperationException e){
			System.out.println(e.getMessage());

		}
	  
	  
	  
	}

}
