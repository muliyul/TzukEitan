package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class GetWarStatsTask extends DBTask<long[]> {

    public GetWarStatsTask(Semaphore s, Connection c, String warName) {
	super(s, c, warName);
    }

    @Override
    public long[] call() throws Exception {
	long[] stats = new long[6];
	try {

	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT `War`.`EndTime` FROM `WarSim`.`War` WHERE `War`.`WarName` = ?");
	    statement.setString(1, warName);
	    executer.acquire();
	    ResultSet res = statement.executeQuery();
	    Timestamp ts = res.getTimestamp("EndTime");
	    if (ts != null)
		stats[0] = 1;
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) FROM `Missile` WHERE `WarName` = ?");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[1] = res.getInt(1);
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) FROM `Missile` WHERE `WarName` =  ?  AND `Intercepted` = '1'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[2] = res.getInt(1);
	    stats[3] = stats[1] - stats[2];
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) FROM `EnemyLauncher` WHERE `WarName` = ? AND `Intercepted` = '1'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[4] = res.getInt(1);
	    statement =
		    connection
			    .prepareStatement("SELECT SUM(`Damage`) FROM `Missile` WHERE `WarName` =  ? AND `Intercepted` = '0'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[5] = res.getInt(1);
	} catch (SQLException e) {
	    System.out.println(warName + " is problematic");
	    while (e != null) {
		System.out.println(e.getMessage());
		e = e.getNextException();
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} finally {
	    executer.release();
	}
	return stats;
    }

}
