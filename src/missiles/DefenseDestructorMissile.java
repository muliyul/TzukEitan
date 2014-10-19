package missiles;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import utils.Utils;
import launchers.EnemyLauncher;
import launchers.LauncherDestructor;
import listeners.WarEventListener;
import model.WarStatistics;
import db.DBFactory;

/** Missile for Plane or Ship **/
public class DefenseDestructorMissile extends Thread {
    private List<WarEventListener> allListeners;
    private String id;
    private LauncherDestructor whoLaunchedMe;
    private EnemyLauncher launcherToDestroy;
    private WarStatistics statistics;
    
    protected DefenseDestructorMissile() {
    }

    public DefenseDestructorMissile(String id, EnemyLauncher LauncherToDestroy,
	    LauncherDestructor whoLaunchedMe, WarStatistics statistics) {

	allListeners = new LinkedList<WarEventListener>();
	this.id = id;
	this.launcherToDestroy = LauncherToDestroy;
	this.whoLaunchedMe = whoLaunchedMe;
	this.statistics = statistics;
    }

    public void run() {
	synchronized (launcherToDestroy) {
	    if (launcherToDestroy.isAlive() && !launcherToDestroy.getIsHidden()
		    && Utils.randomDestractorSucces()) {
		// Check if the launcher is hidden or not
		launcherToDestroy.interrupt();
	    }
	}// synchronized

	if (launcherToDestroy.isInterrupted()) {
	    fireHitEvent();

	} else {
	    fireMissEvent();
	}
    }

    // Event
    private void fireHitEvent() {
	for (WarEventListener l : allListeners) {
	    l.defenseHitInterceptionLauncher(whoLaunchedMe.getDestructorId(), whoLaunchedMe.getType(),
		    id, launcherToDestroy.getLauncherId());
	}

	// update DB
	DBFactory.getInstance().interceptLauncher(launcherToDestroy, this);

	// update statistics
	statistics.increaseNumOfLauncherDestroyed();
    }

    // Event
    private void fireMissEvent() {
	for (WarEventListener l : allListeners) {
	    l.defenseMissInterceptionLauncher(whoLaunchedMe.getDestructorId(), whoLaunchedMe.getType(),
		    id, launcherToDestroy.getLauncherId());
	}
    }

    public void registerListeners(WarEventListener listener) {
	allListeners.add(listener);
    }

    public String getMissileId() {
	return id;
    }

    public LauncherDestructor getWhoLaunchedMe() {
	return whoLaunchedMe;
    }

}
