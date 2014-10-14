/*

 * Made by:
 * Muli Yulzary & Yoni Maymon
 */

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

import model.War;
import net.Server;

import org.xml.sax.SAXException;

import controller.WarController;
import utils.WarXMLReader;
import view.AbstractWarView;
import view.ConsoleView;
import view.GUIView;
import db.DBConnection;
import db.DBFactory;

public class Main {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
	
	AbstractWarView view = new GUIView();

	int startWar = view.showFirstDialog();

	view.flushBuffers();

	if (startWar == 0) {
	    String warName = null;
	    Future<Boolean> fWarExists;
	    // DECIDE WHICH DB DRIVER TO USE
	    DBConnection db = DBFactory.setInstance(DBFactory.Type.JDBC);
	    try {
		do {
		    warName = view.getWarNameFromUser();
		    fWarExists = db.checkWarName(warName);
		} while (!fWarExists.get());
	    } catch (InterruptedException | ExecutionException e1) {
		e1.printStackTrace();
	    }

	    War war = new War(warName);
	    WarController warGUIControl = new WarController(war, view);
	    // WarController warConsoleControl = new WarController(war, view);
	    Server warServer = new Server(war, 9999);

	    try {
		WarXMLReader warXML = new WarXMLReader("warStart.xml", war);
		db.addNewWar(war);
		warXML.start();
		warXML.join();
	    } catch (ParserConfigurationException e) {
		e.printStackTrace();
	    } catch (SAXException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	    war.start();
	    if (view instanceof ConsoleView)
		((ConsoleView) view).start();
	    else if (view instanceof GUIView)
		((GUIView) view).setVisible(true);
	} else if (startWar == 1) {
	    view.showDBDialog();
	} else {
	    System.exit(0);
	}
    }
}
