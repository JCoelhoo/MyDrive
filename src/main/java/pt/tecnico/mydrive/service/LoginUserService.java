package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exception.RequiredArgumentException;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
import pt.tecnico.mydrive.exception.InvalidLoginException;

public class LoginUserService extends MyDriveService {

    private String username;
    private String password;
    private long token;
    
    public LoginUserService(String username, String password) throws RequiredArgumentException, IllegalArgumentException {
    	  if(username == null || username.equals(""))
            throw new IllegalArgumentException("username");
        if(password == null)
            throw new IllegalArgumentException("password");
       

        this.username = username;
        this.password = password;
     
    }


    @Override
    public final void dispatch() {
        MyDrive mydrive = getMyDrive();
        this.token = (new Login(mydrive, username, password)).getToken();
    }
    
    public long result(){
        return token;
    }

    
}