package missiles;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import utils.Utils;
import listeners.WarEventListener;
import model.WarStatistics;

/** Enemy missile, is been created by the Enemy launcher **/
public class EnemyMissile extends Thread implements Serializable{
    private static final long serialVersionUID = 6981536134355062254L;

	private List<WarEventListener> allListeners;

	private String id;
	private String whoLaunchedMeId;
	private String destination;
	private int flyTime;
	private int damage;
	private WarStatistics statistics;
	private String launchTime;
	private String warName;
	private boolean beenHit = false;

	public EnemyMissile(String id, String destination, int flyTime, int damage,
			String whoLaunchedMeId, WarStatistics statistics, String warName) {
		allListeners = new LinkedList<WarEventListener>();
		this.warName = warName;
		this.id = id;
		this.destination = destination;
		this.flyTime = flyTime;
		this.damage = damage;
		this.whoLaunchedMeId = whoLaunchedMeId;
		this.statistics = statistics;
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
			l.enemyHitDestination(whoLaunchedMeId, id, destination, damage,
					launchTime);
		}

		// update the war statistics
		statistics.increaseNumOfHitTargetMissiles();
		statistics.increaseTotalDamage(damage);
	}

	// Event
	private void fireMissEvent() {
		for (WarEventListener l : allListeners) {
			l.enemyMissDestination(whoLaunchedMeId, id, destination, launchTime);
		}
	}

	// Event
	public void registerListeners(WarEventListener listener) {
		allListeners.add(listener);
	}

	public String getMissileId() {
		return id;
	}

	public int getDamage() {
		return damage;
	}
	
	public boolean isBeenHit(){
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
}
