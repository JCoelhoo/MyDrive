package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.exception.IllegalArgumentException;

public class CreateFileService extends MyDriveService {

  private String name;
  private long token;
  private String type;
  private String content;

  public CreateFileService(long token, String name, String type, String content) throws IllegalArgumentException {
    if (name == null || name == "")
      throw new IllegalArgumentException("name");
    if (content == null)
      throw new IllegalArgumentException("content");
    if (type == null)
      throw new IllegalArgumentException("type");
    if (type == "Directory")
      throw new IllegalArgumentException("content");
      init(token, name, type, content);
  }

  public CreateFileService(long token, String name, String type) throws RequiredArgumentException {
    if (name == null || name == "")
      throw new IllegalArgumentException("name");
    if (type == null)
      throw new IllegalArgumentException("type");
    if (type == "Link")
      throw new IllegalArgumentException("content");
      init(token, name, type, null);
  }

  private void init(long token, String name, String type, String content) {
    this.name = name;
    this.token = token;
    this.type = type;
    this.content = content;
  }

  @Override
  public final void dispatch() throws EntryAlreadyExistsException, PermissionDeniedException, FileTypeDoesNotExistException {
   	MyDrive mydrive = getMyDrive();
    Login login = mydrive.getLoginByToken(token);

    switch (type) {
      case "PlainFile": 
        new PlainFile(mydrive, name, login.getUser().getUmask(), 
            login.getUser(), login.getCurrentDir(), content); 
        break;
      case "Link":
        new Link(mydrive, name, login.getUser().getUmask(), 
            login.getUser(), login.getCurrentDir(), content); 
        break;
      case "App":
        new App(mydrive, name, login.getUser().getUmask(), 
            login.getUser(), login.getCurrentDir(), content); 
        break;
      case "Directory":
        new Directory(mydrive, name, login.getUser().getUmask(), 
            login.getUser(), login.getCurrentDir()); 
        break;
      default:
        throw new FileTypeDoesNotExistException(type);
    }
 
  }
}