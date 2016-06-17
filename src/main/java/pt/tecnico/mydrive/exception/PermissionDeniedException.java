package pt.tecnico.mydrive.exception;

public class PermissionDeniedException extends MyDriveException {

  private String _username;
  private char _permission;
  private String _path;

  public PermissionDeniedException(String username, char permission, String path) {
    _username = username;
    _permission = permission;
    _path = path;
  }

  public String getUsername() {
    return _username;
  }
  public char getPermission() {
    return _permission;
  }
  public String getPath() {
    return _path;
  }
  @Override
  public String getMessage() {
    String permissionString = "";
    switch (_permission) {
      case 'r': permissionString = "read"; break;
      case 'w': permissionString = "write"; break;
      case 'x': permissionString = "execute"; break;
      case 'd': permissionString = "delete"; break;
    }
    return "The user " + _username + " has no permission to " + permissionString + " at: " + _path;
  }
}
