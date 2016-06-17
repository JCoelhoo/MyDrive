package pt.tecnico.mydrive.exception;

public class MethodDoesNotExistException extends MyDriveException {

  private String _methodName;

  public MethodDoesNotExistException(String methodName) {
    _methodName = methodName;
  }

  public String getMethodName() {
    return _methodName;
  }

  @Override
  public String getMessage() {
    return "The file '" + _methodName + "' does not exist.";
  }
}
