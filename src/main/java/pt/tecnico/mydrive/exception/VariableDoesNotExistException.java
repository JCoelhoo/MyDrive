package pt.tecnico.mydrive.exception;

public class VariableDoesNotExistException extends MyDriveException {
	private String _varName;

	  public VariableDoesNotExistException(String varName) {
	    _varName = varName;
	  }

	  public String getVarName() {
	    return _varName;
	  }

	  @Override
	  public String getMessage() {
	    return "The environment variable '" + getVarName() + "' does not exist.";
	  }
}
