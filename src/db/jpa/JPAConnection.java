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
    if(connection.find(War.class, w.getWarName()) == null)
	persistObject(w);
    }

    @Override
    public void endWar(War w) { //not needed in JPA
	
    }

    @Override
    public void addLauncher(EnemyLauncher l) {
    if(connection.find(EnemyLauncher.class, l.getLauncherId()) == null)
	persistObject(l);
    }

    @Override
    public void addMissile(EnemyMissile m) {
    if(connection.find(EnemyMissile.class, m.getMId()) == null)
	persistObject(m);
    }

    @Override
    public void interceptMissile(EnemyMissile m, IronDome id) { //not needed in JPA

    }

    @Override
    public void interceptLauncher(EnemyLauncher l, DefenseDestructorMissile dm) { //not needed in JPA

    }

    @Override
    public void addIronDome(IronDome id) {
    if(connection.find(IronDome.class, id.getIdId()) == null)
	persistObject(id);
    }

    @Override
    public void addLauncherDestructor(LauncherDestructor ld) {
    if(connection.find(LauncherDestructor.class, ld.getLid()) == null)
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
