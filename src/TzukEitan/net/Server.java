package TzukEitan.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import TzukEitan.war.WarController;

public class Server extends Thread {

	private List<ClientHandler> ch;
	private ServerSocket ss;
	private boolean isRunning;
	private WarController wc;

	public Server(WarController wc,int port) {
		ch = new Vector<>();
		this.wc = wc;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
		}
		start();
	}

	@Override
	public void run() {
		isRunning = true;
		System.out.println("Server is listening on " + ss.getLocalPort());
		while (isRunning) {
			try {
				Socket client = ss.accept();
				ch.add(new ClientHandler(client));
			} catch (IOException e) {
			}
		}
	}

	class ClientHandler extends Thread {
		private Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		public ClientHandler(Socket s) {
			socket = s;
			try {
				in = new ObjectInputStream(s.getInputStream());
				out = new ObjectOutputStream(s.getOutputStream());
			} catch (IOException e) {
			}
		}

		@Override
		public void run() {
			while (socket.isConnected()) {
				// do stuff
			}
		}
	}
}
