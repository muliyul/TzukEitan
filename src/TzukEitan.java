/*

 * Made by:
 * Guy Eshel: eshelguy@gmail.com
 * &
 * Ben Amir: amir.ben@gmail.com
 */

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import model.War;
import model.WarController;
import net.Server;

import org.xml.sax.SAXException;

import utils.WarXMLReader;
import view.AbstractWarView;
import view.ConsoleView;
import view.GUIView;
import db.TzukEitanDBConnection;

public class TzukEitan {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		WarXMLReader warXML;

		AbstractWarView guiView = new GUIView();
		AbstractWarView consoleView = new ConsoleView();

		String warName;
		while (!TzukEitanDBConnection.checkWarName(warName = guiView
				.getWarNameFromUser()));
		War warModel = new War(warName);

		WarController warGUIControl = new WarController(warModel, guiView);
		//WarController warConsoleControl = new WarController(warModel,consoleView);
		Server warServer = new Server(warGUIControl, 9999);
		
		try {
			warXML = new WarXMLReader("warStart.xml", warGUIControl);
			TzukEitanDBConnection.addNewWar(warName);
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
	}

}
