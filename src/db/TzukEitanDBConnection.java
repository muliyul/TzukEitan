package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import db.jdbc.*;

public class TzukEitanDBConnection {

	private static Connection connection;
	private static String dbUrl;
	private static Semaphore executer;
	static {
		dbUrl = "jdbc:mysql://104.131.232.248/WarSim";

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(dbUrl, "YoniMuli",
					"123456");
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
			PreparedStatement statement = connection
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
		new Thread(new AddNewWarTaskJDBC(executer, connection, warName))
				.start();
	}

	public static void endWar(String warName) {
		new Thread(new EndWarTaskJDBC(executer, connection, warName)).start();

	}

	public static void addLauncher(String launcherId, String warName) {
		new Thread(new AddLauncherTaskJDBC(executer, connection, warName,
				launcherId)).start();

	}

	public static void addMissile(String mId, String lId, String destination,
			int dmg, int flyTime, String warName) {
		new Thread(new AddMissileTaskJDBC(executer, connection, warName, mId,
				lId, destination, dmg, flyTime)).start();

	}

	public static void interceptedMissile(String mId, String idId,
			String warName) {
		new Thread(new InterceptMissileTaskJDBC(executer, connection, warName,
				mId, idId)).start();
	}

	public static void interceptedLauncher(String lId, String dId,
			String warName) {
		new Thread(new InterceptLauncherTaskJDBC(executer, connection, warName,
				lId, dId)).start();
	}

	public static void addIronDome(String idId, String warName) {
		new Thread(new AddIronDomeTaskJDBC(executer, connection, warName, idId))
				.start();
	}

	public static void addLauncherDestructor(String id, String type,
			String warName) {
		new Thread(new AddLauncherDestructorTaskJDBC(executer, connection,
				warName, id, type)).start();
	}

	public static void getWarNamesByDate(String startDate, String endDate) {
		new Thread(new WarNamesQueryByDateTaskJDBC(executer, connection,
				startDate, endDate)).start();
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
