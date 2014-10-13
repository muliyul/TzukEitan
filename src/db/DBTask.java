package db;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;


public class DBTask<T> implements Callable<T> {

    protected Semaphore executer;
    protected Connection connection;
    protected String warName;
    
    public DBTask(Semaphore s, Connection c, String warName) {
	this.executer = s;
	this.connection = c;
	this.warName = warName;
    }

    @Override
    public T call() throws Exception {
	return null;
    }

}
