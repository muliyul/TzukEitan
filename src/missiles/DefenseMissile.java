package missiles;

import java.util.LinkedList;
import java.util.List;

import utils.Utils;
import launchers.IronDome;
import listeners.WarEventListener;
import model.WarStatistics;
import db.DBConnection;

/** Missile for iron dome **/
public class DefenseMissile extends Thread {
	private List<WarEventListener> allListeners;

	private String id;
	private IronDome whoLaunchedMe;
	private EnemyMissile missileToDestroy;
	private WarStatistics statistics;
	private DBConnection db;

	public DefenseMissile(String id, EnemyMissile missileToDestroy,
			IronDome whoLunchedMeId, WarStatistics statistics, DBConnection db) {
		allListeners = new LinkedList<WarEventListener>();
		this.db = db;
		this.id = id;
		this.missileToDestroy = missileToDestroy;
		this.whoLaunchedMe = whoLunchedMeId;
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
			fireMissEvent();
		}
	}// run

	// Event
	private void fireHitEvent() {
		for (WarEventListener l : allListeners) {
			l.defenseHitInterceptionMissile(whoLaunchedMe.getIronDomeId(), id,
					missileToDestroy.getMissileId());
		}

		// update statistics
		statistics.increaseNumOfInterceptMissiles();
		//update DB
		db.interceptMissile(missileToDestroy, whoLaunchedMe);
	}

	// Event
	private void fireMissEvent() {
		for (WarEventListener l : allListeners) {
			l.defenseMissInterceptionMissile(whoLaunchedMe.getIronDomeId(), id,
					missileToDestroy.getMissileId(),
					missileToDestroy.getDamage());
		}
	}

	public void registerListeners(WarEventListener listener) {
		allListeners.add(listener);
	}

	public String getMissileId() {
		return id;
	}

}
