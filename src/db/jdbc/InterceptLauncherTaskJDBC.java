package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class InterceptLauncherTaskJDBC extends DBTask {

    private String lId;
    private String dId;

    public InterceptLauncherTaskJDBC(Semaphore s, Connection c,
	    String warName, String lId, String dId) {
	super(s, c, warName);
	this.lId = lId;
	this.dId = dId;
    }
    
    public void run() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("UPDATE  `WarSim`.`EnemyLauncher` SET `Intercepted` = '1', `InterceptionTime` =  ? , `InterceptedBy` = ? WHERE `EnemyLauncher`.`ID` = ? AND  `EnemyLauncher`.`WarName` = ?");
	    statement.setTimestamp(1,
		    new Timestamp(System.currentTimeMillis()));
	    statement.setString(2, dId);
	    statement.setString(3, lId);
	    statement.setString(4, warName);
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
