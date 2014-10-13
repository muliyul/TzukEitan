package db.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.eclipse.persistence.jpa.jpql.parser.DateTime;

public class GetWarStatsTask implements Callable<long[]> {

    private Semaphore executer;
    private Connection connection;
    private String warName;

    public GetWarStatsTask(Semaphore s, Connection c, String warName) {
	this.executer = s;
	this.connection = c;
	this.warName = warName;
    }

    @Override
    public long[] call() throws Exception {
	long[] stats = new long[6];
	try {

	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT `EndTime` FROM `War` WHERE `WarName` = ?");
	    statement.setString(1, warName);
	    executer.acquire();
	    ResultSet res = statement.executeQuery();
	    if (res.getTimestamp("EndTime") != null)
		stats[0] = 1;
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) FROM `Missile` WHERE `WarName` = ?");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[1] = res.getInt(0);
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) FROM `Missile` WHERE `WarName` =  ?  AND `Intercepted` = '1'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[2] = res.getInt(0);
	    stats[3] = stats[1] - stats[2];
	    statement =
		    connection
			    .prepareStatement("SELECT COUNT(`ID`) FROM `EnemyLauncher` WHERE `WarName` = ? AND `Intercepted` = '1'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[4] = res.getInt(0);
	    statement =
		    connection
			    .prepareStatement("SELECT SUM(`Damage`) FROM `Missile` WHERE `WarName` =  ? AND `Intercepted` = '0'");
	    statement.setString(1, warName);
	    res = statement.executeQuery();
	    stats[5] = res.getInt(0);
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
	return stats;
    }

}
