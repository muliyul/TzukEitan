/*

 * Made by:
 * Muli Yulzary & Yoni Maymon
 */

import java.io.IOException;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import model.War;
import model.WarController;
import net.Server;

import org.xml.sax.SAXException;

import utils.WarXMLReader;
import view.AbstractWarView;
import view.ConsoleView;
import view.GUIView;
import db.DBConnection;
import db.DBFactory;

public class Main {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
	WarXMLReader warXML;

	AbstractWarView guiView = new GUIView();
	AbstractWarView consoleView = new ConsoleView();

	boolean startWar = guiView.showFirstDialog();
	// DECIDE WHICH DBDRIVER TO USE
	DBConnection db = DBFactory.get(DBFactory.Type.JDBC);

	if (startWar) {
	    String warName = guiView.getWarNameFromUser();
	    while (!db.checkWarName(warName));

	    War warModel = new War(warName, db);
	    WarController warGUIControl = new WarController(warModel, guiView);
	    // WarController warConsoleControl = new
	    // WarController(warModel,consoleView);
	    Server warServer = new Server(warGUIControl, 9999);

	    try {
		warXML = new WarXMLReader("warStart.xml", warGUIControl);
		db.addNewWar(warModel);
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

	    warModel.start();
	    ((ConsoleView) consoleView).start();
	    ((JFrame) guiView).setVisible(true);
	} else {
	    guiView.showDBDialog();
	}
    }
}
