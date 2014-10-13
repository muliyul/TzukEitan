package missiles;

import utils.Utils;
import launchers.EnemyLauncher;
import model.WarStatistics;

/** Missile for Plane or Ship **/
public class DefenseDestructorMissile extends Thread {
	private String id;
	private String whoLaunchedMeId;
	private String whoLaunchedMeType;
	private EnemyLauncher launcherToDestroy;
	private WarStatistics statistics;

	public DefenseDestructorMissile(String id, EnemyLauncher LauncherToDestroy,
			String whoLunchedMeId, String whoLaunchedMeType,
			WarStatistics statistics) {

		this.id = id;
		this.launcherToDestroy = LauncherToDestroy;
		this.whoLaunchedMeId = whoLunchedMeId;
		this.whoLaunchedMeType = whoLaunchedMeType;
		this.statistics = statistics;
	}

	public void run() {
		synchronized (launcherToDestroy) {
			if (launcherToDestroy.isAlive() && !launcherToDestroy.getIsHidden()
					&& Utils.randomDestractorSucces()) {
				// Check if the launcher is hidden or not
				launcherToDestroy.interrupt();
			}
		}// synchronized
	
		if(launcherToDestroy.isInterrupted()){
			
		}else {
		}
	}

	// Event
	private void fireHitEvent() {
		statistics.increaseNumOfLauncherDestroyed();
	}

	public String getMissileId() {
		return id;
	}

}
