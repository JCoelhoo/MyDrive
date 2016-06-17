package pt.tecnico.mydrive.exception;

public class NotADirectoryException extends MyDriveException {

  public NotADirectoryException() {}

  @Override
  public String getMessage() {
    return "The specified file is not a directory.";
  }

}
