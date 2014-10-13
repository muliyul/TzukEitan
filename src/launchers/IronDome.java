package launchers;

import java.util.LinkedList;
import java.util.List;

import db.DBConnection;
import utils.IdGenerator;
import utils.Utils;
import listeners.WarEventListener;
import missiles.DefenseMissile;
import missiles.EnemyMissile;
import model.WarLogger;
import model.WarStatistics;

public class IronDome extends Thread implements Munitions {
    private List<WarEventListener> allListeners;
    private String warName;
    private String id;
    private transient boolean isRunning = true;
    private transient boolean isBusy = false;
    private transient EnemyMissile toDestroy;
    private transient WarStatistics statistics;
    private transient DefenseMissile currentMissile;
    private DBConnection db;
    
    public IronDome() {
    }

    public IronDome(String id, String warName, WarStatistics statistics, DBConnection db) {
	allListeners = new LinkedList<WarEventListener>();
	this.db = db;
	this.statistics = statistics;
	this.id = id;
	this.warName = warName;
	WarLogger.addLoggerHandler("IronDome", id);
    }

    public void run() {
	// this thread will be alive until the war is over
	while (isRunning) {
	    synchronized (this) {
		try {
		    wait();
		} catch (InterruptedException e) {
		    stopRunning();
		    break;
		}
	    }

	    // update that this iron-dome is in use
	    isBusy = true;
	    try {
		launchMissile();

		// update that this iron dome is not in use
		isBusy = false;
	    } catch (InterruptedException e) {
		stopRunning();
		break;
	    }

	    // update that is no missile in this iron-dome
	    currentMissile = null;

	}// while

	// close the handler of the logger
	WarLogger.closeMyHandler(id);
	// ironDomeHandler.close();
    }// run

    // set the next target of this iron dome, called from the war
    public void setMissileToDestroy(EnemyMissile toDestroy) {
	this.toDestroy = toDestroy;
    }

    // Launch missile with given parameters
    public void launchMissile() throws InterruptedException {
	createMissile();

	// sleep for launch time
	sleep(Utils.LAUNCH_DURATION);

	// check if the target is still alive
	if (toDestroy != null && toDestroy.isAlive()) {
	    // throw event
	    fireLaunchMissileEvent(currentMissile.getMissileId());

	    // Start missile and wait until he will finish to be able
	    // to shoot anther one
	    currentMissile.start();
	    currentMissile.join();
	} else {
	    fireMissileNotExist(toDestroy.getMissileId());
	}
    }

    private void fireMissileNotExist(String missileId) {
	for (WarEventListener l : allListeners)
	    l.missileNotExist(getIronDomeId(), missileId);
    }

    public void createMissile() {
	// generate missile id
	String missieId = IdGenerator.defensMissileIdGenerator();

	// create new missile
	currentMissile = new DefenseMissile(missieId, toDestroy, this, statistics, db);

	// register listeners
	for (WarEventListener l : allListeners)
	    currentMissile.registerListeners(l);
    }

    public DefenseMissile getCurrentMissile() {
	return currentMissile;
    }

    // Event
    private void fireLaunchMissileEvent(String missileId) {
	for (WarEventListener l : allListeners) {
	    l.defenseLaunchMissile(id, missileId, toDestroy.getMissileId());
	}
    }

    public void registerListeners(WarEventListener listener) {
	allListeners.add(listener);
    }

    // check if can shoot from this current iron dome
    public boolean getIsBusy() {
	return isBusy;
    }

    public String getIronDomeId() {
	return id;
    }

    // use for end the thread
    @Override
    public void stopRunning() {
	currentMissile = null;
	toDestroy = null;
	isRunning = false;
    }

    public String getWarName() {
	return warName;
    }

}
