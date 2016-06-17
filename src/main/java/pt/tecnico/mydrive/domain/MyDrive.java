package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import org.jdom2.Document;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.ojb.broker.util.logging.LoggingConfiguration;

import pt.ist.fenixframework.FenixFramework;


import pt.tecnico.mydrive.exception.*;

public class MyDrive extends MyDrive_Base {

	static final Logger log = LogManager.getRootLogger();

	public static MyDrive getInstance() {
		MyDrive drive = FenixFramework.getDomainRoot().getMydrive();
		if (drive != null) {
			return drive;
		}

		log.trace("new drive");
		return new MyDrive();
	}

	private MyDrive() {
		super.setDomainRoot(FenixFramework.getDomainRoot());
		super.setEntryCounter(0);
		log.trace("setting up...");
		setup();
	}

	private void setup() {
		try {
			RootUser root = new RootUser(this);
			log.trace("Created root user successfully!");

			Directory rootDirectory = new Directory(this, "/", (byte) (0b1111_1010), root);
			super.setRootDirectory(rootDirectory);
			log.trace("Created root directory successfully!");

			Directory home = new Directory(this, "home", (byte) (0b1111_1110), root, rootDirectory);
			log.trace("Created home directory successfully!");

			root.setHome(new Directory(this, "root", (byte) (0b1111_1010), root, home));
			log.trace("Created root home directory successfully!");

			// Guest
			GuestUser guestUser = new GuestUser(this);
			log.trace("Created guest user successfully!");

			Directory guestHome = new Directory(this, "guest", (byte) (0b1111_1010), guestUser, home);
			guestUser.setHome(guestHome);
			log.trace("Created guest home directory successfully!");
			
			//new User(this, "hugo12345", root);

		} catch (MyDriveException e) {
			log.debug("Could not create instance");
			log.debug(e.getMessage());
			throw e;
		}
	}

	// ====================================
	// | KEEP TRACK OF UNIQUE IDENTIFIERS |
	// ====================================
	protected int getNextID() {
		super.setEntryCounter(super.getEntryCounter() + 1);
		return super.getEntryCounter() - 1;
	}

	protected long getNewToken() {
		long token = new BigInteger(64, new Random()).longValue();
		while (hasLogin(token) == true) {
			token = new BigInteger(64, new Random()).longValue();
		}
		return token;
	}

	// ================================
	// | METHODS USED TO MANAGE USERS |
	// ================================
	@Override
	public void addUser(User newUser) {
		if (hasUser(newUser.getUsername()))
			throw new UserAlreadyExistsException(newUser.getUsername());

		super.addUser(newUser);
		log.trace("Created user: " + newUser.getUsername());
	}

	public User getUserByUsername(String username) throws UserDoesNotExistException {
		for (User user : getUserSet()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		throw new UserDoesNotExistException(username);
	}

	public boolean hasUser(String username) {
		for (User user : getUserSet()) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	// =================================
	// | METHODS USED TO MANAGE LOGINS |
	// =================================
	public Login getLoginByToken(long token) throws TokenDoesNotExistException {
		for (Login login : super.getLoginSet()) {
			if (login.getToken() == token)
				if (login.isValid()) {
					login.refresh();
					return login;
				} else {
					login.remove();
					log.warn("Invalid token!");
					throw new TokenDoesNotExistException();
				}
		}
		log.warn("Invalid token!");
		throw new TokenDoesNotExistException();
	}

	public boolean hasLogin(long token) {
		for (Login login : super.getLoginSet()) {
			if (login.getToken() == token)
				return true;
		}
		return false;
	}

	public void verifyLogins() {
		for (Login login : super.getLoginSet()) {
			if (!login.isValid())
				login.remove();
		}
	}

	// USED FOR TESTS ONLY
	public void changeCurrentDir(String path, long token) {
		Login login = getLoginByToken(token);
		Directory dir = getDirectory(path, login.getUser(), login.getCurrentDir());
		login.setCurrentDir(dir);
	}

	// =================
	// | PATH TO ENTRY |
	// =================
	public Entry getEntry(String path, User user, Directory currentDir) {
		if (path.equals("/")) {
			return getRootDirectory();
		} else if (!path.substring(0, 1).equals("/")) {
			return currentDir.getEntry(path, user);
		} else {
			return getRootDirectory().getEntry(path.substring(1), user);
		}
	}

	public Directory getDirectory(String path, User user, Directory currentDir) {
		Entry entry = getEntry(path, user, currentDir);
		if (entry instanceof Link)
			entry = ((Link) entry).getTarget(user);
		if (!(entry instanceof Directory))
			throw new NotADirectoryException();
		return (Directory) entry;
	}

	// =======================
	// | XML IMPORT / EXPORT |
	// =======================

	protected Directory createPath(String path) {
		Directory dir;
		User root = getUserByUsername("root");
		try {
			dir = (Directory) getEntry(path, root, null);
		} catch (EntryDoesNotExistException e) {
			Directory parent;

			if (path.lastIndexOf('/') == 0)
				parent = getRootDirectory();
			else
				parent = createPath(path.substring(0, path.lastIndexOf('/')));

			dir = new Directory(this, path.substring(path.lastIndexOf('/') + 1), root.getUmask(), root, parent);
		}
		return dir;
	}

	public void importXml(Element element) throws ImportDocumentException {
		for (Element node : element.getChildren("user")) {
			if (!hasUser(node.getAttributeValue("username")))
				new User(this, node);
			else
				log.trace("Trying to import user '" + node.getAttributeValue("username")
						+ "' that already exists. Aborting user import...");
		}

		for (Element node : element.getChildren("dir"))
			new Directory(this, node);

		for (Element node : element.getChildren("plain"))
			new PlainFile(this, node);

		for (Element node : element.getChildren("link"))
			new Link(this, node);

		for (Element node : element.getChildren("app"))
			new App(this, node);
	}

	public Document exportXml() {
		Element drive = new Element("myDrive");
		Document doc = new Document(drive);

		for (User u : getUserSet())
			drive.addContent(u.exportXml());

		drive.addContent(drive.getContentSize(), getRootDirectory().exportXml(drive));
		return doc;
	}

	// ======================
	// | DISABLED FUNCTIONS |
	// ======================
	@Override
	public void setEntryCounter(int counter) {
		throw new ForbiddenMethodException("mydrive.setEntryCounter");
	}

	@Override
	public int getEntryCounter() {
		throw new ForbiddenMethodException("mydrive.getEntryCounter");
	}

	@Override
	public void setRootDirectory(Directory dir) {
		throw new ForbiddenMethodException("mydrive.setRootDirectory");
	}

	@Override
	public Set<Login> getLoginSet() {
		throw new ForbiddenMethodException("mydrive.getLoginSet");
	}

	@Override
	public void setDomainRoot(pt.ist.fenixframework.DomainRoot domainRoot) {
		throw new ForbiddenMethodException("mydrive.setDomainRoot");
	}

	@Override
	public pt.ist.fenixframework.DomainRoot getDomainRoot() {
		throw new ForbiddenMethodException("mydrive.getDomainRoot");
	}

	public String getExtensionByFilename(String filename) {
		return filename;
		// TODO Auto-generated method stub

	}

}
