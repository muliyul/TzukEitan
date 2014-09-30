package TzukEitan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class TzukEitanDBConnection {

	private static Connection connection;
	private static String dbUrl;
	private static ResultSet rs;
	private static PreparedStatement statement;
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
			statement = connection
					.prepareStatement("SELECT WarName FROM `WarSim`.`War` WHERE `War`.`WarName` =  ?");
			statement.setString(1, warName);
			rs = statement.executeQuery();
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
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("INSERT INTO `WarSim`.`War` (`WarName`, `StartTime`) VALUES ( ?, ?)");
					statement.setString(1, warName);
					statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
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
		}.start();
	}

	public static void endWar(String warName) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
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
		}.start();

	}

	public static void addLauncher(String launcherId, String warName) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("INSERT INTO `WarSim`.`EnemyLauncher` (`ID`, `WarName`) VALUES (?, ?)");
					statement.setString(1, launcherId);
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
		}.start();

	}

	public static void addMissile(String mId, String lId, String destination,
			int dmg, int flyTime, String warName) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("INSERT INTO `WarSim`.`Missile` (`ID`, `Launcher`, `LaunchTime`,"
									+ "		`Destination`, `Damage`, `FlyTime`, `WarName`) VALUES (?, ?, ? , ?, ?, ?, ?)");
					statement.setString(1, mId);
					statement.setString(2, lId);
					statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
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
			}
		}.start();

	}

	public static void interceptedMissile(String mId, String idId,
			String warName) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("UPDATE  `WarSim`.`Missile` SET `Intercepted` = '1', `InterceptionTime` =  ? , `InterceptedBy` = ? WHERE `Missile`.`ID` = ? AND  `Missile`.`WarName` = ?");
					statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
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
		}.start();
	}

	public static void interceptedLauncher(String lId, String dId,
			String warName) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("UPDATE  `WarSim`.`EnemyLauncher` SET `Intercepted` = '1', `InterceptionTime` =  ? , `InterceptedBy` = ? WHERE `EnemyLauncher`.`ID` = ? AND  `EnemyLauncher`.`WarName` = ?");
					statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
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
		}.start();
	}

	public static void addIronDome(String idId, String warName) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("INSERT INTO `WarSim`.`IronDome` (`ID`, `WarName`) VALUES (?, ?)");
					statement.setString(1, idId);
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
		}.start();
	}

	public static void addLauncherDestructor(String id, String type, String name) {
		new Thread() {
			@Override
			public void run() {
				try {
					executer.acquire();
					statement = connection
							.prepareStatement("INSERT INTO `WarSim`.`LauncherDestructor` (`ID`, `Type`, `WarName`) VALUES (?, ?, ?)");
					statement.setString(1, id);
					statement.setString(2, type);
					statement.setString(3, name);
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
		}.start();
	}
	
	public static void getWarNamesByDate(String startDate, String endDate){
		
		new Thread() {
			@Override
			public void run() {
				try {
					String fixedStartDate = startDate + " 00:00:00";
					String fixedEndtDate = startDate + " 23:59:59";
					executer.acquire();
					statement = connection
							.prepareStatement("SELECT WarName FROM `WarSim`.`War` WHERE `War`.`StartTime` BETWEEN ? AND ?");
					statement.setString(1, fixedStartDate);
					statement.setString(2, fixedEndtDate);
					
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
		}.start();
	}

	public static void closeDB() {
		try {
			if (connection != null) {
				connection.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			System.out.println("Could not close the current connection.");
			e.printStackTrace();
		}
	}
}
