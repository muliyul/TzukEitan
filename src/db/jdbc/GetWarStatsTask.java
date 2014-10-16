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
	    executer.acquire();
	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT `EndTime` FROM `War` WHERE `WarName` = ?");
	    statement.setString(1, warName);
	    ResultSet res = statement.executeQuery();
	    res.next();
	    Timestamp ts = res.getTimestamp("EndTime");
	    if (ts != null)
		stats[0] = 1;
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) AS MissileLaunched FROM `Missile` WHERE `WarName` = ?");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    res.next();
	    stats[1] = res.getInt("MissileLaunched");
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) AS MissileIntercepted FROM `Missile` WHERE `WarName` =  ?  AND `Intercepted` = '1'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    res.next();
	    stats[2] = res.getInt("MissileIntercepted");
	    stats[3] = stats[1] - stats[2];
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) AS LauncherIntercepted FROM `EnemyLauncher` WHERE `WarName` = ? AND `Intercepted` = '1'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    res.next();
	    stats[4] = res.getInt("LauncherIntercepted");
	    statement =
		    connection
			    .prepareStatement("SELECT SUM(`Damage`) AS TotalDamage FROM `Missile` WHERE `WarName` =  ? AND `Intercepted` = '0'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    res.next();
	    stats[5] = res.getInt("TotalDamage");
	} catch (SQLException e) {
	    while (e != null) {
		e.printStackTrace();
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
