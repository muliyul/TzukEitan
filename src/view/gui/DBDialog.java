package view.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DBDialog extends Application implements Initializable{

    @Override
    public void start(Stage primaryStage) throws Exception {
	FXMLLoader loader =
		new FXMLLoader(getClass().getResource("DatabaseBrowser.fxml"));
	primaryStage.setTitle("Database Browser");
	loader.setController(new DatabaseBrowserController(primaryStage));
	try {
	    primaryStage.setScene(new Scene(loader.load()));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	// TODO Auto-generated method stub
	
    }

}
