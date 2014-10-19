package launchers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;

import utils.IdGenerator;
import utils.Utils;
import listeners.WarEventListener;
import missiles.DefenseDestructorMissile;
import model.War;
import model.WarLogger;
import model.WarStatistics;

/** Plane or Ship **/
@Entity

public class LauncherDestructor extends Thread implements Munitions,
	Serializable {
    private static final long serialVersionUID = 4233050750050993350L;
    private transient List<WarEventListener> allListeners;
    @Id
    private String id;
    
    private String warName;
    
    private String type; // plane or ship
    private boolean isRunning = true;
    private boolean isBusy = false;
    private transient EnemyLauncher toDestroy;
    private transient WarStatistics statistics;
    private transient DefenseDestructorMissile currentMissile;
    @OneToOne
    private War w;

    public War getW() {
		return w;
	}

	public void setW(War w) {
		this.w = w;
		if(this.w != null){
			w.addLauncherDestructor(this);
		}
	}

	protected LauncherDestructor() {
    }

    public LauncherDestructor(String type, String id, War w,
	    WarStatistics statistics) {
	allListeners = new LinkedList<WarEventListener>();
	this.id = id;
	this.type = Utils.capitalize(type);
	this.statistics = statistics;
	this.warName = w.getWarName();
	this.w = w;
	WarLogger.addLoggerHandler(this.type, id);
    }

    public void run() {

	while (isRunning) {
	    synchronized (this) {
		try {
		    // Wait until user try to destroy missile
		    wait();
		} catch (InterruptedException ex) {
		    // used for end the war
		    stopRunning();
		    break;
		}
	    }// synchronized
	     // with this boolean you can see if the launcher is available to
	     // use
	    isBusy = true;

	    // checking if the missile you would like to destroy is alive (as a
	    // thread)
	    // is not null (if there is any missile) and if he isn't hidden
	    try {
		if (toDestroy != null && toDestroy.isAlive()
			&& !toDestroy.getIsHidden()) {
		    launchMissile();

		} else {
		    if (toDestroy != null)
			fireLauncherIsHiddenEvent(toDestroy.getLauncherId());
		}
	    } catch (InterruptedException e) {
		// e.printStackTrace();
		stopRunning();
		break;
	    }

	    // update that the launcher is not in use
	    isBusy = false;

	    // update that there is no missile to this launcher
	    currentMissile = null;
	}// while

	// close the handler of the logger
	WarLogger.closeMyHandler(id);
    }// run

    // set the next target of this launcher destructor, called from the war
    public void setEnemyLauncherToDestroy(EnemyLauncher toDestroy) {
	this.toDestroy = toDestroy;
    }

    public void launchMissile() throws InterruptedException {
	// create launcher destructor missile
	createMissile();

	// sleep for launch time;
	sleep(Utils.LAUNCH_DURATION);

	if (toDestroy != null && toDestroy.isAlive()
		&& !toDestroy.getIsHidden()) {
	    // Throw event
	    fireLaunchMissileEvent(currentMissile.getMissileId());

	    // Start missile and wait until he will finish to be able
	    // to shoot anther one
	    currentMissile.start();
	    currentMissile.join();
	} else {
	    if (toDestroy.getIsHidden()) {
		fireLauncherIsHiddenEvent(toDestroy.getLauncherId());
	    } else {
		fireLauncherNotExist(toDestroy.getLauncherId());
	    }
	}
    }

    public void createMissile() {
	// generate missile id
	String MissileId =
		IdGenerator.defenseDestractorLauncherMissileIdGenerator(type
			.charAt(0));

	// create new missile
	currentMissile =
		new DefenseDestructorMissile(MissileId, toDestroy, this,
			statistics);

	// register all listeners
	for (WarEventListener l : allListeners)
	    currentMissile.registerListeners(l);
    }

    public DefenseDestructorMissile getCurrentMissile() {
	return currentMissile;
    }

    // Event
    private void fireLaunchMissileEvent(String missileId) {
	for (WarEventListener l : allListeners) {
	    l.defenseLaunchMissile(id, type, missileId,
		    toDestroy.getLauncherId());
	}
    }

    // Event
    private void fireLauncherIsHiddenEvent(String launcherId) {
	for (WarEventListener l : allListeners) {
	    l.defenseMissInterceptionHiddenLauncher(id, type, launcherId);
	}
    }

    // Event
    private void fireLauncherNotExist(String launcherId) {
	for (WarEventListener l : allListeners)
	    l.enemyLauncherNotExist(id, launcherId);
    }

    public void registerListeners(WarEventListener listener) {
	allListeners.add(listener);
    }

    // check if can shoot from this current launcher destructor
    public boolean getIsBusy() {
	return isBusy;
    }

    public String getDestructorId() {
	return id;
    }

    @Override
    // use for end the thread
    public void stopRunning() {
	toDestroy = null;
	isRunning = false;
    }

    public String getWarName() {
	return warName;
    }

    public String getType() {
	return type;
    }

    public List<WarEventListener> getAllListeners() {
	return allListeners;
    }

    public void setAllListeners(List<WarEventListener> allListeners) {
	this.allListeners = allListeners;
    }

    public String getLid() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public boolean isRunning() {
	return isRunning;
    }

    public void setRunning(boolean isRunning) {
	this.isRunning = isRunning;
    }

    public EnemyLauncher getToDestroy() {
	return toDestroy;
    }

    public void setToDestroy(EnemyLauncher toDestroy) {
	this.toDestroy = toDestroy;
    }

    public WarStatistics getStatistics() {
	return statistics;
    }

    public void setStatistics(WarStatistics statistics) {
	this.statistics = statistics;
    }

    public void setType(String type) {
	this.type = type;
    }

    public void setBusy(boolean isBusy) {
	this.isBusy = isBusy;
    }

    public void setCurrentMissile(DefenseDestructorMissile currentMissile) {
	this.currentMissile = currentMissile;
    }

    public void setWarName(String warName) {
	this.warName = warName;
    }
}
