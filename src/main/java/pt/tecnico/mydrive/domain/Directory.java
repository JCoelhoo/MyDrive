package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.mydrive.exception.CannotRemoveRootDirectoryException;
import pt.tecnico.mydrive.exception.EntryAlreadyExistsException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.ExceededPathLengthException;
import pt.tecnico.mydrive.exception.UnsupportedOperationException;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

import java.util.*;

public class Directory extends Directory_Base {

	// USED TO CREATE ROOT DIRECTORY "/"
	public Directory(MyDrive mydrive, String name, byte perms, User owner) {
		super();
		init(mydrive, name, perms, owner);
		setMydrive(mydrive);
	}

	// USED TO CREATE REGULAR DIRECTORIES
	public Directory(MyDrive mydrive, String name, byte perms, User owner, Directory parent)
	  throws EntryAlreadyExistsException {
		super();
		init(mydrive, name, perms, owner, parent);
	}
	
	public int getSize() {
		return getEntrySet().size() + 2;
	}

	@Override
	public String getEntryType() {
		  return "Directory";
	}
	
  //  ==================================
  //  | METHODS USED TO MANAGE ENTRIES |
  //  ==================================

	@Override
	public void addEntry(Entry newEntry, User user) throws EntryAlreadyExistsException, PermissionDeniedException {
		if (!hasPermission(user, 'w')) {
			throw new PermissionDeniedException(user.getUsername(), 'w', getPath());
		}
		if (hasEntry(newEntry.getName()))
			throw new EntryAlreadyExistsException(newEntry.getName());

		super.addEntry(newEntry);
	}

	public boolean hasEntry(String entryName) {
		if (entryName.equals(".") || entryName.equals("..")) {
			return true;
		}
		for (Entry entry : getEntrySet())
			if (entry.getName().equals(entryName))
				return true;
		return false;
	}

	// SEARCHES FOR AN ENTRY IN THIS DIRECTORY
	public Entry getEntryByName(String entryName, User user)
			throws EntryDoesNotExistException, PermissionDeniedException {
		if (!hasPermission(user, 'x')) {
			throw new PermissionDeniedException(user.getUsername(), 'x', getPath());
		}
		if (entryName.equals("."))
			return this;
		if (entryName.equals(".."))
			return getParent();
		for (Entry entry : getEntrySet())
			if (entry.getName().equals(entryName))
				return entry;
		throw new EntryDoesNotExistException(entryName);
	}
	
	public Set<Entry> getEntryList(User user) {
		if (!hasPermission(user, 'r'))
			throw new PermissionDeniedException(user.getUsername(), 'r', getPath());
		return getEntrySet();
	}
	
	// SEARCHES FOR AN ENTRY IN THE DIRECTORIES TREE
	@Override
	public Entry getEntry(String path, User user) throws EntryDoesNotExistException, PermissionDeniedException {
		if (path.charAt(path.length() - 1) == '/')
		  path = path.substring(0, path.length() - 1);

		String[] directories = path.split("/", 2);
		Entry entry = null;

		entry = this.getEntryByName(directories[0], user);

		if (directories.length > 1) {
			return entry.getEntry(directories[1], user);
		} else {
			return entry;
		}
	}

	@Override
	public void remove(User user) throws CannotRemoveRootDirectoryException, PermissionDeniedException {
		if (this == getParent())
			throw new CannotRemoveRootDirectoryException();

		if (!hasPermission(user, 'd'))
			throw new PermissionDeniedException(user.getUsername(), 'd', getPath());

		if (!getParent().hasPermission(user, 'w'))
			throw new PermissionDeniedException(user.getUsername(), 'w', getParent().getPath());

		if (this.getSize() > 2) {
			for (Entry entries : this.getEntrySet()) {
				entries.remove(user);
			}
		}

		setParent(null);
		setOwner(null);
		deleteDomainObject();
	}


	// =======================
  // | XML IMPORT / EXPORT |
  // =======================

	public Directory(MyDrive mydrive, Element xml) throws ImportDocumentException {
		super();
		importXml(mydrive, xml);
	}
    
	public void importXml(MyDrive mydrive, Element dirElement) throws ImportDocumentException {
		super.importXml(mydrive, dirElement);
	}

	public Element exportXml(Element drive) {
		Element dir = super.exportXml(drive);
		dir.setName("dir");
		for (Entry e : getEntrySet()) {
			if (!(e.equals(this) || e.equals(getParent())))
				drive.addContent(drive.getContentSize(), e.exportXml(drive));
		}
		return dir;
	}
}
