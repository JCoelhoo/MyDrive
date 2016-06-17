package pt.tecnico.mydrive.domain;

import java.lang.reflect.Method;

import pt.tecnico.mydrive.exception.EntryAlreadyExistsException;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import pt.tecnico.mydrive.exception.InvalidTargetException;
import pt.tecnico.mydrive.exception.MethodDoesNotExistException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import java.lang.ClassNotFoundException;

import org.jdom2.Document;
import org.jdom2.Element;
public class App extends App_Base {
    
  public App() {
    super();
  }

  public App(MyDrive mydrive, String name, byte perms, User owner, Directory parent, String target) throws EntryAlreadyExistsException {
    super();
    if (!validTarget(target))
      throw new InvalidTargetException();
    init(mydrive, name, perms, owner, parent, target);
  }

  private boolean validTarget(String target) {
    return ((target.length() >= 3) && (target.contains(".")) );
  }

  public int getSize() {
	  return this.getName().length();
  }

  
  @Override
  public String getEntryType() {
	  return "App";
  }

  @Override
  public void execute (User user, String[] args) {
    String className = null;
    String methodName = null;
    Class cls = null;
    if (!hasPermission(user, 'x'))
      throw new PermissionDeniedException(user.getUsername(), 'x', getPath());
    try {
      String content = getContent();
      className = content.substring(0, content.lastIndexOf("."));
      methodName = content.substring(content.lastIndexOf(".") + 1);
      try {
        cls = Class.forName(className);
      }
      catch (ClassNotFoundException e) {
        className = content;
        methodName = "main";
        cls = Class.forName(className);
      }
      Class[] argType = new Class[] { String[].class };
      Method method = cls.getDeclaredMethod(methodName, argType);
      method.invoke(null, (Object) args);  // no class instance needed for invoking static methods

    }
    catch(NoSuchMethodException ex) {
      throw new MethodDoesNotExistException(methodName);
    } 
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  // =======================
  // | XML IMPORT / EXPORT |
  // =======================

  public App(MyDrive mydrive, Element xml) throws ImportDocumentException {
    super();
    importXml(mydrive, xml);
  }

  public void importXml(MyDrive mydrive, Element dirElement) throws ImportDocumentException {    
    String content = dirElement.getChildText("method");
    if (content != null) 
      setContent(content);
    super.importXml(mydrive, dirElement);
  }

  public Element exportXml(Element drive) {
	  Element app = super.exportXml(drive);
	  app.setName("app");
	  app.getChild("contents").setName("method");
	  
	  return app;
  }
}
