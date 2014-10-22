package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import aspects.WarLogger;
import net.Server;
import utils.IdGenerator;
import launchers.EnemyLauncher;
import launchers.IronDome;
import launchers.LauncherDestructor;
import listeners.WarEventListener;
import missiles.EnemyMissile;
import db.DBFactory;

@Entity
public class War extends Thread implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 6577935402829490515L;
	private transient List<WarEventListener> allListeners;
	@Id
	private String warName;
	@OneToMany(mappedBy = "w", cascade = CascadeType.PERSIST)
	private List<IronDome> ironDomeArr;
	@OneToMany(mappedBy = "w", cascade = CascadeType.PERSIST)
	private List<LauncherDestructor> launcherDestractorArr;
	@OneToMany(mappedBy = "w", cascade = CascadeType.PERSIST)
	private List<EnemyLauncher> enemyLauncherArr;
	private transient WarStatistics statistics;
	private transient String[] targetCities = { "Sderot", "Ofakim", "Beer-Sheva",
			"Netivot", "Tel-Aviv", "Re'ut" };
	private transient Server warServer;

	protected War() {
	}

	public War(String warName) {
		ironDomeArr = new ArrayList<>();
		launcherDestractorArr = new ArrayList<>();
		enemyLauncherArr = new ArrayList<>();
		allListeners = new LinkedList<>();
		statistics = new WarStatistics();
		this.warName = warName;
		
		
	}

	public void run() {
		// throws event
		fireWarHasBeenStarted();

		// this thread will be alive until the war is over
		synchronized (this) {
			try {
				wait();

				stopAllMunitions();
				fireWarHasBeenFinished();
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}// synchronized


	}// run

	// this method stop all the munitions that are alive
	// in case the war is end
	// the method will wait until all munitions end there run
	private void stopAllMunitions() {
		for (EnemyLauncher el : enemyLauncherArr) {
			try {
				if (el.getCurrentMissile() != null) {
					el.getCurrentMissile().join();
				}

				el.stopRunning();
				el.interrupt();
				el.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (LauncherDestructor ld : launcherDestractorArr) {
			try {
				if (ld.getCurrentMissile() != null) {
					ld.getCurrentMissile().join();
				}

				ld.stopRunning();
				ld.interrupt();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (IronDome ironDome : ironDomeArr) {
			try {
				if (ironDome.getCurrentMissile() != null) {
					ironDome.getCurrentMissile().join();
				}

				ironDome.stopRunning();
				ironDome.interrupt();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}// endAllmunitions

	// returns all missiles in air in current time
	public Vector<String> getAllDuringFlyMissilesIds() {
		Vector<String> missileIds = new Vector<>();

		for (EnemyLauncher el : enemyLauncherArr) {
			if (el.getCurrentMissile() != null)
				missileIds.add(el.getCurrentMissile().getMId());
		}

		if (missileIds.size() == 0)
			return null;

		return missileIds;
	}

	// intercept given missile id
	public synchronized void interceptGivenMissile(String missileId) {
		IronDome ironDome = findFreeIronDome();

		if (ironDome == null) {
			fireNoSuchObject("Iron Dome");
		} else {
			interceptGivenMissile(missileId, ironDome);
		}
	}

	// intercept given missile id and iron dome id, Use for xml
	public synchronized void interceptGivenMissile(String ironDomeID,
			String missileId) {
		for (IronDome ironDome : ironDomeArr)
			if (ironDome.getIronDomeId().equals(ironDomeID)
					&& !ironDome.getIsBusy()) {
				interceptGivenMissile(missileId, ironDome);

				return;
			}

		fireNoSuchObject("Iron Dome");
	}

	// intercept given missile id and IronDome
	private void interceptGivenMissile(String missileId, IronDome ironDome) {
		EnemyMissile missileToDestroy;

		for (EnemyLauncher el : enemyLauncherArr) {
			missileToDestroy = el.getCurrentMissile();

			if (missileToDestroy != null
					&& missileToDestroy.getMId().equals(missileId)) {
				synchronized (ironDome) {
					ironDome.setMissileToDestroy(missileToDestroy);
					ironDome.notify();

					return;
				}// synchronized
			}// if
		}// for

		fireMissileNotExistEvent(ironDome.getIronDomeId(), missileId);
	}

	// finds free iron dome to use in war against enemy missiles
	public IronDome findFreeIronDome() {
		for (IronDome ironDome : ironDomeArr) {
			if (!ironDome.getIsBusy())
				return ironDome;
		}

		return null;
	}

	// intercept given missile id and launcher id, Use for xml
	public synchronized void interceptGivenLauncher(String destructorId,
			String launcherId) {
		for (LauncherDestructor ld : launcherDestractorArr)
			if (ld.getDestructorId().equals(destructorId) && !ld.getIsBusy()) {

				interceptGivenLauncher(launcherId, ld);

				// if found, not need to search more
				return;
			} else {
				switch (launcherId.charAt(0)) {
				case 'P':
					fireNoSuchObject("plane");
					break;

				case 'S':
					fireNoSuchObject("ship");
					break;
				}
			}

	}

	// intercept given launcher id
	public synchronized void interceptGivenLauncher(String launcherId) {
		LauncherDestructor ld = findFreeDestructor();

		if (ld == null) {
			switch (launcherId.charAt(0)) {
			case 'P':
				fireNoSuchObject("plane");
				break;

			case 'S':
				fireNoSuchObject("ship");
				break;
			}
		} else {
			interceptGivenLauncher(launcherId, ld);
		}
	}

	// intercept given missile id and launcher
	private void interceptGivenLauncher(String launcherId,
			LauncherDestructor destructor) {
		for (EnemyLauncher el : enemyLauncherArr) {

			if (el.getLauncherId().equals(launcherId) && el.isAlive()) {

				synchronized (destructor) {
					destructor.setEnemyLauncherToDestroy(el);
					destructor.notify();

					return;
				}
			}
		}

		fireMissileNotExistEvent(destructor.getDestructorId(), launcherId);
	}

	// finds free launcher destructor to use in war against enemy launchers
	public LauncherDestructor findFreeDestructor() {
		for (LauncherDestructor ld : launcherDestractorArr) {
			if (!ld.getIsBusy()) {
				return ld;
			}
		}

		return null;
	}

	// returns vector of all visible launchers id's
	public Vector<String> getAllVisibleLaunchersIds() {
		Vector<String> visibleIds = new Vector<>();

		for (EnemyLauncher el : enemyLauncherArr) {

			if (el.isAlive() && !el.getIsHidden()) {
				visibleIds.add(el.getLauncherId());
			}
		}

		if (visibleIds.size() == 0)
			return null;

		return visibleIds;
	}

	// returns vector of all launchers id's
	public Vector<String> getAllLaunchersIds() {
		Vector<String> visibleIds = new Vector<>();

		for (EnemyLauncher el : enemyLauncherArr) {
			if (el.isAlive()) {
				visibleIds.add(el.getLauncherId());
			}
		}

		if (visibleIds.size() == 0)
			return null;

		return visibleIds;
	}

	public void launchEnemyMissile(String launcherId, String destination,
			int damage, int flyTime) {

		for (EnemyLauncher el : enemyLauncherArr) {
			// Check if there is enemy launcher with given id
			if (el.getLauncherId().equals(launcherId) && el.isAlive()) {

				// Check if launcher is not in use
				if (el.getCurrentMissile() == null) {
					synchronized (el) {
						el.setMissileInfo(destination, damage, flyTime);
						el.notify();
					}// synchronized

				}// if

			}// if
		}// for
	}// method

	// add enemy launcher without given parameters
	public String addEnemyLauncher() {
		String id = IdGenerator.enemyLauncherIdGenerator();
		boolean isHidden = Math.random() < 0.5;

		addEnemyLauncher(id, isHidden);

		return id;
	}

	// add enemy launcher with parameters
	public String addEnemyLauncher(String launcherId, boolean isHidden) {
		return addLauncher(new EnemyLauncher(launcherId, isHidden, this,
				statistics));

	}

	public String addLauncher(EnemyLauncher launcher) {
		for (WarEventListener l : allListeners)
			launcher.registerListeners(l);
		try {
			sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		launcher.start();
		enemyLauncherArr.add(launcher);
		DBFactory.getInstance().addLauncher(launcher);
		return launcher.getLauncherId();
	}

	// public void addEnemyLauncher(EnemyLauncher launcher){
	// enemyLauncherArr.add(launcher);
	// }

	// add iron dome without given parameters
	public String addIronDome() {
		String id = IdGenerator.ironDomeIdGenerator();
		addIronDome(id);
		return id;
	}

	// add iron dome with given parameters
	public String addIronDome(String id) {
		IronDome ironDome = new IronDome(id, this, statistics);

		for (WarEventListener l : allListeners)
			ironDome.registerListeners(l);

		ironDome.start();

		addIronDone(ironDome);
		// add iron dome to db
		DBFactory.getInstance().addIronDome(ironDome);

		return id;
	}

	public void addIronDone(IronDome id) {
		ironDomeArr.add(id);
	}

	// add defense launcher destructor
	public String addDefenseLauncherDestructor(String type) {
		String id = IdGenerator.defenseLauncherDestractorIdGenerator(type
				.charAt(0));

		LauncherDestructor destructor = new LauncherDestructor(type, id, this,
				statistics);

		for (WarEventListener l : allListeners)
			destructor.registerListeners(l);

		destructor.start();

		launcherDestractorArr.add(destructor);
		DBFactory.getInstance().addLauncherDestructor(destructor);

		return id;
	}

	public void addLauncherDestructor(LauncherDestructor destructor) {
		launcherDestractorArr.add(destructor);
	}

	public void registerListeners(WarEventListener control) {
		for (IronDome iron : ironDomeArr)
			iron.registerListeners(control);

		for (LauncherDestructor launcherDestructor : launcherDestractorArr)
			launcherDestructor.registerListeners(control);

		for (EnemyLauncher EnemyLauncher : enemyLauncherArr)
			EnemyLauncher.registerListeners(control);

		allListeners.add(control);
	}

	// Event
	private void fireWarHasBeenFinished() {
		DBFactory.getInstance().endWar(this);
		if (warServer != null)
			warServer.stopServer();
		for (WarEventListener l : allListeners)
			l.warHasBeenFinished();
		DBFactory.getInstance().closeDB();
	}

	// Event
	private void fireWarHasBeenStarted() {
		for (WarEventListener l : allListeners)
			l.warHasBeenStarted();
	}

	// Event
	private void fireNoSuchObject(String type) {
		for (WarEventListener l : allListeners)
			l.noSuchObject(type);
	}

	// Event
	private void fireMissileNotExistEvent(String defenseLauncherId,
			String enemyId) {
		for (WarEventListener l : allListeners)
			l.missileNotExist(defenseLauncherId, enemyId);
	}

	public WarStatistics getStatistics() {
		return statistics;
	}

	public String[] getAllTargetCities() {
		return targetCities;
	}

	public Vector<String> getIronDomes() {
		Vector<String> v = new Vector<>();
		for (IronDome i : ironDomeArr) {
			v.add(i.getIronDomeId());
		}
		return v;
	}

	public Vector<String> getLauncherDestructors() {
		Vector<String> v = new Vector<>();
		for (LauncherDestructor ld : launcherDestractorArr) {
			v.add(ld.getDestructorId());
		}
		return v;
	}

	public String getEnemyInventory() {
		StringBuilder retval = new StringBuilder(1000);
		retval.append("Launchers:\r\n");
		for (EnemyLauncher launcher : enemyLauncherArr) {
			if (launcher.isAlive())
				retval.append('\t' + launcher.getLauncherId() + " "
						+ (launcher.getIsHidden() ? "(Hidden)" : "(Exposed)")
						+ "\r\n");
		}
		retval.append("\r\nMissiles in air:\r\n");

		if (getAllDuringFlyMissilesIds() != null)
			for (String missile : getAllDuringFlyMissilesIds()) {
				retval.append('\t' + missile + "\r\n");
			}
		return retval.toString();
	}

	public String getFriendlyInventory() {
		StringBuilder retval = new StringBuilder(1000);
		retval.append("Iron domes:\r\n");
		if (getIronDomes() != null)
			for (String id : getIronDomes()) {
				retval.append('\t' + id + "\r\n");
			}
		retval.append("\r\nLauncher Destructors:" + "\r\n");
		if (getLauncherDestructors() != null)
			for (String ld : getLauncherDestructors())
				retval.append('\t' + ld + "\r\n");
		return retval.toString();
	}

	public String getWarName() {
		return warName;
	}

	public void setServer(Server server) {
		this.warServer = server;
	}

	public List<WarEventListener> getAllListeners() {
		return allListeners;
	}

	public void setAllListeners(List<WarEventListener> allListeners) {
		this.allListeners = allListeners;
	}

	// JPA STUFF

	public void setWarName(String name) {
		this.warName = name;
	}

	public List<IronDome> getIronDomeArr() {
		return ironDomeArr;
	}

	public void setIronDomeArr(List<IronDome> ironDomeArr) {
		this.ironDomeArr = ironDomeArr;
	}

	public List<LauncherDestructor> getLauncherDestractorArr() {
		return launcherDestractorArr;
	}

	public void setLauncherDestractorArr(
			List<LauncherDestructor> launcherDestractorArr) {
		this.launcherDestractorArr = launcherDestractorArr;
	}

	public List<EnemyLauncher> getEnemyLauncherArr() {
		return enemyLauncherArr;
	}

	public void setEnemyLauncherArr(List<EnemyLauncher> enemyLauncherArr) {
		this.enemyLauncherArr = enemyLauncherArr;
	}

	public String[] getTargetCities() {
		return targetCities;
	}

	public void setTargetCities(String[] targetCities) {
		this.targetCities = targetCities;
	}

	public Server getWarServer() {
		return warServer;
	}

	public void setWarServer(Server warServer) {
		this.warServer = warServer;
	}

	public void setStatistics(WarStatistics statistics) {
		this.statistics = statistics;
	}

	

}
