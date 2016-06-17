package pt.tecnico.mydrive.domain;

import java.util.Set;
import pt.tecnico.mydrive.exception.InvalidLoginException;
import pt.tecnico.mydrive.exception.InvalidPasswordException;
import pt.tecnico.mydrive.exception.UserDoesNotExistException;
import pt.tecnico.mydrive.exception.VariableDoesNotExistException;
import pt.tecnico.mydrive.exception.ForbiddenMethodException;
import pt.tecnico.mydrive.MyDriveTest;

public class Login extends Login_Base {
  
  public Login(MyDrive mydrive, String username, String password) {
    super();
    User user = getUser(mydrive, username, password);
    super.setTimeToLive(user.timeout());
    super.setUser(user);
    setCurrentDir(user.getHome());
    super.setToken(mydrive.getNewToken());
    super.setLastActivity(System.currentTimeMillis()); //milliseconds
    setMydrive(mydrive);

    //Verify all logins for validity
    mydrive.verifyLogins();
  }


  @Override
  public void setMydrive(MyDrive mydrive) {
    if (mydrive == null)
      super.setMydrive(null);
    else
      mydrive.addLogin(this);
  }

  private User getUser(MyDrive mydrive, String username, String password) {
    try {   
      User user = mydrive.getUserByUsername(username); 
      if (user.checkPassword(password)){
        return user;
      }
      throw new InvalidLoginException();
    }
    catch (UserDoesNotExistException e) { throw new InvalidLoginException(); }
  }
  

  

  public void setTimeToLive(MyDriveTest testInstance, long millisecs) {
    if (testInstance != null)
      super.setTimeToLive(millisecs);
    else {
      throw new ForbiddenMethodException("login.setTimeToLive");
    }
  }
  
  
  public void refresh() {
    super.setLastActivity(System.currentTimeMillis()); //milliseconds
  }

  public boolean isValid() {
	if( super.getGuest() == true){return true;}
    return ((System.currentTimeMillis() - getLastActivity()) < getTimeToLive());
  }

  public void addEnvVar(String name, String value) {
	EnvironmentVariable var;
    try {
    	var = getEnvVar(name);
    	var.setValue(value);
    } catch (VariableDoesNotExistException e) {
    	var = new EnvironmentVariable(name, value);
    	addEnvironmentVariable(var);
    }
  } 

  public String getEnvValue(String name) {
    return getEnvVar(name).getValue();
  }
  
  public EnvironmentVariable getEnvVar(String name) {
    for (EnvironmentVariable var: getEnvironmentVariableSet()){
      if (var.getName().equals(name))
        return var;
    }
    throw new VariableDoesNotExistException(name);
  }
  
  public void remove(){
    super.setUser(null);
    setMydrive(null);
    setCurrentDir(null);
    for (EnvironmentVariable var: getEnvironmentVariableSet())
      var.remove();
    
    deleteDomainObject();
  }


  // ======================
  // | DISABLED FUNCTIONS |
  // ======================
  @Override
  public void setUser(User user) {
    throw new ForbiddenMethodException("login.setUser");
  }
  @Override
  public void setToken(long token) {
    throw new ForbiddenMethodException("login.setToken");
  }
  @Override 
  public void setLastActivity(long lastActivity){
    throw new ForbiddenMethodException("login.setLastActivity");
  }
  @Override
  public void setTimeToLive(long timeToLive) {
    throw new ForbiddenMethodException("login.setTimeToLive");
  }
}
