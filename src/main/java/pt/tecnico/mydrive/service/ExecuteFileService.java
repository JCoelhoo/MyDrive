package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.*;

public class ExecuteFileService extends MyDriveService {
	private long token;
	private String path;
	private String[] args; 

    
   public ExecuteFileService(long token, String path) {
        this.token = token;
        this.path = path;
        this.args = new String[0];
    }

   
    public ExecuteFileService(long token, String path, String[] args) {
    	this.token = token;
    	this.path = path;
    	if (args != null){
    		this.args = args;
    	} else{ 
    		this.args = new String[0];
    		
    	}
    }

    @Override
    public final void dispatch() throws EntryDoesNotExistException {
    	MyDrive mydrive = getMyDrive();
    	Login login = mydrive.getLoginByToken(token);
    	User user = login.getUser();
    	Entry entry = mydrive.getEntry(path, user, login.getCurrentDir());
    	entry.execute(user, args);
    }
    
    public String result(){
    	return null;
    }
}