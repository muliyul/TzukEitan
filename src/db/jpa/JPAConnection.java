package db.jpa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import launchers.EnemyLauncher;
import launchers.IronDome;
import launchers.LauncherDestructor;
import missiles.DefenseDestructorMissile;
import missiles.EnemyMissile;
import model.War;
import db.DBConnection;
import db.jdbc.CheckWarNameExistsTask;
import db.jdbc.GetWarNamesByDateTask;
import db.jdbc.GetWarStatsTask;

public class JPAConnection implements DBConnection {

    private static JPAConnection instance;
    private Connection connection;
    private String dbUrl;
    private Semaphore executer;
    private ExecutorService es;

    public static DBConnection getInstance() {
	if (instance == null)
	    return instance = new JPAConnection();
	return instance;
    }

    private JPAConnection() {
	dbUrl = "jdbc:mysql://104.131.232.248/WarSimJPA";
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
	es = Executors.newFixedThreadPool(1);
	executer = new Semaphore(1, true);
    }

    @Override
    public Future<Boolean> checkWarName(String warName) {
	return es.submit(new CheckWarNameExistsTask(executer, connection, warName));
    }

    @Override
    public void addNewWar(War w) {
	persistObjects(w);
    }

    @Override
    public void endWar(War w) {
	persistObjects(w);
    }

    @Override
    public void addLauncher(EnemyLauncher l) {
	persistObjects(l);
    }

    @Override
    public void addMissile(EnemyMissile m) {
	persistObjects(m);
    }

    @Override
    public void interceptMissile(EnemyMissile m, IronDome id) {
	persistObjects(m, id);
    }

    @Override
    public void interceptLauncher(EnemyLauncher l, DefenseDestructorMissile dm) {
	persistObjects(l, dm);
    }

    @Override
    public void addIronDome(IronDome id) {
	persistObjects(id);
    }

    @Override
    public void addLauncherDestructor(LauncherDestructor ld) {
	persistObjects(ld);
    }

    @Override
    public Future<String[]> getWarNamesByDate(LocalDate startDate,
	    LocalDate endDate) {
	return es.submit(new GetWarNamesByDateTask(executer, connection, startDate, endDate));
    }

    @Override
    public Future<long[]> getWarStats(String warName) {
	return es.submit(new GetWarStatsTask(executer, connection, warName));
    }

    private void persistObjects(Object... objects) {
	es.submit(new PersistObjectTask(executer, connection, objects));
    }

    @Override
    public void closeDB() {
	try {
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
