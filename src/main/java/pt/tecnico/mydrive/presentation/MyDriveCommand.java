package pt.tecnico.mydrive.presentation;

import java.util.*;

import pt.tecnico.mydrive.service.LoginUserService;


public class MyDriveCommand extends Command {

	protected List<Long> tokenList = new ArrayList<Long>();
	protected String username;
	protected long currentToken;

	public MyDriveCommand(Shell sh, String n) {
		super(sh, n);
		LoginUserService service = new LoginUserService("nobody", "");
		username="nobody";
		service.execute();
		setToken(service.result());
	}

	public MyDriveCommand(Shell sh, String n, String h) {
		super(sh, n, h);
		LoginUserService service = new LoginUserService("nobody", "");
		username = "nobody";
		service.execute();
		setToken(service.result());
		shell().addToken(username, currentToken);
		shell().setToken(currentToken);
	}

	@Override
	void execute(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setToken(long token) {
		this.currentToken = token;
	}

	public void addTokenList(long token) {
		this.tokenList.add(token);

	}

	public long getTokenByUsername(String username) {
		// TODO
		return currentToken;

	}

	public String getUsername(long token) {
		return username;

	}

}
