package missiles;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.Id;





import utils.Utils;
import launchers.EnemyLauncher;
import listeners.WarEventListener;
import model.WarStatistics;

/** Enemy missile, is been created by the Enemy launcher **/
@Entity
//@IdClass(CompositeMissileKey.class)
public class EnemyMissile extends Thread implements Serializable {
    private static final long serialVersionUID = 6981536134355062254L;
    @Id
    private String id;
    
    private String warName;

    
    private transient List<WarEventListener> allListeners;
    private String launcherID;
    private transient EnemyLauncher whoLaunchedMe;
    private String destination;
    private int flyTime;
    private int damage;
    private transient WarStatistics statistics;
    private String launchTime;
    private boolean beenHit = false;
    

    protected EnemyMissile() {
    }

    public EnemyMissile(String id, String destination, int flyTime, int damage,
	    EnemyLauncher whoLaunchedMeId, WarStatistics statistics,
	    String warName) {
	allListeners = new LinkedList<WarEventListener>();
	this.id = id;
	this.destination = destination;
	this.flyTime = flyTime;
	this.damage = damage;
	this.whoLaunchedMe = whoLaunchedMeId;
	this.statistics = statistics;
	this.warName =  warName;
	this.launcherID = whoLaunchedMeId.getLauncherId();
	
    }

    public void run() {
	launchTime = Utils.getCurrentTime();

	try {
	    // fly time
	    sleep(flyTime * Utils.SECOND);

	    // Interrupt is thrown when Enemy missile has been hit.
	} catch (InterruptedException ex) {
	    // this event was already being thrown by the missile (defense) who
	    // hit this
	    // missile.
	    synchronized (this) {
		beenHit = true;
	    }

	} finally {
	    synchronized (this) {
		if (!beenHit) {
		    if (Utils.randomSuccesRate()) {
			fireHitEvent();
		    } else {
			fireMissEvent();
		    }
		}
	    }
	}// finally
    }// run

    // Event
    private void fireHitEvent() {
	for (WarEventListener l : allListeners) {
	    l.enemyHitDestination(whoLaunchedMe.getLauncherId(), id,
		    destination, damage, launchTime);
	}

	// update the war statistics
	statistics.increaseNumOfHitTargetMissiles();
	statistics.increaseTotalDamage(damage);
    }

    // Event
    private void fireMissEvent() {
	for (WarEventListener l : allListeners) {
	    l.enemyMissDestination(whoLaunchedMe.getLauncherId(), id,
		    destination, launchTime);
	}
    }

    // Event
    public void registerListeners(WarEventListener listener) {
	allListeners.add(listener);
    }

    public String getMId() {
	return id;
    }

    public int getDamage() {
	return damage;
    }

    public boolean isBeenHit() {
	return beenHit;
    }

    public String getWarName() {
	return warName;
    }

    public String getDestination() {
	return destination;
    }

    public int getFlyTime() {
	return flyTime;
    }

    public void setStatistics(WarStatistics statistics) {
	this.statistics = statistics;
    }

    public EnemyLauncher getLauncher() {
	return whoLaunchedMe;
    }

    // JPA STUFF

    public List<WarEventListener> getAllListeners() {
	return allListeners;
    }

    public void setAllListeners(List<WarEventListener> allListeners) {
	this.allListeners = allListeners;
    }

    public void setMid(String id) {
	this.id = id;
    }

    public EnemyLauncher getWhoLaunchedMe() {
	return whoLaunchedMe;
    }

    public void setWhoLaunchedMe(EnemyLauncher whoLaunchedMe) {
	this.whoLaunchedMe = whoLaunchedMe;
    }

    public String getLaunchTime() {
	return launchTime;
    }

    public void setLaunchTime(String launchTime) {
	this.launchTime = launchTime;
    }

    public WarStatistics getStatistics() {
	return statistics;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    public void setFlyTime(int flyTime) {
	this.flyTime = flyTime;
    }

    public void setDamage(int damage) {
	this.damage = damage;
    }

    public void setWarName(String warName) {
	this.warName = warName;
    }

    public void setBeenHit(boolean beenHit) {
	this.beenHit = beenHit;
    }

   
}
