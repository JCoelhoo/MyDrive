package pt.tecnico.mydrive.presentation;

import java.util.List;

import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.dto.EntryDto;


public class ListCommand extends MyDriveCommand{

	public ListCommand(MyDriveShell myDriveShell) {
		super(myDriveShell,"ls", "Print the complete entry information of directory" );
	}
	
	public void execute(String[] args) {
		try{
			if (args.length == 0) {
				ListDirectoryService lds = new ListDirectoryService(currentToken);
				lds.execute();
				List<EntryDto> list = lds.result();
				for (EntryDto l: list){
					System.out.println(l.toString());
				}
				System.out.println("use 'ls <path>' to list a different directory" );
			} 
			else {
				ListDirectoryService lds = new ListDirectoryService(currentToken, args[0]);
				lds.execute();
				List<EntryDto> list = lds.result();
				for (EntryDto l: list){
					System.out.println(l.toString());
				}
		}
		}catch(TokenDoesNotExistException e){
			System.out.println(e.getMessage());
		}catch(PermissionDeniedException e){
			System.out.println(e.getMessage());
		}catch(EntryDoesNotExistException e){
			System.out.println(e.getMessage());

		}
			
			
		
	}

}
