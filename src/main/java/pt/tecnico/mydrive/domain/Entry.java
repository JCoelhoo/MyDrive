package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.EntryAlreadyExistsException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import pt.tecnico.mydrive.exception.InvalidNameException;
import pt.tecnico.mydrive.exception.UserDoesNotExistException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.ExceededPathLengthException;
import pt.tecnico.mydrive.exception.CannotRemoveRootDirectoryException;
import pt.tecnico.mydrive.exception.ForbiddenMethodException;



import java.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;


import org.jdom2.Element;

import java.lang.ClassCastException;


public abstract class Entry extends Entry_Base {

  public Entry() {
    super();
  }

  // BUILDS AN ENTRY GIVEN THE PARENT DIRECTORY 
  public Entry(MyDrive mydrive, String name, byte perms, User owner, Directory parent) throws EntryAlreadyExistsException {
    super();
    init(mydrive, name, perms, owner, parent);
  }

  // INIT FOR REGULAR ENTRIES
  protected void init(MyDrive mydrive, String name, byte perms, User owner, Directory parent) throws EntryAlreadyExistsException, ExceededPathLengthException {
    if (!validName(name))
    	throw new InvalidNameException(name);
    super.setName(name);
    super.setEntryID(mydrive.getNextID());
    super.setPermissions(perms);
    super.setOwner(owner);
    setLastModified(new DateTime());
    setParent(parent);
    if (!validPathLength(parent, name)) 
      throw new ExceededPathLengthException();    
  }
  
  // INIT FOR ROOT DIRECTORY
  protected void init(MyDrive mydrive, String name, byte perms, User owner) {
    if (mydrive.getRootDirectory() == null) {
    	super.setName(name);
    	super.setEntryID(mydrive.getNextID());
    	super.setPermissions(perms);
    	super.setOwner(owner);
    	setLastModified(new DateTime());
    	setParent((Directory) this);
    }
  }

  @Override
  public void setParent(Directory parent) throws EntryAlreadyExistsException {
    if (parent == null)
      super.setParent(null);
    else
      parent.addEntry(this, getOwner());
  }

  private boolean validPathLength (Directory parent, String name) {
    return ((parent.getPath().length() + name.length() + 1) <= 1024);
  }
  
  
  private boolean validName(String name) {
	  return !(name.contains("\0") || name.contains("/"));
  }

  public int getSize() throws UnsupportedOperationException {
    throw new UnsupportedOperationException("get the size of");
  }

  public void addEntry(Entry newEntry, User user) throws UnsupportedOperationException, EntryAlreadyExistsException, PermissionDeniedException{
    throw new UnsupportedOperationException("addEntry");
  }
  
  public Entry getEntry(String path, User user) throws UnsupportedOperationException, EntryDoesNotExistException, PermissionDeniedException {
	  throw new UnsupportedOperationException("getEntry");
  }
    
  public String read(User user) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("read");
  }

  public void write(User user, String input) throws UnsupportedOperationException, PermissionDeniedException {
    throw new UnsupportedOperationException("write");
  }
  

  public void execute(User user, String[] args) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("execute");
  }
      
  public String getPath() {
    if (getParent() == this)
      return "/";
    else if (getParent().getParent() == getParent())
      return getParent().getPath() + getName();
    else
      return getParent().getPath() + "/" + getName();  
  }

  public abstract String getEntryType();
  
  public String toString() {
    return getName();
  }
  
  // CONVERTS PERMISSIONS BINARY MASK TO STRING
  public String printPermissions() {
    String mask = Integer.toBinaryString(0xFF & getPermissions());
    mask = ("00000000" + mask).substring(mask.length());
    mask = mask.replace('0', '-');
    
    String perm = "rwxdrwxd";
    
    while (mask.contains("1"))
      mask = mask.replaceFirst("1", Character.toString(perm.charAt(mask.indexOf('1'))));
    
    return mask;
  }
  
  public boolean hasPermission(User user, char permission) {
    if (user.getUsername().equals("root"))
      return true;
    byte filePermissions = this.getPermissions();
    //byte userPermissions = user.getUmask();
    String permissionsChars = "rwxd";

    if (permissionsChars.indexOf(permission) == -1)
      return false;
    if (this.getOwner().equals(user)) {
      //if the user is the owner, look at the leftmost permission bits
      filePermissions = (byte) (filePermissions >> 4);
      //userPermissions = (byte) (userPermissions >> 4);
    }

    return (((filePermissions /*& userPermissions*/) >> (3 - permissionsChars.indexOf(permission))) & 0x01) != 0;
  }

  public boolean hasPermission(RootUser root, char permission) {
	  return true;
  }
  
  public void remove(User user) throws CannotRemoveRootDirectoryException, PermissionDeniedException {

	  if (this == getParent())
      throw new CannotRemoveRootDirectoryException();

    if (!hasPermission(user, 'd'))
      throw new PermissionDeniedException(user.getUsername(), 'd', getPath());

    if (!getParent().hasPermission(user, 'w')) 
      throw new PermissionDeniedException(user.getUsername(), 'w', getParent().getPath());

    super.setParent(null);
    super.setOwner(null);
    deleteDomainObject();
  }
  
  public void remove(GuestUser user) throws CannotRemoveRootDirectoryException, PermissionDeniedException {
	 if (!(this.getOwner()==user)) throw new PermissionDeniedException(user.getUsername(), 'd', getPath());
  }
  
  public void write(GuestUser user, String input) throws UnsupportedOperationException, PermissionDeniedException {
	  if(!(this.getOwner()==user))  throw new PermissionDeniedException(user.getUsername(), 'w', getPath());
  }


  // =======================
  // | XML IMPORT / EXPORT |
  // =======================

  public Entry(MyDrive mydrive, Element xml) throws ImportDocumentException {
    super();
    importXml(mydrive, xml);
  }

  public Entry getEntry(String path) throws UnsupportedOperationException, EntryDoesNotExistException {
    throw new UnsupportedOperationException("getEntry");
  }

  public void importXml(MyDrive mydrive, Element entryElement) throws ImportDocumentException {
    try {
      String path = entryElement.getChildText("path");
      String name = entryElement.getChildText("name");
      String owner = entryElement.getChildText("owner");
      String mask = entryElement.getChildText("perm");

      if (owner == null)
        throw new ImportDocumentException("Import: File owner not specified.");
      if (path == null)
        throw new ImportDocumentException("Import: File path not specified.");
      if (name == null)
        throw new ImportDocumentException("Import: File name not specified.");      

      super.setName(name);
      super.setOwner(mydrive.getUserByUsername(owner));

      if (mask != null) { 
        //check if mask string is valid and transform it into byte
        if (!mask.matches("(r|-)(w|-)(x|-)(d|-)(r|-)(w|-)(x|-)(d|-)")) {
          throw  new ImportDocumentException("Import: Invalid file permissions.");
        } else {
          super.setPermissions((byte) Integer.parseInt(mask.replaceAll("[^-]", "1").replaceAll("-", "0"), 2));
        }   
      } else {
        super.setPermissions(getOwner().getUmask());
      }

      //try to find parent directory
      if (path.equals(""))
        throw new ImportDocumentException("Import: Invalid file path.");
      Directory parent = mydrive.createPath(path);
      
      
      try {
        super.setParent(parent);
      } catch (EntryAlreadyExistsException e) {
        //already exists!!
        MyDrive.log.trace("Trying to import entry '" + name + "' at " + path + " that already exists! Aborting entry import...");
        remove(mydrive.getUserByUsername("root"));
        return;
      }
      
      super.setEntryID(mydrive.getNextID());
      MyDrive.log.trace("Sucessfully imported entry '" + name + "' at " + path);

    } 
    catch (ClassCastException | UserDoesNotExistException e) {
      throw new ImportDocumentException("Import: User does not exist.");
    }
    catch (PermissionDeniedException e) {
      throw new ImportDocumentException("Import: There isn't a root user or root does not have the correct permissions.");
    }
  }
  
  public Element exportXml(Element drive) {
    Element entry = new Element("entry");
    entry.setAttribute("id", Integer.toString(getEntryID()));
    
    entry.addContent((new Element("path")).setText(getParent().getPath()));
    entry.addContent((new Element("name")).setText(getName()));
    entry.addContent((new Element("owner")).setText(getOwner().getUsername()));
    entry.addContent((new Element("perm")).setText(printPermissions()));    
    
    return entry;
  }


  // ======================
  // | DISABLED FUNCTIONS |
  // ======================
  @Override
  public void setName(String name) {
    throw new ForbiddenMethodException("entry.setName");
  }
  @Override
  public void setEntryID(int id) {
    throw new ForbiddenMethodException("entry.setEntryID");
  }
  @Override
  public void setPermissions(byte permissions) {
    throw new ForbiddenMethodException("entry.setPermissions");
  }
  


}