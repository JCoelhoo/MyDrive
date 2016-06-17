package pt.tecnico.mydrive.exception;

public class TokenDoesNotExistException extends MyDriveException {

  public TokenDoesNotExistException() {}

  @Override
  public String getMessage() {
    return "Invalid session. Please log in if you want to keep using the application.";
  }
}
