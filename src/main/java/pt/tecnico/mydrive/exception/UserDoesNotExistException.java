package pt.tecnico.mydrive.exception;

public class UserDoesNotExistException extends MyDriveException {

  private String _username;

  public UserDoesNotExistException(String username) {
    _username = username;
  }

  public String getUsername() {
    return _username;
  }

  @Override
  public String getMessage() {
    return "The user '" + _username + "' does not exist.";
  }
}
