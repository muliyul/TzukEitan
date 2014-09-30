package TzukEitan.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import TzukEitan.launchers.EnemyLauncher;
import TzukEitan.missiles.EnemyMissile;
import TzukEitan.utils.IdGenerator;
import TzukEitan.utils.Utils;
import TzukEitan.war.War;
import TzukEitan.war.WarStatistics;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

public class ClientController {
    @FXML
    private TabPane inventoryPane;

    @FXML
    private ComboBox<String> chosenLauncher, chosenDestination;

    @FXML
    private Slider chosenDamage, chosenFlytime;

    @FXML
    private TextArea inventoryTextArea;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientController(ObjectInputStream in, ObjectOutputStream out) {
	this.in = in;
	this.out = out;
	Timeline inventoryRefresher = new Timeline();
	inventoryRefresher.setDelay(Duration.seconds(1));
	inventoryRefresher.setCycleCount(Timeline.INDEFINITE);
	inventoryRefresher.setOnFinished(new EventHandler<ActionEvent>() {
	    public void handle(ActionEvent event) {
		refreshInventory();
	    }
	});
	inventoryRefresher.play();
    }

    @FXML
    protected void addLauncher() {
	try {
	    out.writeChars(TzukEitanProtocol.ADD_LAUNCHER);
	    out.writeObject(new EnemyLauncher(IdGenerator
		    .enemyLauncherIdGenerator(), true, in.readUTF(),
		    (WarStatistics) in.readObject()));
	} catch (ClassNotFoundException | IOException e) {

	}
    }

    @FXML
    protected void fireMissile() {
	try {
	    out.writeChars(TzukEitanProtocol.FIRE_MISSILE);
	    String warName = in.readUTF();
	    out.writeObject(new EnemyMissile(IdGenerator
		    .enemyMissileIdGenerator(), chosenDestination.getValue(),
		    (int) chosenFlytime.getValue(), (int) chosenDamage
			    .getValue(), chosenLauncher.getValue(), null,
		    warName));
	} catch (IOException e) {
	}
    }

    protected void refreshInventory() {
	try {
	    if (inventoryPane.isVisible()) {
		out.writeChars(TzukEitanProtocol.REQUEST_ENEMY_INVENTORY);
		String eInv = in.readUTF();
		inventoryTextArea.setText(eInv);
	    }
	} catch (IOException e) {
	}
    }
}
