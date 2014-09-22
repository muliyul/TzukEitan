/*

 * Made by:
 * Guy Eshel: eshelguy@gmail.com
 * &
 * Ben Amir: amir.ben@gmail.com
 */



import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import TzukEitan.view.AbstractWarView;
import TzukEitan.view.ConsoleView;
import TzukEitan.view.GUIView;
import TzukEitan.view.WarXMLReader;
import TzukEitan.war.War;
import TzukEitan.war.WarController;

public class TzukEitan {

	public static void main(String[] args) {
		WarXMLReader warXML;

		AbstractWarView guiView = new GUIView();
		AbstractWarView consoleView = new ConsoleView();
		
		War warModel = new War(guiView.getWarName());

		WarController warGUIControl = new WarController(warModel, guiView);
		WarController warConsoleControl = new WarController(warModel, consoleView);
		
		try {
			warXML = new WarXMLReader("warStart.xml", warGUIControl);
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
		((ConsoleView)consoleView).start();
	}

}
