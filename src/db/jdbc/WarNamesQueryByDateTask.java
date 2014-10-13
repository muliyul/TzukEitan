package db.jdbc;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class WarNamesQueryByDateTask implements Callable<String[]> {

    private Semaphore executer;
    private Connection connection;
    private String startDate;
    private String endDate;

    public WarNamesQueryByDateTask(Semaphore s, Connection c, String startDate,
	    String endDate) {
	this.executer = s;
	this.connection = c;
	this.startDate = startDate;
	this.endDate = endDate;
    }

    @Override
    public String[] call() throws Exception {
	String[] warNames = null;
	try {
	    String fixedStartDate = startDate + " 00:00:00";
	    String fixedEndtDate = endDate + " 23:59:59";
	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT WarName FROM `WarSim`.`War` WHERE `War`.`StartTime` BETWEEN ? AND ?");
	    statement.setString(1, fixedStartDate);
	    statement.setString(2, fixedEndtDate);
	    executer.acquire();
	    ResultSet res = statement.executeQuery();
	    System.out.println(warNames);
	    Array arr = res.getArray("WarName");
	    warNames = (String[]) arr.getArray();
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
	return warNames;
    }

}
