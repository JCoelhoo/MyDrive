package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exception.TokenDoesNotExistException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.NotADirectoryException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
    


public class ChangeDirectoryService extends MyDriveService {

  private long token;
  private String path;
  private Directory directory;


  public ChangeDirectoryService(long token,String path) {
    this.token=token;
    this.path = path;
  }


  @Override
  public final void dispatch() throws UnsupportedOperationException, TokenDoesNotExistException, EntryDoesNotExistException, NotADirectoryException, PermissionDeniedException {
  
    if(path==null){
      throw new IllegalArgumentException("Given path is null");
    }
    else if(path.equals("")){
      throw new EntryDoesNotExistException("");   
    }
      
    MyDrive mydrive = getMyDrive();
    Login login = mydrive.getLoginByToken(token);
    Directory dir = mydrive.getDirectory(path, login.getUser(), login.getCurrentDir());
    login.setCurrentDir(dir);
    directory = login.getCurrentDir();
        

  }
    
  public String result(){
  	return directory.getPath();
  }
    
}