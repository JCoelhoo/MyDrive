package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;

public class DeleteFileService extends MyDriveService {

	private String path;
	private long token;

	public DeleteFileService(long token, String path) {
		this.path = path;
		this.token = token;
	}

	@Override
	public final void dispatch() throws EntryDoesNotExistException {
	
		if (path == null || path.equals("")) {
			throw new IllegalArgumentException(path);
		}

		if (path.equals(".") || path.equals(".."))
			throw new CannotRemoveEntryException();
		
		MyDrive mydrive = getMyDrive();
        Login login = mydrive.getLoginByToken(token);
			
		User user = login.getUser();
		Entry entry = mydrive.getEntry(path, user, login.getCurrentDir());


		
		entry.remove(user);
		
	}

}
