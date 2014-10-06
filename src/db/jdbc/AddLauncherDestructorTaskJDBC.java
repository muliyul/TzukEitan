package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class AddLauncherDestructorTaskJDBC extends DBTask {

    private String id;
    private String type;

    public AddLauncherDestructorTaskJDBC(Semaphore s, Connection c,
	    String warName, String id, String type) {
	super(s, c, warName);
	this.id = id;
	this.type = type;
    }

    public void run() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("INSERT INTO `WarSim`.`LauncherDestructor` (`ID`, `Type`, `WarName`) VALUES (?, ?, ?)");
	    statement.setString(1, id);
	    statement.setString(2, type);
	    statement.setString(3, warName);
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
