package pt.tecnico.mydrive.exception;

public class NotAnAppException extends MyDriveException {

  private String _appName;

  public NotAnAppException(String appName) {
    _appName = appName;
  }

  public String getappName() {
    return _appName;
  }

  @Override
  public String getMessage() {
    return "The file '" + _appName + "' does not exist.";
  }
}
