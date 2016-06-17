package pt.tecnico.mydrive.exception;

public class InvalidLoginException extends MyDriveException {

  public InvalidLoginException() {}

  @Override
  public String getMessage() {
    return "Invalid login. Please try again.";
  }
}
