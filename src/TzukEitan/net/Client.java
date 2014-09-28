package TzukEitan.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import TzukEitan.war.War;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Client extends Application {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client() {
    }

    public static void main(String[] args) {
	launch(args);
    }

    public Client(String serverAddress, int port) {
	
	try {
	    socket = new Socket(serverAddress, port); // blocking
	    System.out.println(LocalDateTime.now() + "Connected to server on "
		    + socket.getLocalAddress() + ':' + socket.getLocalPort());
	    in = new ObjectInputStream(socket.getInputStream());
	    out = new ObjectOutputStream(socket.getOutputStream());
	} catch (IOException e) {
	}
    }

    private void createAndShowGUI(Stage s) {
	FXMLLoader fxml = new FXMLLoader(getClass().getResource("Client.fxml"));
	fxml.setController(new ClientController(in,out));
	s.setTitle("Enemy War Client");
	try {
	    s.setScene(new Scene(fxml.load()));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	s.show();
    }

    @Override
    public void start(Stage arg0) throws Exception {
	Platform.runLater(new Runnable() {
	    public void run() {
		createAndShowGUI(arg0);
	    }
	});
    }
}
