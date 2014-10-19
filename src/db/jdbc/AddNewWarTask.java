package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;


public class AddNewWarTask extends DBTask<Void>{

    public AddNewWarTask(Semaphore s, Connection c, String warName) {
	super(s, c, warName);
    }

    @Override
    public Void call() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    ((Connection) connection)
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
	return null;
    }
}
