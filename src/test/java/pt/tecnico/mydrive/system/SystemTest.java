package pt.tecnico.mydrive.system;

import org.junit.Test;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.presentation.*;
import pt.tecnico.mydrive.service.AbstractServiceTest;

public class SystemTest extends AbstractServiceTest{
    private MyDriveShell sh;
    private MyDrive myDrive;
	private Directory dir;
	private User root;

    protected void populate() {
        sh = new MyDriveShell();
		myDrive = MyDrive.getInstance();
		root = myDrive.getUserByUsername("root");
		dir = root.getHome();
		User user = new User(myDrive,  "joao", "joao", "joao123123", (byte) (0b1111_1111), root);
		new PlainFile(myDrive, "plainFile", (byte) (0b1111_1111), user, user.getHome(), "");
    }

    @Test
    public void success() {
    	new LoginCommand(sh).execute(new String[] { "root", "***" }) ;
        new ChangeWorkingDirectoryCommand(sh).execute(new String[] { "/home" } );
        new EnvironmentCommand(sh).execute(new String[] { "env", "var" } );
        new ExecuteCommand(sh).execute(new String[] { "/home/root/planFile" } );
        new ListCommand(sh).execute(new String[] { "/home" } );
        new WriteCommand(sh).execute(new String[] { "/home/joao/plainFile", "hey" } );
    	new KeyCommand(sh).execute(new String[] { "nobody" } );
    }
}
