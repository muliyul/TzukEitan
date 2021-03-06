package launchers;

import java.util.LinkedList;
import java.util.List;

import utils.IdGenerator;
import utils.Utils;
import missiles.DefenseDestructorMissile;
import model.WarStatistics;

/** Plane or Ship **/
public class LauncherDestructor extends Thread implements Munitions{

	private String id;
	private String type; // plane or ship
	private boolean isRunning = true;
	private boolean isBusy = false;
	private EnemyLauncher toDestroy;
	private WarStatistics statistics;
	private DefenseDestructorMissile currentMissile;
	private String warName;

	public LauncherDestructor(String type, String id, String warName, WarStatistics statistics) {

		this.id = id;
		this.type = Utils.capitalize(type);
		this.statistics = statistics;
		this.warName = warName;
	}

	public void run() {

		while (isRunning) {
			synchronized (this) {
				try {
					// Wait until user try to destroy missile
					wait();
				} catch (InterruptedException ex) {
					// used for end the war
					stopRunning();
					break;
				}
			}// synchronized
			// with this boolean you can see if the launcher is available to use
			isBusy = true;

			// checking if the missile you would like to destroy is alive (as a thread)
			// is not null (if there is any missile) and if he isn't hidden
			try{
				if (toDestroy != null && toDestroy.isAlive() && !toDestroy.getIsHidden()) {
					launchMissile();
					
				} else {
					if (toDestroy != null){}
				}
			} catch (InterruptedException e) {
				//e.printStackTrace();
				stopRunning();
				break;
			}
			
			// update that the launcher is not in use
			isBusy = false;
			
			//update that there is no missile to this launcher
			currentMissile = null;
		}// while
		
		// close the handler of the logger
	}// run

	
	// set the next target of this launcher destructor, called from the war
	public void setEnemyLauncherToDestroy(EnemyLauncher toDestroy) {
		this.toDestroy = toDestroy;
	}

	public void launchMissile() throws InterruptedException {
		// create launcher destructor missile
		createMissile();

		// sleep for launch time;
		sleep(Utils.LAUNCH_DURATION);

		if (toDestroy != null && toDestroy.isAlive() && !toDestroy.getIsHidden()) {
			// Throw event

			// Start missile and wait until he will finish to be able
			// to shoot anther one
			currentMissile.start();
			currentMissile.join();
		}
		else{
			if (toDestroy.getIsHidden()){
			}
			else{
			}
		}
	}



	public void createMissile() {
		// generate missile id
		String MissileId = IdGenerator.defenseDestractorLauncherMissileIdGenerator(type.charAt(0));

		// create new missile
		currentMissile = new DefenseDestructorMissile(MissileId, toDestroy, id,
				type, statistics);

		// register all listeners
	}

	public DefenseDestructorMissile getCurrentMissile() {
		return currentMissile;
	}

	// check if can shoot from this current launcher destructor
	public boolean getIsBusy() {
		return isBusy;
	}

	public String getDestructorId() {
		return id;
	}

	@Override
	// use for end the thread
	public void stopRunning() {
		toDestroy = null;
		isRunning = false;
	}
}
