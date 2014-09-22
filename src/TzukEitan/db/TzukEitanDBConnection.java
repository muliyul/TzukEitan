package TzukEitan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class TzukEitanDBConnection {
	
	private static Connection   connection;
	private static String       dbUrl;
	private static ResultSet    rs;
	private static PreparedStatement statement;
	
	static {
		dbUrl = "jdbc:mysql://104.131.232.248/WarSim";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(dbUrl, "YoniMuli", "123456");
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
	
	public static Vector<String[]> getQueryData(String tableName, Vector<String> headers) {
		Vector<String[]> rowsData = new Vector<String[]>();
		try {
			statement = (PreparedStatement) connection.prepareStatement("SELECT * FROM " + tableName);
			rs = statement.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int numOfCols = meta.getColumnCount();

			// rebuild the headers array with the new column names
			headers.clear();
			for (int h = 1; h <= numOfCols; h++) {
				headers.add(meta.getColumnName(h));
			}
			
			while (rs.next()) {
				String[] record = new String[numOfCols];
				for (int i = 0; i < numOfCols; i++) {
					record[i] = rs.getString(i + 1);
				}
				rowsData.addElement(record);
			}
		} catch (SQLException e) {
			while (e != null) {
				e.printStackTrace();
				e = e.getNextException();
			}
		}
		return rowsData;
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
