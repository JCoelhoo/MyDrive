package pt.tecnico.mydrive.exception;

public class CannotRemoveGuestUserException extends Exception {

	public CannotRemoveGuestUserException() {}

	  @Override
	  public String getMessage() {

	    return "Guest user cannot been removed.";
	  }

	
	
}
