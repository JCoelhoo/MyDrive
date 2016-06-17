package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import java.util.Set;

import pt.tecnico.mydrive.exception.UserAlreadyExistsException;
import pt.tecnico.mydrive.exception.EntryAlreadyExistsException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import pt.tecnico.mydrive.exception.InvalidNameException;
import pt.tecnico.mydrive.exception.InvalidPasswordException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.CannotRemoveGuestUserException;
import pt.tecnico.mydrive.exception.CannotRemoveSuperUserException;
import pt.tecnico.mydrive.exception.ForbiddenMethodException;

public class User extends User_Base {
  public User() {
    super();
  }

  public User(MyDrive mydrive,  String name, String username, String password, byte umask, Directory home) throws UserAlreadyExistsException, InvalidNameException{
    super();
    init(username, name, password, umask);
    super.setHome(home);
    setMydrive(mydrive);
  }
  
  //user with home at /home/username
  public User(MyDrive mydrive,  String name, String username, String password, byte umask, User user) throws UserAlreadyExistsException, InvalidNameException, PermissionDeniedException {
    super();
    init(username, name, password, umask);
    
    try {
      Directory home = new Directory(mydrive, username, umask, this, (Directory)mydrive.getRootDirectory().getEntryByName("home", user));
      super.setHome(home);
      
    } catch (EntryDoesNotExistException e)  {
      Directory home = new Directory (mydrive, "home", (byte) (0b1111_1110), this, mydrive.getRootDirectory());
      super.setHome(home);
    }
    catch (EntryAlreadyExistsException | ClassCastException e){
      throw new UserAlreadyExistsException(username);
    }

    setMydrive(mydrive);
  }

  protected void init(String username, String name, String password, byte umask) {
    if (!validUsername(username)) throw new InvalidNameException(username);
    if (!validPassword(password)) throw new InvalidPasswordException();
    super.setUsername(username);
    super.setName(name);
    super.setPassword(password);
    super.setUmask(umask);
  }
  

  //default values
  public User(MyDrive mydrive, String username, User user) throws UserAlreadyExistsException {
    this(mydrive, username, username, username, (byte) (0b1111_0000), user);
  }

  
  //ROOT USER
  public User (MyDrive mydrive) throws UserAlreadyExistsException {
    super();
    if (mydrive == null)
      throw new ForbiddenMethodException("user.newRootUser()");
    super.setUsername("root");
    super.setName("Super User");
    super.setPassword("***");
    super.setUmask((byte) (0b1111_1010));
    // home is set in mydrive.setup() 
    setMydrive(mydrive);
  }

  @Override
  public void setMydrive(MyDrive mydrive) {
    if (mydrive == null)
      super.setMydrive(null);
    else
      mydrive.addUser(this);
  }

  private boolean validUsername(String username){
    if(username.length() < 3)
      return false;
    for (char c : username.toCharArray())
      if (!Character.isDigit(c) && !Character.isLetter(c))
        return false;  
    return true;  
  }

  protected boolean validPassword(String password) {
	  return (password.length() >= 8);
  }
  
  public boolean checkPassword(String password) {
    //if (password.length() < 8) throw new InvalidPasswordException();
    return super.getPassword().equals(password);
  }

  public String printPermissions() {
  	String mask = Integer.toBinaryString(0xFF & getUmask());
	  	mask = ("00000000" + mask).substring(mask.length());
	  	mask = mask.replace('0', '-');
	  
	  	String perm = "rwxdrwxd";
	  
	  	while (mask.contains("1"))
	  		mask = mask.replaceFirst("1", Character.toString(perm.charAt(mask.indexOf('1'))));
	  	return mask;
  }

  public long timeout() {
	  return 7200000;
  }
  
   public void remove() throws CannotRemoveSuperUserException, CannotRemoveGuestUserException {
    if (getUsername().equals("root"))
      throw new CannotRemoveSuperUserException();
    //home.remove(); maybe??
    setMydrive(null);
    setHome(null);
    deleteDomainObject();
  }

  // =======================
  // | XML IMPORT / EXPORT |
  // =======================

  public User(MyDrive mydrive, Element xml) throws ImportDocumentException {
    super();
    importXml(mydrive, xml);
  }

  public void importXml(MyDrive mydrive, Element userElement) throws ImportDocumentException {
    try {
      String homePath = userElement.getChildText("home");
      String mask = userElement.getChildText("mask");
      String name = userElement.getChildText("name");
      String password = userElement.getChildText("password");
      String username = userElement.getAttributeValue("username");

      if (password.length() < 8) {
        throw new ImportDocumentException("Import: Invalid password, must be longer than 8 characters.");
      }
      
      if (username != null && validUsername(username)) {
        super.setUsername(username);
      } else {
        throw new ImportDocumentException("Import: Invalid username.");
      }
      if (name != null) {
        super.setName(name);
      } else {
        super.setName(username);
      }

      if (password != null) {
        super.setPassword(password);
      } else {
        super.setPassword(username);
      }

      if (mask != null) {
        //check if mask string is valid and transform it into byte
        if (!mask.matches("(r|-)(w|-)(x|-)(d|-)(r|-)(w|-)(x|-)(d|-)")) {
          throw  new ImportDocumentException("Import: Invalid user mask.");
        } else {
          super.setUmask((byte) Integer.parseInt(mask.replaceAll("[^-]", "1").replaceAll("-", "0"), 2));
        }   
      } else {
          super.setUmask((byte) (0b1111_1010));
      }


      mydrive.addUser(this);      
      if (homePath == null) {
          homePath = "/home/" + username;
      }
  
      super.setHome(mydrive.createPath(homePath));

      mydrive.log.trace("Sucessfully imported user '" + username + "'");

    } 
    catch (ClassCastException | UserAlreadyExistsException e) {
      throw new ImportDocumentException("Import: Duplicated user.");
    }
  }
  
  public Element exportXml() {
	  Element user = new Element("user");
	  user.setAttribute("username", getUsername());
	  
	  user.addContent((new Element("password")).setText(getPassword()));
	  user.addContent((new Element("name")).setText(getName()));
	  user.addContent((new Element("home")).setText(getHome().getPath()));
	  user.addContent((new Element("mask")).setText(printPermissions()));	  
	  
	  return user;
  }

  // ======================
  // | DISABLED FUNCTIONS |
  // ======================
  @Override 
  public void setPassword(String password) {
    throw new ForbiddenMethodException("user.setPassword");
  }
  @Override 
  public String getPassword() {
    throw new ForbiddenMethodException("user.getPassword");
  }
  @Override 
  public void setUsername(String username) {
    throw new ForbiddenMethodException("user.setUsername");
  }
  @Override 
  public void setName(String name) {
    throw new ForbiddenMethodException("user.setName");
  }
  @Override 
  public void setUmask(byte umask) {
    throw new ForbiddenMethodException("user.setUmask");
  }
  @Override
  public Set<Login> getLoginSet() {
    throw new ForbiddenMethodException("user.getLoginSet");
  }

}
