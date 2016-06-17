package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.security.KeyStore.Entry;

import javax.swing.text.Document;

import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.presentation.Sys;

public class ImportMyDriveTest extends AbstractServiceTest {
	// Fixed null exception; was not extending AbstractServiceTest!
	MyDrive md = null;
	
	protected void populate() {
		md = MyDrive.getInstance();
	}

	@Test
	public void success() throws Exception {
		ImportXMLService service = new ImportXMLService("src/main/resources/drive.xml");
		service.execute();
		
		User user = md.getUserByUsername("jtb");
		Directory directory = md.getDirectory(".", user, user.getHome());
		assertEquals("Created 2 Users", 4, md.getUserSet().size());
		assertTrue("Created jtb", md.hasUser("jtb"));
		assertTrue("Created mja", md.hasUser("mja"));
		assertEquals("jtb has 6 Entries", 6, directory.getSize());
		assertEquals(7, directory.getEntryByName("bin", user).getEntryID());
		assertEquals("Primeiro chefe de Estado do regime republicano (acumulando com a chefia do governo), "
				+ "numa capacidade provisória até à eleição do primeiro presidente da República."
				+ "", directory.getEntryByName("profile", user).read(user));
	}

}
