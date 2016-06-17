package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;

public class ReadFileService extends MyDriveService {

    private String path;
    private String content;
    private long token;

    public ReadFileService(long token, String path) throws IllegalArgumentException {
        if (path == null || path.equals(""))
            throw new IllegalArgumentException("name");

        this.token = token;
        this.path = path;
    }

    @Override
    public final void dispatch() throws EntryDoesNotExistException {
    	MyDrive mydrive = getMyDrive();
        Login login = mydrive.getLoginByToken(token);

        User user = login.getUser();
        Entry entry = mydrive.getEntry(path, user, login.getCurrentDir());
        content = entry.read(user);

    }
    
    public String result(){
    	return content;
    }
}