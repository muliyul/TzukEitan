package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class AddIronDomeTask extends DBTask<Void> {

    private String idId;

    public AddIronDomeTask(Semaphore s, Connection c,
	    String warName, String idId) {
	super(s, c, warName);
	this.idId = idId;
    }

    public Void Call() {
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
	return null;
    }
}
