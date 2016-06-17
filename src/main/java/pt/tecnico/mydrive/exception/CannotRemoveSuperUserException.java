package pt.tecnico.mydrive.exception;

public class CannotRemoveSuperUserException extends MyDriveException {

  public CannotRemoveSuperUserException() {}

  @Override
  public String getMessage() {

    return "Root user cannot been removed.";
  }

}