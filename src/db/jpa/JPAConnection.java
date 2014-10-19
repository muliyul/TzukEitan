package db.jpa;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import launchers.EnemyLauncher;
import launchers.IronDome;
import launchers.LauncherDestructor;
import missiles.DefenseDestructorMissile;
import missiles.EnemyMissile;
import model.War;
import db.DBConnection;

public class JPAConnection implements DBConnection {

    private static JPAConnection instance;
    private EntityManager connection;
    private Semaphore executer;
    private ExecutorService es;

    public static DBConnection getInstance() {
	if (instance == null)
	    return instance = new JPAConnection();
	return instance;
    }

    private JPAConnection() {
	/**dbUrl = "jdbc:mysql://104.131.232.248/WarSimJPA";
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
	}*/
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("config");
	connection = emf.createEntityManager();
	es = Executors.newFixedThreadPool(1);
	executer = new Semaphore(1, true);
    }

    @Override
    public boolean checkWarName(String warName) {
	try {
	    return es.submit(new CheckWarNameExistsTask(executer, connection, warName)).get();
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	return false;
    }

    @Override
    public void addNewWar(War w) {
	persistObject(w);
    }

    @Override
    public void endWar(War w) {
	es.submit(new MergeObjectTask(executer, connection, w));
    }

    @Override
    public void addLauncher(EnemyLauncher l) {
	persistObject(l);
    }

    @Override
    public void addMissile(EnemyMissile m) {
	persistObject(m);
    }

    @Override
    public void interceptMissile(EnemyMissile m, IronDome id) {
	es.submit(new MergeObjectTask(executer, connection, m));
	es.submit(new MergeObjectTask(executer, connection, id));
    }

    @Override
    public void interceptLauncher(EnemyLauncher l, DefenseDestructorMissile dm) {
	es.submit(new MergeObjectTask(executer, connection, l));
	es.submit(new MergeObjectTask(executer, connection, dm));
    }

    @Override
    public void addIronDome(IronDome id) {
	persistObject(id);
    }

    @Override
    public void addLauncherDestructor(LauncherDestructor ld) {
	persistObject(ld);
    }

    @Override
    public Future<String[]> getWarNamesByDate(LocalDate startDate,
	    LocalDate endDate) {
	return null;//es.submit(new GetWarNamesByDateTask(executer, connection, startDate, endDate));
    }

    @Override
    public Future<long[]> getWarStats(String warName) {
	return null;//es.submit(new GetWarStatsTask(executer, connection, warName));
    }

    private void persistObject(Object o) {
	es.submit(new PersistObjectTask(executer, connection, o));
    }

    @Override
    public void closeDB() {
	connection.close();
    }

}
