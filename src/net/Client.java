package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client() {
    }

    public static void main(String[] args) {
	launch(args);
    }

    private void createAndShowGUI(Stage s) {
	FXMLLoader fxml = new FXMLLoader(getClass().getResource("Client.fxml"));
	fxml.setController(new ClientController(in,out));
	try {
	    s.setScene(new Scene(fxml.load()));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	s.show();
    }

    @Override
    public void start(Stage s) throws Exception {
	try {
	    socket = new Socket("localhost", 9999); // blocking
	    s.setTitle("Enemy War Client - Connected to server on "
		    + socket.getLocalAddress() + ':' + socket.getLocalPort());
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());
	} catch (IOException e) {
	}
	s.setOnCloseRequest(new EventHandler<WindowEvent>() {
	    public void handle(WindowEvent event) {
		try {
		    out.writeObject(new Protocol(Protocol.Type.EXIT));
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	});
	Platform.runLater(new Runnable() {
	    public void run() {
		createAndShowGUI(s);
	    }
	});
	
    }
}
