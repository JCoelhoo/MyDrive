package pt.tecnico.mydrive.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import mockit.integration.junit4.JMockit;

import java.io.File;

import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;


public class IntegrationTest extends AbstractServiceTest {
    long token = 0; 
    static boolean executed = false;

    protected void populate() {
   
    }

    public static void test(String[] args) {
        executed = true;
    }



    @Test
    public void success() throws Exception {
        LoginUserService loginService = new LoginUserService("root", "***");
        loginService.execute();
        token = loginService.result();
        assertTrue(token != 0);

        ChangeDirectoryService changeDirService = new ChangeDirectoryService(token, "/home");
        changeDirService.execute();
        assertTrue((changeDirService.result()).equals("/home"));

        ListDirectoryService listDirService = new ListDirectoryService(token);
        listDirService.execute();
        assertEquals((listDirService.result()).size(), 4);

        CreateFileService createFileService = new CreateFileService(token, "testFile", "PlainFile", "Integration test");
        createFileService.execute();
        ListDirectoryService listDirService2 = new ListDirectoryService(token);
        listDirService2.execute();
        assertEquals((listDirService2.result()).size(), 5);

        ReadFileService readFileService = new ReadFileService(token, "testFile");
        readFileService.execute();
        assertTrue((readFileService.result()).equals("Integration test"));

        WriteFileService writeFileService = new WriteFileService(token, "testFile", "Changed");
        writeFileService.execute();
        ReadFileService readFileService2 = new ReadFileService(token, "testFile");
        readFileService2.execute();
        assertTrue((readFileService2.result()).equals("Changed"));

        DeleteFileService deleteFileService = new DeleteFileService(token, "testFile");
        deleteFileService.execute();
        ListDirectoryService listDirService3 = new ListDirectoryService(token);
        listDirService3.execute();
        assertEquals((listDirService3.result()).size(), 4);


        CreateFileService createFileService2 = new CreateFileService(token, "testApp", "App", "pt.tecnico.mydrive.system.IntegrationTest.test");
        createFileService2.execute();
        ExecuteFileService executeService = new ExecuteFileService(token, "testApp");
        executeService.execute();
        assertTrue(executed);


        AddEnvVariableService addEnvVarservice = new AddEnvVariableService(token, "Env", "Var");
        addEnvVarservice.execute();
        assertEquals(addEnvVarservice.result().get(0).getName(), "Env");
        assertEquals(addEnvVarservice.result().get(0).getValue(), "Var");
        assertEquals(addEnvVarservice.result().size(), 1);       

    }
}
