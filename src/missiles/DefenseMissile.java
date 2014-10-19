package missiles;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import utils.Utils;
import launchers.IronDome;
import listeners.WarEventListener;
import model.WarStatistics;
import db.DBConnection;
import db.DBFactory;

/** Missile for iron dome **/
public class DefenseMissile extends Thread {
	private List<WarEventListener> allListeners;
	private String id;
	private IronDome whoLaunchedMe;
	private EnemyMissile missileToDestroy;
	private WarStatistics statistics;
	
	protected DefenseMissile() {
	}

	public DefenseMissile(String id, EnemyMissile missileToDestroy,
			IronDome whoLaunchedMeId, WarStatistics statistics) {
		allListeners = new LinkedList<WarEventListener>();
		this.id = id;
		this.missileToDestroy = missileToDestroy;
		this.whoLaunchedMe = whoLaunchedMeId;
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
					missileToDestroy.getMId());
		}

		// update statistics
		statistics.increaseNumOfInterceptMissiles();
		//update DB
		DBFactory.getInstance().interceptMissile(missileToDestroy, whoLaunchedMe);
	}

	// Event
	private void fireMissEvent() {
		for (WarEventListener l : allListeners) {
			l.defenseMissInterceptionMissile(whoLaunchedMe.getIronDomeId(), id,
					missileToDestroy.getMId(),
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
