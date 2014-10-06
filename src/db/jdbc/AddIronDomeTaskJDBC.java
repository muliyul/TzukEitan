package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class AddIronDomeTaskJDBC extends DBTask {

    private String idId;

    public AddIronDomeTaskJDBC(Semaphore s, Connection c,
	    String warName, String idId) {
	super(s, c, warName);
	this.idId = idId;
    }

    public void run() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("INSERT INTO `WarSim`.`IronDome` (`ID`, `WarName`) VALUES (?, ?)");
	    statement.setString(1, idId);
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
    }
}
