package launchers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import db.jpa.CompositeIronKey;
import utils.IdGenerator;
import utils.Utils;
import listeners.WarEventListener;
import missiles.DefenseMissile;
import missiles.EnemyMissile;
import model.War;
import model.WarLogger;
import model.WarStatistics;

@Entity
//@IdClass(CompositeIronKey.class)
public class IronDome extends Thread implements Munitions, Serializable {
    private static final long serialVersionUID = 4072250089090881271L;
    private transient List<WarEventListener> allListeners;
    @Id
    private String id;
   
    private String warName;
    
    private boolean isRunning = true;
    private boolean isBusy = false;
    private transient EnemyMissile toDestroy;
    private transient WarStatistics statistics;
    private transient DefenseMissile currentMissile;
  //  @OneToOne
    private transient War w;

    public IronDome() {
    }

    public IronDome(String id, War w, WarStatistics statistics) {
	allListeners = new LinkedList<WarEventListener>();
	this.statistics = statistics;
	this.id = id;
	this.warName = w.getWarName();
	this.w = w;
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
	    fireMissileNotExist(toDestroy.getMId());
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
	currentMissile =
		new DefenseMissile(missieId, toDestroy, this, statistics);

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
	    l.defenseLaunchMissile(id, missileId, toDestroy.getMId());
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

    public List<WarEventListener> getAllListeners() {
	return allListeners;
    }

    public void setAllListeners(List<WarEventListener> allListeners) {
	this.allListeners = allListeners;
    }

    public String getIdId() {
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

    public EnemyMissile getToDestroy() {
	return toDestroy;
    }

    public void setToDestroy(EnemyMissile toDestroy) {
	this.toDestroy = toDestroy;
    }

    public WarStatistics getStatistics() {
	return statistics;
    }

    public void setStatistics(WarStatistics statistics) {
	this.statistics = statistics;
    }

    public void setWarName(String warName) {
	this.warName = warName;
    }

    public void setBusy(boolean isBusy) {
	this.isBusy = isBusy;
    }

    public void setCurrentMissile(DefenseMissile currentMissile) {
	this.currentMissile = currentMissile;
    }

}
