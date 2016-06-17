package pt.tecnico.mydrive.exception;

public class InvalidTargetException extends MyDriveException {

  public InvalidTargetException() {}

  @Override
  public String getMessage() {
    return "Invalid Java name";
  }
}
