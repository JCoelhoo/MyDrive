package pt.tecnico.mydrive.exception;

public class RequiredArgumentException extends MyDriveException {
  private String _errorMessage;

  public RequiredArgumentException(String errorMessage) {
    _errorMessage = errorMessage;
  }

  @Override
  public String getMessage() {
    return _errorMessage;
  }
}