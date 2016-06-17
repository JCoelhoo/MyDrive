package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.MyDrive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.Entry;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.service.dto.EntryDto;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

public class ListDirectoryService extends MyDriveService {

    private long token;
    private List<EntryDto> entryList;
    String path = null;


    public ListDirectoryService(long token) {
        this.token = token;
        this.path=null;
        entryList = new ArrayList<EntryDto>();
    }
    
    public ListDirectoryService(long currentToken, String path) {
        this.token = currentToken;
        this.path=path;
        entryList = new ArrayList<EntryDto>();
	}

	@Override
    public final void dispatch() throws TokenDoesNotExistException, PermissionDeniedException{
    	MyDrive mydrive = getMyDrive();
        Login login = mydrive.getLoginByToken(token);
        Directory dir;
        if (path == null){
        	dir = login.getCurrentDir();
        }else{
        	User user = login.getUser();
        	dir = mydrive.getDirectory(path, user, login.getCurrentDir());
        }
    	

    	for (Entry e : dir.getEntryList(login.getUser())) {
    		entryList.add(new EntryDto(e.toString(), e.getEntryID(), e.printPermissions(), e.getLastModified(), e.getOwner().getUsername(), e.getEntryType()));
    	}
    	entryList.add(new EntryDto(".", dir.getEntryID(), dir.printPermissions(), dir.getLastModified(), dir.getOwner().getUsername(), dir.getEntryType()));
    	entryList.add(new EntryDto("..", dir.getParent().getEntryID(), dir.getParent().printPermissions(), dir.getParent().getLastModified(), dir.getParent().getOwner().getUsername(), dir.getParent().getEntryType()));
    	Collections.sort(entryList);      
    }
    
    public List<EntryDto> result(){
    	return entryList;
    }

    
}