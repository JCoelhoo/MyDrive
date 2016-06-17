package pt.tecnico.mydrive.exception;

public class FileDoesNotExistException extends MyDriveException{


	private String _fileName;
	
	public FileDoesNotExistException(String filename) {
	_fileName = filename;
	}
	
	public String getFileName() {
		return _fileName;
	}
	  
	@Override
	public String getMessage() {
	  return "The file '" + _fileName + "' does not exist.";
	}

}
