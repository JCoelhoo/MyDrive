package pt.tecnico.mydrive.service;

import org.joda.time.DateTime;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;

public class WriteFileService extends MyDriveService {

    private String path;
    private String content;
    private long token;

    public WriteFileService(long token, String path, String content) throws IllegalArgumentException{
        if (path == null || path == "")
            throw new IllegalArgumentException("path");
        if (content == null)
            throw new IllegalArgumentException("content");
        this.token = token;
        this.path = path;
        this.content = content;
    }

    public String getPath() {
    	return path;
    }
    
    @Override
    public final void dispatch() throws EntryDoesNotExistException, PermissionDeniedException, InvalidLoginException, TokenDoesNotExistException {
        MyDrive mydrive = getMyDrive();
        Login login = mydrive.getLoginByToken(token);
        Directory currentDir = login.getCurrentDir();
        User user = login.getUser();
        mydrive.getEntry(getPath(), user, currentDir).write(user, content);
        
    }
}