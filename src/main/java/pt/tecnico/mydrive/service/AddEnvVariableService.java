package pt.tecnico.mydrive.service;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.mydrive.domain.EnvironmentVariable;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.exception.IllegalArgumentException;
import pt.tecnico.mydrive.exception.MyDriveException;
import pt.tecnico.mydrive.service.dto.EnvVarDto;

public class AddEnvVariableService extends MyDriveService{
	private long token;
	private String name;
	private String value;
	private List<EnvVarDto> varlist;
	
	public AddEnvVariableService(long token, String name, String value) {
		this.token=token;
		this.name=name;
		this.value=value;
		varlist = new ArrayList<EnvVarDto>();
	}

	public AddEnvVariableService(long currentToken) {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void dispatch() throws MyDriveException {
		if (name==null ^ value==null)
			throw new IllegalArgumentException("Variable name and value cannot be null");
		
		MyDrive mydrive = getMyDrive();
        Login login = mydrive.getLoginByToken(token);
        if (name!=null && value!=null)	
        	login.addEnvVar(name, value);
		for (EnvironmentVariable e : login.getEnvironmentVariableSet()) {
    		varlist.add(new EnvVarDto(e.getName(), e.getValue()));
    	}		
	}

	public List<EnvVarDto> result() {
		return varlist;
	}

}
