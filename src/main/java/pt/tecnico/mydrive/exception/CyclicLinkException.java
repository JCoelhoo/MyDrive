package pt.tecnico.mydrive.exception;

public class CyclicLinkException extends MyDriveException {

  public CyclicLinkException() {}

  @Override
  public String getMessage() {
    return "There is a cyclic Link. Could not resolve pathname.";
  }
}
