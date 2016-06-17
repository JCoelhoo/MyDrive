package pt.tecnico.mydrive.exception;

public class CannotRemoveEntryException extends MyDriveException {
	public CannotRemoveEntryException() {}

	@Override
	public String getMessage() {

	  return "The file cannot be removed.";
	}
}
