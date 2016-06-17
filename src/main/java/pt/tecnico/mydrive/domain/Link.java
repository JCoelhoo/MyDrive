package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.EntryAlreadyExistsException;
import pt.tecnico.mydrive.exception.EntryDoesNotExistException;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.CyclicLinkException;

import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
public class Link extends Link_Base {
    
  public Link() {
    super();
  }
    
  public Link(MyDrive mydrive, String name, byte perms, User owner, Directory parent, String path) throws EntryAlreadyExistsException {
    super();
    init(mydrive, name, perms, owner, parent, path);
  }
  
  public int getSize() {
	  return this.getName().length();
  }

  @Override
  public String getEntryType() {
	  return "Link";
  }
  
  public Entry getTarget(User user) {
    return getTarget(user, new ArrayList<String>());
  }

  // DEALS WITH INFINITE LINK CYCLES
  public Entry getTarget(User user, List<String> list) {
    String path;

    /* converting to absolute path */
    if (getContent().substring(0,1).equals("/"))
      path = getContent();
    else
      path = getParent().getPath() + "/" + getContent();

    if (list.contains(path)) {
      /* already visited this link */
      throw new CyclicLinkException();
    } else {
      list.add(path);
      Entry entry = MyDrive.getInstance().getEntry(path, user, getParent());
      if (entry instanceof Link) {
        return ((Link) entry).getTarget(user, list);
      } else {
        /* no more recursion */
        return entry;
      }
    }
  }       
  
  @Override
  public void addEntry(Entry newEntry, User user) throws EntryAlreadyExistsException, PermissionDeniedException {
	if (!hasPermission(user, 'w'))
		throw new PermissionDeniedException(user.getUsername(), 'w', getPath());
	getTarget(user).addEntry(newEntry, user);
  }
  
  @Override
  public Entry getEntry(String path, User user) {
	  return getTarget(user).getEntry(path, user); 
  }
  
  
  @Override
  public String read(User user) {
    return getTarget(user).read(user);
  }

  @Override
  public void write(User user, String input) {
    getTarget(user).write(user, input);
  }

  @Override
  public void execute(User user, String[] args) {
    getTarget(user).execute(user, args);
  }

  @Override
  public String toString() {
    return getName()+ "->" + getContent();
  }

  // =======================
  // | XML IMPORT / EXPORT |
  // =======================

  public Link(MyDrive mydrive, Element xml) throws ImportDocumentException {
    super();
    importXml(mydrive, xml);
  }

  public void importXml(MyDrive mydrive, Element dirElement) throws ImportDocumentException {
    String content = dirElement.getChildText("value");
    if (content != null) 
      setContent(content);
    super.importXml(mydrive, dirElement);
  }

  public Element exportXml(Element drive) {
	  Element link = super.exportXml(drive);
	  link.setName("link");	
	  link.getChild("contents").setName("value");
	  return link;
  }
  
}
