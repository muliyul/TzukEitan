package db;

import db.jdbc.JDBCConnection;
import db.jpa.JPAConnection;

public class DBFactory {
    private static DBConnection instance;

    /**
     * Types of DB Implementations available.
     * @author Muli
     *
     */
    public enum Type {
	JDBC, JPA
    }

    /**
     * Returns the first Instance of {@link DBConnection}, if it is
     * <code>null</code> it will return a new generated JDBCConnection
     * 
     * @return Instance of first generated {@link DBConnection} or an Instance
     *         of {@link JDBCConnection} if called before
     *         <code>setInstance()</code>.
     */
    public static DBConnection getInstance() {
	if (instance == null) {
	    instance = setInstance(Type.JDBC);
	}
	return instance;
    }

    /**
     * Sets the type of {@link DBConnection} used. May only be used once!
     * further calls will return the first instance created.
     * @param type - Type of the instance from {@link Type}
     * @return {@link DBConnection} object.
     */
    public static DBConnection setInstance(Type type) {
	if (instance == null) {
	    switch (type) {
	    case JDBC:
		instance = JDBCConnection.getInstance();
		break;
	    case JPA:
		instance = JPAConnection.getInstance();
		break;
	    default:
		instance = JDBCConnection.getInstance();
		break;
	    }
	}
	return instance;
    }

    public static DBConnection setInstance(DBConnection db) {
	if(instance == null){
	    instance = db;
	}
	return instance;
    }
}
