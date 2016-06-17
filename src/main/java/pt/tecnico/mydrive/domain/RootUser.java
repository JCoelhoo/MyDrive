package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.CannotRemoveSuperUserException;
import pt.tecnico.mydrive.exception.ForbiddenMethodException;
import pt.tecnico.mydrive.exception.UserAlreadyExistsException;

public class RootUser extends RootUser_Base {
	
	//ROOT USER
	  public RootUser(MyDrive mydrive) throws UserAlreadyExistsException {
	    super();
	    if (mydrive == null)
	      throw new ForbiddenMethodException("RootUser()");
	    init("root", "Super User", "***", (byte) (0b1111_1010));
	    // home is set in mydrive.setup() 
	    setMydrive(mydrive);
	  }
	  
	  @Override
	  protected boolean validPassword(String password) {
		  return true;
	  }
	  
	  @Override
	  public long timeout() {
		  return 600000;
	  }
	  
	  @Override
	  public void remove() {
		  throw new CannotRemoveSuperUserException();
	  }
}
