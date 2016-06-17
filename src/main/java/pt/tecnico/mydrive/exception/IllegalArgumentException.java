package pt.tecnico.mydrive.exception;
 
public class IllegalArgumentException extends MyDriveException {

  private String _illegalArgument;
 
  public IllegalArgumentException(String illegalArgument) {
    _illegalArgument = illegalArgument;
  }
 
  public String getIllegalArgument() {
    return _illegalArgument;
  }
 
  @Override
  public String getMessage() {
    return "The argument '" + _illegalArgument + "' is illegal. Too many, too few or null arguments";
  }
}

