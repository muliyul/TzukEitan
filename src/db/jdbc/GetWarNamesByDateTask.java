package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class GetWarNamesByDateTask extends DBTask<String[]> {
    private LocalDate startDate;
    private LocalDate endDate;

    public GetWarNamesByDateTask(Semaphore s, Connection c, LocalDate startDate,
	    LocalDate endDate) {
	super(s, c, null);
	this.startDate = startDate;
	this.endDate = endDate;
    }

    @Override
    public String[] call() throws Exception {
	String[] warNames = null;
	try {
	    executer.acquire();
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    String fixedStartDate = startDate.format(dtf) + " 00:00:00";
	    String fixedEndtDate = endDate.format(dtf) + " 23:59:59";
	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT `WarName` FROM `WarSim`.`War` WHERE `War`.`StartTime` BETWEEN ? AND ?");
	    statement.setString(1, fixedStartDate);
	    statement.setString(2, fixedEndtDate);
	    ResultSet res = statement.executeQuery();
	    Vector<String> names = new Vector<String>();
	    while(res.next())
		names.add(res.getString("WarName"));
	    warNames = names.toArray(new String[names.size()]);
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
