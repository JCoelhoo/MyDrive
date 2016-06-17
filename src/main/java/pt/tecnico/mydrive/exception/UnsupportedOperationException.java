package pt.tecnico.mydrive.exception;

public class UnsupportedOperationException extends MyDriveException {

  String _operation;

  public UnsupportedOperationException(String operation) {
    _operation = operation;
  }

  public String getOperation(){
    return _operation;
  }

  @Override
  public String getMessage() {
    return "Not possible to " + _operation + " the specified file.";
  }

}
