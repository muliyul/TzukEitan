package db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Semaphore;

import db.DBTask;

public class EndWarTaskJDBC extends DBTask {
    private String warName;

    public EndWarTaskJDBC(Semaphore s, Connection c, String warName) {
	super(s, c, warName);
    }

    @Override
    public void run() {
	try {
		executer.acquire();
		PreparedStatement statement = connection
				.prepareStatement("UPDATE  `WarSim`.`War` SET  `EndTime` = ? WHERE  `War`.`WarName` =  ? ");
		statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		statement.setString(2, warName);
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
