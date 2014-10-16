package db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
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

public class JDBCConnection implements DBConnection {

    private Connection connection;
    private String dbUrl;
    private Semaphore executer;
    private ExecutorService es;
    private static JDBCConnection instance;

    public static JDBCConnection getInstance() {
	if (instance == null) {
	    instance = new JDBCConnection();
	}
	return instance;
    }

    private JDBCConnection() {
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
	es = Executors.newSingleThreadExecutor();
	executer = new Semaphore(1, true);
    }

    public Future<Boolean> checkWarName(String warName) {
	return es.submit(new CheckWarNameExistsTask(executer, connection,
		warName));
    }

    public void addNewWar(War w) {
	es.submit(new AddNewWarTask(executer, connection, w.getWarName()));
    }

    public void endWar(War w) {
	try {
	    es.submit(new EndWarTask(executer, connection, w.getWarName())).get();
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
    }

    public void addLauncher(EnemyLauncher l) {
	es.submit(new AddLauncherTask(executer, connection, l.getWarName(), l
		.getLauncherId()));

    }

    public void addMissile(EnemyMissile m) {
	es.submit(new AddMissileTask(executer, connection, m.getWarName(), m
		.getMissileId(), m.getLauncher(), m.getDestination(), m
		.getDamage(), m.getFlyTime()));

    }

    public void interceptMissile(EnemyMissile m, IronDome id) {
	es.submit(new InterceptMissileTask(executer, connection,
		m.getWarName(), m.getMissileId(), id.getIronDomeId()));
    }

    public void interceptLauncher(EnemyLauncher l, DefenseDestructorMissile m) {
	es.submit(new InterceptLauncherTask(executer, connection, l
		.getWarName(), l.getLauncherId(), m.getWhoLaunchedMe()
		.getDestructorId()));
    }

    public void addIronDome(IronDome id) {
	es.submit(new AddIronDomeTask(executer, connection, id.getWarName(), id
		.getIronDomeId()));
    }

    public void addLauncherDestructor(LauncherDestructor ld) {
	es.submit(new AddLauncherDestructorTask(executer, connection, ld
		.getWarName(), ld.getDestructorId(), ld.getType()));
    }

    public Future<String[]> getWarNamesByDate(LocalDate startDate,
	    LocalDate endDate) {
	return es.submit(new GetWarNamesByDateTask(executer, connection,
		startDate, endDate));
    }

    public Future<long[]> getWarStats(String warName) {
	return es.submit(new GetWarStatsTask(executer, connection, warName));
    }

    public void closeDB() {
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
