package pt.tecnico.mydrive.exception;

public class EntryDoesNotExistException extends MyDriveException {

  private String _entryName;

  public EntryDoesNotExistException(String entryName) {
    _entryName = entryName;
  }

  public String getEntryName() {
    return _entryName;
  }

  @Override
  public String getMessage() {
    return "The file '" + _entryName + "' does not exist.";
  }
}
