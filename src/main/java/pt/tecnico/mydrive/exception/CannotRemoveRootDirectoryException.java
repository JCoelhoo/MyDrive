package pt.tecnico.mydrive.exception;

public class CannotRemoveRootDirectoryException extends MyDriveException {

  public CannotRemoveRootDirectoryException() {}

  @Override
  public String getMessage() {

    return "Root directory cannot bee removed.";
  }

}