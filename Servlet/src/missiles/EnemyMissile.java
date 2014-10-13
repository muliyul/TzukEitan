package missiles;

import java.io.Serializable;
import utils.Utils;
import model.WarStatistics;

/** Enemy missile, is been created by the Enemy launcher **/
public class EnemyMissile extends Thread implements Serializable{

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
					}
				}
			}
		}// finally
	}// run

	// Event
	private void fireHitEvent() {
		

		// update the war statistics
		statistics.increaseNumOfHitTargetMissiles();
		statistics.increaseTotalDamage(damage);
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
