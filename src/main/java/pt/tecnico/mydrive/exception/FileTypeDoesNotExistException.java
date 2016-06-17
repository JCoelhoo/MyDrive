package pt.tecnico.mydrive.exception;

public class FileTypeDoesNotExistException extends MyDriveException {

  private String _fileType;

  public FileTypeDoesNotExistException(String FileTypeName) {
    _fileType = FileTypeName;
  }

  public String getFileType() {
    return _fileType;
  }

  @Override
  public String getMessage() {
    return "The file type '" + _fileType + "' does not exist.";
  }
}
