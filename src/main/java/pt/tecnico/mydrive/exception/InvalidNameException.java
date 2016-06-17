package pt.tecnico.mydrive.exception;

public class InvalidNameException extends MyDriveException {

  private String _invalidUsername;

  public InvalidNameException(String invalidUsername) {
    _invalidUsername = invalidUsername;
  }

  public String getInvalidUsername() {
    return _invalidUsername;
  }

  @Override
  public String getMessage() {
    return "The username '" + _invalidUsername + "' is invalid. Only letters and decimal digits are allowed.";
  }
}
