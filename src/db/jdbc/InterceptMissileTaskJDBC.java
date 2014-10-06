package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class InterceptMissileTaskJDBC extends DBTask {

    private String mId;
    private String idId;

    public InterceptMissileTaskJDBC(Semaphore s, Connection c,
	    String warName, String mId, String idId) {
	super(s, c, warName);
	this.mId = mId;
	this.idId = idId;
    }

    public void run() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("UPDATE  `WarSim`.`Missile` SET `Intercepted` = '1', `InterceptionTime` =  ? , `InterceptedBy` = ? WHERE `Missile`.`ID` = ? AND  `Missile`.`WarName` = ?");
	    statement.setTimestamp(1,
		    new Timestamp(System.currentTimeMillis()));
	    statement.setString(2, idId);
	    statement.setString(3, mId);
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
