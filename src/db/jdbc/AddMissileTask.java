package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class AddMissileTask extends DBTask<Void> {

    private String mId;
    private String lId;
    private String destination;
    private int dmg;
    private int flyTime;

    public AddMissileTask(Semaphore s, Connection c, String warName,
	    String mId, String lId, String destination, int dmg, int flyTime) {
	super(s, c, warName);
	this.mId = mId;
	this.lId = lId;
	this.destination = destination;
	this.dmg = dmg;
	this.flyTime = flyTime;
    }

    @Override
    public Void call() {
	try {
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("INSERT INTO `WarSim`.`Missile` (`ID`, `Launcher`, `LaunchTime`,"
				    + "		`Destination`, `Damage`, `FlyTime`, `WarName`) VALUES (?, ?, ? , ?, ?, ?, ?)");
	    statement.setString(1, mId);
	    statement.setString(2, lId);
	    statement
		    .setTimestamp(3, new Timestamp(System.currentTimeMillis()));
	    statement.setString(4, destination);
	    statement.setInt(5, dmg);
	    statement.setInt(6, flyTime);
	    statement.setString(7, warName);
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
