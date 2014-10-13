package launchers;

import java.io.Serializable;
import utils.IdGenerator;
import utils.Utils;
import missiles.EnemyMissile;
import model.WarStatistics;

public class EnemyLauncher extends Thread implements Munitions,Serializable{
	private String warName;
	private String id;
	private String destination;
	private int damage;
	private int flyTime;
	private boolean isHidden;
	private boolean firstHiddenState;
	private boolean beenHit = false;
	private WarStatistics statistics;
	private EnemyMissile currentMissile;

	public EnemyLauncher(String id, boolean isHidden, String warName, WarStatistics statistics) {
		this.id = id;
		this.isHidden = isHidden;
		this.statistics = statistics;
		this.warName = warName;
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
				//e.printStackTrace();
			}

			// update that this launcher is not in use
			currentMissile = null;

		}// while

		// close the handler of the logger
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
		// It's take time to launch missile
		sleep(Utils.LAUNCH_DURATION);

		// throw event
		fireLaunchMissileEvent(currentMissile.getMissileId());

		currentMissile.start();

		// X time that the launcher is not hidden:
		int x = flyTime * Utils.SECOND;
		sleep(x);

		// returning the first hidden state:
		isHidden = firstHiddenState;
		if (firstHiddenState){}
			// throw event if he is back to be hidden

		// wait until the missile will finish
		currentMissile.join();
	}

	// Create new missile
	public void createMissile() {
		// generate missile id
		String missileId = IdGenerator.enemyMissileIdGenerator();

		// create new missile
		currentMissile = new EnemyMissile(missileId, destination, flyTime,
				damage, id, statistics, warName);
		

		// register listeners
	}

	// check if there is alive missile in the air
	public EnemyMissile getCurrentMissile() {
		if (currentMissile != null && currentMissile.isAlive())
			return currentMissile;

		return null;
	}


	// Event
	private void fireLaunchMissileEvent(String missileId) {
		//update statistics
		statistics.increaseNumOfLaunchMissiles();
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
		currentMissile = null;
		beenHit = true;
	}
	
	public String getWarName() {
		return warName;
	}
}
