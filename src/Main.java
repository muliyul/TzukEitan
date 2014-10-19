/*

 * Made by:
 * Muli Yulzary & Yoni Maymon
 */

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import model.War;
import net.Server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import utils.WarXMLReader;
import view.AbstractWarView;
import view.ConsoleView;
import view.GUIView;
import controller.WarController;
import db.DBConnection;
import db.DBFactory;

public class Main {

    @SuppressWarnings("unused")
    public static void main(String[] args) throws ExecutionException {
	ApplicationContext ac = // JDBCBean.xml
		new ClassPathXmlApplicationContext("JDBCBean.xml"); // JPABean.xml
	DBConnection db = (DBConnection) ac.getBean("DatabaseConnection");

	DBFactory.setInstance(db);

	AbstractWarView view = new GUIView();

	int startWar = view.showFirstDialog();

	view.flushBuffers();

	if (startWar == 0) {
	    String warName = null;

	    // DECIDE WHICH DB DRIVER TO USE
	    // DBFactory.setInstance(DBFactory.Type.JDBC);

	    do {
		warName = view.getWarNameFromUser();
	    } while (db.checkWarName(warName));

	    War war = new War(warName);
	    db.addNewWar(war);
	    WarController warGUIControl = new WarController(war, view);
	    // WarController warConsoleControl = new WarController(war, view);
	    war.setServer(new Server(war, 9999));

	    try {
		WarXMLReader warXML = new WarXMLReader("warStart.xml", war);
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
