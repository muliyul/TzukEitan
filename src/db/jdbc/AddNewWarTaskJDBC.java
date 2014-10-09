package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;


public class AddNewWarTaskJDBC extends DBTask implements Runnable {

    public AddNewWarTaskJDBC(Semaphore s, Connection c, String warName) {
	super(s, c, warName);
    }

    @Override
    public void run() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("INSERT INTO `WarSim`.`War` (`WarName`, `StartTime`) VALUES ( ?, ?)");
	    statement.setString(1, warName);
	    statement
		    .setTimestamp(2, new Timestamp(System.currentTimeMillis()));
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
    }
}
