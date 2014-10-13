package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import db.jdbc.*;

public class DBConnection {

    private static Connection connection;
    private static String dbUrl;
    private static Semaphore executer;
    private static ExecutorService es = Executors.newFixedThreadPool(10);

    static {
	dbUrl = "jdbc:mysql://104.131.232.248/WarSim";

	try {
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    connection =
		    DriverManager.getConnection(dbUrl, "YoniMuli", "123456");
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    while (e != null) {
		System.out.println(e.getMessage());
		e = e.getNextException();
	    }
	}
	executer = new Semaphore(1, true);
    }

    public static boolean checkWarName(String warName) {
	try {
	    PreparedStatement statement =
		    connection
			    .prepareStatement("SELECT WarName FROM `WarSim`.`War` WHERE `War`.`WarName` =  ?");
	    statement.setString(1, warName);
	    ResultSet rs = statement.executeQuery();
	    return !rs.first();
	} catch (SQLException e) {
	    while (e != null) {
		System.out.println(e.getMessage());
		e = e.getNextException();
	    }
	    return true;
	}

    }

    public static void addNewWar(String warName) {
	es.execute(new AddNewWarTask(executer, connection, warName));
    }

    public static void endWar(String warName) {
	es.execute(new EndWarTask(executer, connection, warName));

    }

    public static void addLauncher(String launcherId, String warName) {
	es.execute(new AddLauncherTask(executer, connection, warName,
		launcherId));

    }

    public static void addMissile(String mId, String lId, String destination,
	    int dmg, int flyTime, String warName) {
	es.execute(new AddMissileTask(executer, connection, warName, mId,
		lId, destination, dmg, flyTime));

    }

    public static void interceptedMissile(String mId, String idId,
	    String warName) {
	es.execute(new Thread(new InterceptMissileTask(executer,
		connection, warName, mId, idId)));
    }

    public static void interceptedLauncher(String lId, String dId,
	    String warName) {
	es.execute(new Thread(new InterceptLauncherTask(executer,
		connection, warName, lId, dId)));
    }

    public static void addIronDome(String idId, String warName) {
	es.execute(new Thread(new AddIronDomeTask(executer, connection,
		warName, idId)));
    }

    public static void addLauncherDestructor(String id, String type,
	    String warName) {
	es.execute(new Thread(new AddLauncherDestructorTask(executer,
		connection, warName, id, type)));
    }

    public static Future<String[]> getWarNamesByDate(String startDate,
	    String endDate) {
	return es.submit(new WarNamesQueryByDateTask(executer, connection,
		startDate, endDate));
    }

    public static Future<long[]> getWarStats(String warName) {
	return es
		.submit(new GetWarStatsTask(executer, connection, warName));
    }

    public static void closeDB() {
	try {
	    if (connection != null) {
		connection.close();
	    }
	} catch (Exception e) {
	    System.out.println("Could not close the current connection.");
	    e.printStackTrace();
	}
    }
}
