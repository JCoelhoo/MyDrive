package pt.tecnico.mydrive.exception;
 
public class ForbiddenMethodException extends MyDriveException {

  private String _method;
 
  public ForbiddenMethodException(String method) {
    _method = method;
  }
 
  public String getMethod() {
    return _method;
  }
 
  @Override
  public String getMessage() {
    return "The method '" + _method + "' is forbidden.";
  }
}

