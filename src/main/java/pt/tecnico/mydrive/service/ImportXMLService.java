package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exception.ImportDocumentException;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import java.io.IOException;
import java.io.File;
    


public class ImportXMLService extends MyDriveService {

  private String path;


  public ImportXMLService(String path) {
    this.path = path;
  }


  @Override
  public final void dispatch() throws ImportDocumentException {
  
    if(path==null){
      throw new IllegalArgumentException("Given path is null");
    }

    try {
      MyDrive md = getMyDrive();
      SAXBuilder builder = new SAXBuilder();
      Document document = (Document) builder.build(new File(path));
      md.importXml(document.getRootElement());
    } catch (JDOMException | IOException e) {
      throw new ImportDocumentException("Unable to open XML file or file is wrongly formatted.");
    }
        

  }
    
}