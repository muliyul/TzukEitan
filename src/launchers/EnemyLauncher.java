package launchers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.OneToOne;


import listeners.WarEventListener;
import missiles.EnemyMissile;
import model.War;
import model.WarStatistics;
import utils.IdGenerator;
import utils.Utils;
import db.DBFactory;

@Entity
public class EnemyLauncher extends Thread implements Munitions, Serializable {
    private static final long serialVersionUID = -5934215378852248255L;
    private transient List<WarEventListener> allListeners;
    @Id
    private String id;  
    private String warName;  
    private String destination;
    private transient int damage;
    private transient int flyTime;
    private boolean isHidden;
    private boolean firstHiddenState;
    private boolean beenHit = false;
    private transient WarStatistics statistics;
    private transient EnemyMissile currentMissile; 
    @OneToOne
    private War w;

    public War getW() {
		return w;
	}

	public void setW(War w) {
		this.w = w;
		if(this.w != null){
			w.addLauncher(this);
		}
	}

	protected EnemyLauncher() {
    }

    public EnemyLauncher(String id, boolean isHidden, War w,
	    WarStatistics statistics) {
	this.id = id;
	this.isHidden = isHidden;
	this.statistics = statistics;
	this.warName = w.getWarName();
	this.w = w;
	allListeners = new LinkedList<WarEventListener>();
	firstHiddenState = isHidden;
	
    }

    public void run() {
	// this thread will be alive until he will be hit
	while (!beenHit) {
	    synchronized (this) {
		try {
		    // Wait until user want to fire a missile
		    wait();

		}
		// Exception is called when launcher has been hit
		catch (InterruptedException ex) {
		    // firehasBeenHitEvent() ==> not needed because
		    // the DefenseDestructorMissile call this event
		    stopRunning();
		    break;
		}
	    }// synchronized
	    try {
		launchMissile();
	    } catch (InterruptedException e) {
		stopRunning();
		break;
		// e.printStackTrace();
	    }

	    // update that this launcher is not in use
	    currentMissile = null;

	}// while

    }// run

    // setting the next missile the user want to launch
    public void setMissileInfo(String destination, int damage, int flyTime) {
	this.destination = destination;
	this.damage = damage;
	this.flyTime = flyTime;
    }

    // launch missile with given parameters
    @Override
    public void launchMissile() throws InterruptedException {
	createMissile();

	// Missile isn't hidden when launching a missile
	isHidden = false;
	if (firstHiddenState)
	    // throw event if he was hidden
	    fireEnemyLauncherIsVisibleEvent(true);

	// It's take time to launch missile
	sleep(Utils.LAUNCH_DURATION);

	// throw event
	fireLaunchMissileEvent(currentMissile.getMId());

	currentMissile.start();

	// X time that the launcher is not hidden:
	int x = flyTime * Utils.SECOND;
	sleep(x);

	// returning the first hidden state:
	isHidden = firstHiddenState;
	if (firstHiddenState)
	    // throw event if he is back to be hidden
	    fireEnemyLauncherIsVisibleEvent(false);

	// wait until the missile will finish
	currentMissile.join();
    }

    // Create new missile
    public void createMissile() {
	// generate missile id
	String missileId = IdGenerator.enemyMissileIdGenerator();

	// create new missile
	currentMissile =
		new EnemyMissile(missileId, destination, flyTime, damage, this,
			statistics, warName);

	DBFactory.getInstance().addMissile(currentMissile); // add missile to DB

	// register listeners
	for (WarEventListener l : allListeners)
	    currentMissile.registerListeners(l);
    }

    // check if there is alive missile in the air
    public EnemyMissile getCurrentMissile() {
	if (currentMissile != null && currentMissile.isAlive())
	    return currentMissile;

	return null;
    }

    // Event
    private void fireEnemyLauncherIsVisibleEvent(boolean visible) {
	for (WarEventListener l : allListeners) {
	    l.enemyLauncherIsVisible(id, visible);
	}
    }

    // Event
    private void fireLaunchMissileEvent(String missileId) {
	for (WarEventListener l : allListeners) {
	    l.enemyLaunchMissile(id, missileId, destination, flyTime, damage);
	}

	// update statistics
	statistics.increaseNumOfLaunchMissiles();
    }

    public void registerListeners(WarEventListener listener) {
	allListeners.add(listener);
    }

    public String getLauncherId() {
	return id;
    }

    public boolean getIsHidden() {
	return isHidden;
    }

    // use the stop the thread when the launcher is been hit
    @Override
    public void stopRunning() {
	
	beenHit = true;
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

    public String getlId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    public int getDamage() {
	return damage;
    }

    public void setDamage(int damage) {
	this.damage = damage;
    }

    public int getFlyTime() {
	return flyTime;
    }

    public void setFlyTime(int flyTime) {
	this.flyTime = flyTime;
    }

    public boolean isFirstHiddenState() {
	return firstHiddenState;
    }

    public void setFirstHiddenState(boolean firstHiddenState) {
	this.firstHiddenState = firstHiddenState;
    }

    public boolean isBeenHit() {
	return beenHit;
    }

    public void setBeenHit(boolean beenHit) {
	this.beenHit = beenHit;
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

    public void setHidden(boolean isHidden) {
	this.isHidden = isHidden;
    }

    public void setCurrentMissile(EnemyMissile currentMissile) {
	this.currentMissile = currentMissile;
    }
}
