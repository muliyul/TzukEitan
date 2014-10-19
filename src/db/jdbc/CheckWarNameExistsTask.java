package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class CheckWarNameExistsTask extends DBTask<Boolean> {

    public CheckWarNameExistsTask(Semaphore s, Connection c, String warName) {
	super(s, c, warName);
    }

    
    @Override
    public Boolean call() throws Exception {
	try {
	    PreparedStatement statement =
		    ((Connection) connection)
			    .prepareStatement("SELECT WarName FROM `WarSim`.`War` WHERE `War`.`WarName` =  ?");
	    statement.setString(1, warName);
	    ResultSet rs = statement.executeQuery();
	    return rs.first();
	} catch (SQLException e) {
	    while (e != null) {
		System.out.println(e.getMessage());
		e = e.getNextException();
	    }
	}
	return false;
    }
}
