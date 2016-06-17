package pt.tecnico.mydrive.exception;

public class UserAlreadyExistsException extends MyDriveException {

  private String _conflictingName;

  public UserAlreadyExistsException(String conflictingName) {
    _conflictingName = conflictingName;
  }

  public String getConflictingName() {
    return _conflictingName;
  }

  @Override
  public String getMessage() {
    return "The name '" + _conflictingName + "' is already being used by another user.";
  }
}
