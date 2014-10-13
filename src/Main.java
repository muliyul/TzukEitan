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

public class Main {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
	WarXMLReader warXML;

	AbstractWarView guiView = new GUIView();
	AbstractWarView consoleView = new ConsoleView();

	boolean startWar = guiView.showFirstDialog();
	if (startWar) {

	    String warName;
	    while (!DBConnection.checkWarName(warName =
		    guiView.getWarNameFromUser()))
		;

	    War warModel = new War(warName, true);
	    WarController warGUIControl = new WarController(warModel, guiView);
	    // WarController warConsoleControl = new
	    // WarController(warModel,consoleView);
	    Server warServer = new Server(warGUIControl, 9999);

	    try {
		warXML = new WarXMLReader("warStart.xml", warGUIControl);
		DBConnection.addNewWar(warName);
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
	    ((JFrame)guiView).setVisible(true);
	} else {
	    guiView.showDBDialog();
	}
    }
}
