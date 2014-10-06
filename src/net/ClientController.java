package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.ResourceBundle;

import utils.IdGenerator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

public class ClientController implements Initializable {
    @FXML
    private TabPane tabPane;

    @FXML
    private ComboBox<String> chosenLauncher, chosenDestination;

    @FXML
    private Slider chosenDamage, chosenFlytime;

    @FXML
    private TextArea operationsTextArea;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientController(ObjectInputStream in, ObjectOutputStream out) {
	this.in = in;
	this.out = out;
    }

    @FXML
    protected void addLauncher() {
	try {
	    out.writeObject(new Protocol(Protocol.Type.ADD_LAUNCHER));
	    operationsTextArea
		    .appendText(getFormattedText("Launcher added successfully"));
	} catch (IOException e) {
	    operationsTextArea
		    .appendText(getFormattedText("Launcher was NOT added! (Something went wrong)"));
	}
    }

    @FXML
    protected void fireMissile() {
	try {
	    out.writeObject(new Protocol(Protocol.Type.FIRE_MISSILE,
		    IdGenerator.enemyMissileIdGenerator(), chosenDestination
			    .getValue(), (int) chosenFlytime.getValue(),
		    (int) chosenDamage.getValue(), chosenLauncher.getValue()));
	    operationsTextArea
		    .appendText(getFormattedText("Missile added successfully"));
	} catch (IOException e) {
	    operationsTextArea
		    .appendText(getFormattedText("Missile was NOT added! (Something went wrong)"));
	}
    }

    private String getFormattedText(String str) {
	return LocalDateTime.now().format(
		DateTimeFormatter.ofPattern("[ dd.MM.uu / HH:mm:ss ]" + str));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	tabPane.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<Tab>() {
		    @SuppressWarnings("unchecked")
		    @Override
		    public void changed(
			    ObservableValue<? extends Tab> observable,
			    Tab oldValue, Tab newValue) {
			try {
			    if (tabPane.getSelectionModel().getSelectedIndex() == 1) {
				out.writeObject(new Protocol(
					Protocol.Type.REQUEST_AVAILABLE_LAUNCHERS));
				Protocol p = (Protocol) in.readObject();
				chosenLauncher.getItems().clear();
				chosenLauncher.getItems().addAll(
					(Collection<String>) p.getContent()[0]);
				out.writeObject(new Protocol(
					Protocol.Type.REQUEST_AVAILABLE_DESTINATIONS));
				p = (Protocol) in.readObject();
				chosenLauncher.getItems().clear();
				chosenDestination.getItems().addAll(
					(String[]) p.getContent()[0]);
			    }
			} catch (ClassNotFoundException | IOException e) {
			}
		    }

		});
    }
}
