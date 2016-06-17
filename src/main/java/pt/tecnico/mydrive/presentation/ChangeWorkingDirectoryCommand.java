package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.CyclicLinkException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.NotADirectoryException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.service.ChangeDirectoryService;

public class ChangeWorkingDirectoryCommand extends MyDriveCommand {
	private ChangeDirectoryService cd;

	public ChangeWorkingDirectoryCommand(MyDriveShell myDriveShell) {
		super(myDriveShell, "cwd", "Changes the current working directory");
	}

	public void execute(String[] args) {
		try {
			String result = (args.length == 0) ? "" : args[0];
			cd = new ChangeDirectoryService(currentToken, result);
			cd.execute();
			System.out.println(cd.result() + " ");
			// new ChangeDirectoryService(args[0]).execute();
		} catch (TokenDoesNotExistException e) {
			System.out.println(e.getMessage());
		} catch (EntryDoesNotExistException e) {
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (NotADirectoryException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println(e.getMessage());
		} catch (CyclicLinkException e) {
			System.out.println(e.getMessage());
		}
	}

}
