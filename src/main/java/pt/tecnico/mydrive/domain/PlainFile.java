package pt.tecnico.mydrive.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import pt.tecnico.mydrive.exception.EntryAlreadyExistsException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import pt.tecnico.mydrive.exception.NotAnAppException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;

import org.joda.time.DateTime;

import org.jdom2.Document;
import org.jdom2.Element;
public class PlainFile extends PlainFile_Base {
    
  public PlainFile() {
    super();
  }
    
  public PlainFile(MyDrive mydrive, String name, byte perms, User owner, Directory parent, String content) throws EntryAlreadyExistsException {
    super();
    init(mydrive, name, perms, owner, parent, content);
  }
  
  protected void init(MyDrive mydrive, String name, byte perms, User owner, Directory parent, String content) throws EntryAlreadyExistsException {
    init(mydrive, name, perms, owner, parent);
    setContent(content);
  }

  public int getSize() {
	    return this.getName().length();
  }
  
  @Override
  public String getEntryType() {
	  return "PlainFile";
  }
  
  @Override
  public String read(User user) throws PermissionDeniedException {
    if (!hasPermission(user, 'r'))
      throw new PermissionDeniedException(user.getUsername(), 'r', getPath());
    return this.getContent();
  }

  @Override
  public void write(User user, String content) throws PermissionDeniedException {
	if (!hasPermission(user, 'w'))
    throw new PermissionDeniedException(user.getUsername(), 'w', getPath());
  this.setContent(content);
  this.setLastModified(new DateTime());
  }
  
  public void write(GuestUser user, String content) throws UnsupportedOperationException, PermissionDeniedException {
	  if(!(this.getOwner()==user))  throw new PermissionDeniedException(user.getUsername(), 'w', getPath());
	  if (!hasPermission(user, 'w'))
	  throw new PermissionDeniedException(user.getUsername(), 'w', getPath());
	  this.setContent(content);
	  this.setLastModified(new DateTime());
  }
  
 
  

  @Override
  public void execute (User user, String[] args) {
  	Entry app=null;
    String[] appArgs = null;

    if (!hasPermission(user, 'x'))
    throw new PermissionDeniedException(user.getUsername(), 'x', getPath());

    try{
      BufferedReader reader = new BufferedReader(new StringReader(getContent()));

      String line = null;
      while( (line = reader.readLine()) != null ) {
        if (line.contains(" ")){
          String path = line.substring(0, line.indexOf(" "));
          app = MyDrive.getInstance().getEntry(path, user, getParent());
         
          String substring = line.substring(line.indexOf(" ") + 1);
          appArgs = substring.split(" ");
        }
        else {
           app = MyDrive.getInstance().getEntry(line, user, getParent());
        }
        if(!(app instanceof App) && !(app instanceof Link))
          throw new NotAnAppException(app.getName());
        app.execute(user, appArgs);
      }
    }
    catch (EntryDoesNotExistException e) {
      throw new NotAnAppException(e.getEntryName());    } 
    catch (UnsupportedOperationException e) {}
    catch (IOException e) {}
    
  }

  // =======================
  // | XML IMPORT / EXPORT |
  // =======================

  public PlainFile(MyDrive mydrive, Element xml) throws ImportDocumentException {
    super();
    importXml(mydrive, xml);
  }

  public void importXml(MyDrive mydrive, Element dirElement) throws ImportDocumentException {
    String content = dirElement.getChildText("contents");
    if (content != null) 
      setContent(content);
    else 
      setContent("");
    super.importXml(mydrive, dirElement);

  }

  public Element exportXml(Element drive) {
	  Element plain = super.exportXml(drive);
	  plain.setName("plain");
	  plain.addContent((new Element("contents")).setText(getContent()));
	  
	  return plain;
  }
}
