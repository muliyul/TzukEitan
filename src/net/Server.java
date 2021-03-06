package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import utils.IdGenerator;
import model.War;

public class Server extends Thread {
    private List<ClientHandler> ch;
    private ServerSocket ss;
    private boolean isRunning;
    private Semaphore semaphore;
    private War model;

    public Server(War w, int port) {
	ch = new Vector<>();
	this.model = w;
	semaphore = new Semaphore(1, true);
	try {
	    ss = new ServerSocket(port);
	    ss.setSoTimeout(1000);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	start();
    }

    @Override
    public void run() {
	try {
	    isRunning = true;
	    System.out.println("Server is listening on port "
		    + ss.getLocalPort());
	    while (isRunning) {
		try {
		    Socket client = ss.accept();
		    if (client != null)
			ch.add(new ClientHandler(client));
		} catch (IOException e) {
		}
	    }
	} catch (Exception e) {
	}
    }

    class ClientHandler extends Thread {
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean isConnected;

	public ClientHandler(Socket s) {
	    socket = s;
	    try {
		out = new ObjectOutputStream(s.getOutputStream());
		in = new ObjectInputStream(s.getInputStream());
	    } catch (IOException e) {
	    }
	    start();
	}

	@Override
	public void run() {
	    isConnected = true;
	    while (isConnected) {
		try {
		    parseAndInvokeCommand((Protocol) in.readObject());
		    semaphore.release();
		} catch (InterruptedException | IOException
			| ClassNotFoundException e) {
		    // e.printStackTrace();
		}
	    }
	}

	private void parseAndInvokeCommand(Protocol p)
		throws InterruptedException, IOException {
	    semaphore.acquire();
	    switch (p.getType()) {
	    case ADD_LAUNCHER: {
		addLauncher();
		break;
	    }
	    case FIRE_MISSILE: {
		fireMissile(p.getContent());
		break;
	    }
	    case REQUEST_AVAILABLE_DESTINATIONS: {
		sendDestinations();
		break;
	    }
	    case REQUEST_AVAILABLE_LAUNCHERS: {
		sendAvailableLaunchers();
		break;
	    }
	    case EXIT: {
		closeConnection();
		break;
	    }
	    case REQUEST_ENEMY_INVENTORY: {
		sendEnemyInventory();
		break;
	    }
	    default:
		break;
	    }

	}

	private void sendEnemyInventory() throws IOException {
	    out.writeObject(new Protocol(Protocol.Type.REQUEST_ENEMY_INVENTORY,
		    model.getEnemyInventory()));
	}

	private void sendDestinations() throws IOException,
		InterruptedException {
	    out.writeObject(new Protocol(
		    Protocol.Type.REQUEST_AVAILABLE_DESTINATIONS,
		    (Object) model.getAllTargetCities()));
	}

	private void sendAvailableLaunchers() throws IOException,
		InterruptedException {
	    out.writeObject(new Protocol(
		    Protocol.Type.REQUEST_AVAILABLE_LAUNCHERS, model
			    .getAllLaunchersIds()));
	}

	private void addLauncher() throws InterruptedException {
	    model.addEnemyLauncher();
	}

	private void fireMissile(Object[] params) throws InterruptedException {
	    model.launchEnemyMissile((String) params[0], (String) params[1],
		    (int) params[2], (int) params[3]);
	}

	public void closeConnection() throws IOException {
	    isConnected = false;
	    socket.close();
	}
    }

    public void stopServer() {
	System.out.println("Server is closing...");
	isRunning = false;
	for (ClientHandler c : ch) {
	    try {
		c.closeConnection();
	    } catch (IOException e) {
		// e.printStackTrace();
	    }
	}
    }
}
