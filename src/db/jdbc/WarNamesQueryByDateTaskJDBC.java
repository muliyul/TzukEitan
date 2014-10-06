package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class WarNamesQueryByDateTaskJDBC extends DBTask {

    private String startDate;
    private String endDate;

    public WarNamesQueryByDateTaskJDBC(Semaphore s,
	    Connection c, String startDate, String endDate) {
	super(s, c, null);
	this.startDate = startDate;
	this.endDate = endDate;
    }
    
    public void run() {
	try {
	    String fixedStartDate = startDate + " 00:00:00";
	    String fixedEndtDate = startDate + " 23:59:59";
	    
	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT WarName FROM `WarSim`.`War` WHERE `War`.`StartTime` BETWEEN ? AND ?");
	    statement.setString(1, fixedStartDate);
	    statement.setString(2, fixedEndtDate);
	    executer.acquire();
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
