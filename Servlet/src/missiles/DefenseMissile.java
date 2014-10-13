package missiles;

import utils.Utils;
import model.WarStatistics;

/** Missile for iron dome **/
public class DefenseMissile extends Thread {
	private String id;
	private String whoLaunchedMeId;
	private EnemyMissile missileToDestroy;
	private WarStatistics statistics;

	public DefenseMissile(String id, EnemyMissile missileToDestroy,
			String whoLunchedMeId, WarStatistics statistics) {
		this.id = id;
		this.missileToDestroy = missileToDestroy;
		this.whoLaunchedMeId = whoLunchedMeId;
		this.statistics = statistics;
	}

	public void run() {
		synchronized (missileToDestroy) {
			// Check if the missile is still in the air before trying to destroy
			if (missileToDestroy.isAlive() && Utils.randomSuccesRate()) {
				missileToDestroy.interrupt();
			}
		}//synchronized
				
		if (missileToDestroy.isInterrupted()){
			fireHitEvent();
		}
		else{
		}
	}// run

	// Event
	private void fireHitEvent() {
		// update statistics
		statistics.increaseNumOfInterceptMissiles();
		//update DB
	}


	public String getMissileId() {
		return id;
	}

}
