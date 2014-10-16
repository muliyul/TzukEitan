package view.gui;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import db.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class DatabaseBrowserController implements Initializable {
    
    private DBConnection db;
    @SuppressWarnings("unused")
    private Stage window;
    
    @FXML
    private TextArea dbTextArea;
    
    @FXML
    private DatePicker fromDate;
    
    @FXML
    private DatePicker toDate;

    public DatabaseBrowserController(Stage s,DBConnection db) {
	window = s;
	this.db = db;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	dbTextArea.setEditable(false);
    }
    
    public void submit() {
	LocalDate from = fromDate.getValue(), to = toDate.getValue();
	Future<String[]> fwarNames = db.getWarNamesByDate(from, to);
	try {
	    String[] warNames = fwarNames.get();
	    int i=0;
	    for(String war : warNames){
		long[] stats = db.getWarStats(war).get();
		dbTextArea.appendText("\t" + warNames[i++] + " Statistics\n");
		dbTextArea.appendText("=========================================\n");
		dbTextArea.appendText('\t' + stats[0]==0? "War is still running":"War has ended\n");
		dbTextArea.appendText("\tNum of launch missiles: " + stats[1] + "\n");
		dbTextArea.appendText("\tNum of intercept missiles: " + stats[2] + "\n");
		dbTextArea.appendText("\tNum of hit target missiles: " + stats[3] + "\n");
		dbTextArea.appendText("\tNum of launchers destroyed: " + stats[4] + "\n");
		dbTextArea.appendText("\ttotal damage: " + stats[5] + "\n");
		dbTextArea.appendText("==========================================\n");
	    }
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
    }
}
