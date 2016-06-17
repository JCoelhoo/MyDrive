package pt.tecnico.mydrive;

import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.mydrive.domain.MyDrive;

public class Main {

	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws IOException {

		try {
			System.out.println("*** Welcome to the MyDrive application! ***");
			for (String s : args)
				scanXml(new File(s));
			/*createPlainFile("/home", "README", getUserList());
			createDirectory("/usr/local", "bin");
			createPlainFile("/usr/local/bin", "Hello", "Howdy");
			readEntry("/home/README");
			printXml();
			removeEntry("/home/README");
			listDirectory("/usr");
			System.out.println("------- /home dir---------");
			listDirectory("/usr/local");
			// removeEntry("/usr");*/
		}

		finally {
			FenixFramework.shutdown();
		}

	}

	@Atomic
	public static void init() {
		log.trace("Init: " + FenixFramework.getDomainRoot());
		MyDrive.getInstance();
	}


	@Atomic
	public static void scanXml(File file) {
		log.trace("loading file: " + file.toString());
		MyDrive md = MyDrive.getInstance();
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(file);
			md.importXml(document.getRootElement());
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

	@Atomic
	public static void printXml() {
		log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
		Document doc = MyDrive.getInstance().exportXml();
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlOutput.output(doc, new PrintStream(System.out));
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
