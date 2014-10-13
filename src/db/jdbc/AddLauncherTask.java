package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class AddLauncherTask extends DBTask<Void> {

    private String launcherId;

    public AddLauncherTask(Semaphore s, Connection c, String warName, String lId) {
	super(s, c, warName);
	this.launcherId = lId;
    }
    
    public Void call() {
	try {
		executer.acquire();
		PreparedStatement statement = connection
				.prepareStatement("INSERT INTO `WarSim`.`EnemyLauncher` (`ID`, `WarName`) VALUES (?, ?)");
		statement.setString(1, launcherId);
		statement.setString(2, warName);
		statement.executeUpdate();
	} catch (SQLException e) {
		while (e != null) {
			System.out.println(e.getMessage());
			e = e.getNextException();
		}
	} catch (InterruptedException e) {
		e.printStackTrace();
	} finally {
		executer.release();
	}
	return null;
    }

}
