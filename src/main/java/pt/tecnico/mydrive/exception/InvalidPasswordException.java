package pt.tecnico.mydrive.exception;

public class InvalidPasswordException extends MyDriveException {

  public InvalidPasswordException() {}

  @Override
  public String getMessage() {
    return "The password must be at least 8 characters long";
  }
}
