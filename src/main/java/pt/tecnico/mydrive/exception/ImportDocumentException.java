package pt.tecnico.mydrive.exception;

public class ImportDocumentException extends MyDriveException {
     private String _errorMessage;

    public ImportDocumentException(String errorMessage) {
        _errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return _errorMessage;
    }
}