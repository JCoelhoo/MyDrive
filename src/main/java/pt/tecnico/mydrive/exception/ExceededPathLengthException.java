package pt.tecnico.mydrive.exception;

public class ExceededPathLengthException extends MyDriveException {

  public ExceededPathLengthException() {}


  @Override
  public String getMessage() {
    return "The path is invalid. Only 1024 characters are are allowed.";
  }
}
