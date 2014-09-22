package TzukEitan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class TzukEitanDBConnection {

    private static Connection connection;
    private static String dbUrl;
    private static ResultSet rs;
    private static PreparedStatement statement;

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
    }
    
    public static void addLauncher(String launcherId){
	
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
