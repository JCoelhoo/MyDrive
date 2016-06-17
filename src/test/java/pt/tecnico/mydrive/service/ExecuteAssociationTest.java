package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.service.ExecuteAssociationService;


@RunWith(JMockit.class)
public class ExecuteAssociationTest extends AbstractServiceTest{
	
	private static final String filename = "myfile";
	private static final String extension = "txt";

    @Mocked
    private MyDrive mydrive;

    protected void populate() {
    	mydrive = MyDrive.getInstance();
    }

    @Test
    public void success() {
	new Expectations() {
            {
            	mydrive.getExtensionByFilename(filename);
                result = extension;
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(filename);
        service.execute();
        assertEquals(service.result(), extension);
    }
    

    @Test
    public void methodIsCalled() {
    	ExecuteAssociationService service = new ExecuteAssociationService(filename);
        service.execute();
	new Verifications() {
            {
                mydrive.getExtensionByFilename(filename);
            }
        };
    }
    
    @Test(expected =FileDoesNotExistException.class)
    public void getExtensionNonExistingFile() throws FileDoesNotExistException {
	new Expectations() {
            {
                mydrive.getExtensionByFilename(filename);
                result = new FileDoesNotExistException(filename);
            }
        };
        ExecuteAssociationService service = new ExecuteAssociationService(filename);
        service.execute();
    }

}
