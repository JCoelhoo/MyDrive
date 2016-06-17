package pt.tecnico.mydrive.service;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.mydrive.Main;
import pt.tecnico.mydrive.MyDriveTest;


public abstract class AbstractServiceTest  extends MyDriveTest {
	
	@Before
	public void setUp() throws Exception {
		try {
			FenixFramework.getTransactionManager().begin(false);
			populate();
		} catch (WriteOnReadError | NotSupportedException | SystemException e1) {
			e1.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		try {
			FenixFramework.getTransactionManager().rollback();
		} catch (IllegalStateException | SecurityException | SystemException e) {
			e.printStackTrace();
		}
	}

	protected void populate() {
		// transaction based setUp
	}

	protected static void test() {}
}