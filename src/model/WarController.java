package model;

import java.util.Vector;

import utils.WarXMLReader;
import view.AbstractWarView;
import view.ConsoleView;
import launchers.EnemyLauncher;
import listeners.WarEventListener;
import listeners.WarEventUIListener;
import missiles.EnemyMissile;


public class WarController implements WarEventListener, WarEventUIListener{
	private War warModel;
	private AbstractWarView view;
	
	public WarController(War warModel, AbstractWarView view){
		this.warModel = warModel;
		this.view = view;
		
		warModel.registerListeners(this);
		view.registerListener(this);
	}
	
	//Method that related to the view
	@Override
	public void defenseLaunchMissile(String myMunitionsId, String missileId, String enemyMissileId) {
		view.showDefenseLaunchMissile(myMunitionsId,missileId,enemyMissileId);
	}

	@Override
	public void defenseLaunchMissile(String myMunitionsId, String type,	String missileId, String enemyLauncherId) {
		view.showDefenseLaunchMissile(myMunitionsId, type, missileId, enemyLauncherId);
	}

	@Override
	public void enemyLaunchMissile(String myMunitionsId, String missileId, String destination, int damage) {
		view.showEnemyLaunchMissile(myMunitionsId, missileId, destination, damage);	
	}

	@Override
	public void enemyLauncherIsVisible(String id,boolean visible) {
		view.showLauncherIsVisible(id,visible);
	}

	@Override
	public void defenseMissInterceptionMissile(String whoLaunchedMeId, String missileId, String enemyMissileId, int damage) {
		view.showMissInterceptionMissile(whoLaunchedMeId, missileId, enemyMissileId);
	}

	@Override
	public void defenseHitInterceptionMissile(String whoLaunchedMeId, String missileId, String enemyMissileId) {
		view.showHitInterceptionMissile(whoLaunchedMeId, missileId, enemyMissileId);
	}

	@Override
	public void enemyHitDestination(String whoLaunchedMeId, String missileId, String destination, int damage, String launchTime) {
		view.showEnemyHitDestination(whoLaunchedMeId, missileId, destination, damage);
	}

	@Override
	public void defenseMissInterceptionLauncher(String whoLaunchedMeId,	String type, String missileId, String enemyLauncherId) {
		view.showMissInterceptionLauncher(whoLaunchedMeId,type, enemyLauncherId, missileId);
	}
	
	@Override
	public void defenseMissInterceptionHiddenLauncher(String whoLaunchedMeId, String type, String enemyLauncherId) {
		view.showMissInterceptionHiddenLauncher(whoLaunchedMeId,type, enemyLauncherId);
	}
	
	@Override
	public void defenseHitInterceptionLauncher(String whoLaunchedMeId, String type, String missileId, String enemyLauncherId) {
		view.showHitInterceptionLauncher(whoLaunchedMeId, type, enemyLauncherId, missileId);
	}
	
	//Methods related to the model
	@Override
	public void finishWar() {
		WarXMLReader.stopAllThreads();
		//warModel.finishWar();
		
		//notify the war
		synchronized (warModel) {
			warModel.notify();
		}
	}

	@Override
	public void showStatistics() {
		WarStatistics statistics = warModel.getStatistics();
		view.showStatistics(statistics.statisticsToArray());	
	}

	@Override
	public Vector<String> chooseMissileToIntercept() {
		Vector<String> ids = warModel.getAllDuringFlyMissilesIds();
		return ids;
	}

	@Override
	public void interceptGivenMissile(String ironDomeId, String missileId) {
		warModel.interceptGivenMissile(ironDomeId, missileId);
	}

	@Override
	public void interceptGivenMissile(String missileId) {
		warModel.interceptGivenMissile(missileId);
	}
	
	@Override
	public void interceptGivenLauncher(String launcherId) {
		warModel.interceptGivenLauncher(launcherId);
	}

	@Override
	public void interceptGivenLauncher(String destructorId, String launcherId) {
		warModel.interceptGivenLauncher(destructorId,launcherId);
	}
	
	@Override
	public Vector<String> chooseLauncherToIntercept() {
		Vector<String> ids = warModel.getAllVisibleLaunchersIds();
		return ids;
	}

	@Override
	public Vector<String> showAllLaunchers() {
		return warModel.getAllLaunchersIds();
	}

	@Override
	public void addEnemyMissile(String launcherId, String destination, int damage, int flyTime) {
		warModel.launchEnemyMissile(launcherId, destination, damage, flyTime);
	}

	@Override
	public String addEnemyLauncher(String launcherId, boolean isHidden) {
		return warModel.addEnemyLauncher(launcherId, isHidden);
	}
	
	@Override
	public String addEnemyLauncher() {
		return warModel.addEnemyLauncher();
	}

	@Override
	public String addIronDome() {
		return warModel.addIronDome();
	}
	
	@Override
	public String addIronDome(String id) {
		return warModel.addIronDome(id);
	}

	@Override
	public String addDefenseLauncherDestructor(String type) {
		String id = warModel.addDefenseLauncherDestructor(type);
		return id;
	}

	@Override
	public String[] getAllWarDestinations() {
		String[] warTargets = warModel.getAllTargetCities();
		return warTargets;
	}

	@Override
	public void warHasBeenFinished() {
		view.showWarHasBeenFinished();
		
		try {
			view.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void warHasBeenStarted() {
		view.showWarHasBeenStarted();
	}

	@Override
	public void noSuchObject(String type) {
		view.showNoSuchObject(type);
	}

	@Override
	public void missileNotExist(String defenseLauncherId, String enemyId) {
		view.showMissileNotExist(defenseLauncherId, enemyId);
	}
	
	@Override
	public void enemyLauncherNotExist(String defenseLauncherId,
			String launcherId) {
		view.showLauncherNotExist(defenseLauncherId, launcherId);
	}

	@Override
	public void enemyMissDestination(String whoLaunchedMeId, String id,
			String destination, String launchTime) {
		view.showEnemyMissDestination(whoLaunchedMeId, id, destination, launchTime);
	}

	@Override
	public String requestEnemyInventory() {
	    return warModel.getEnemyInventory();
	}

	@Override
	public String requestFriendlyInventory() {
	    return warModel.getFriendlyInventory();
	}

	public void addEnemyLauncher(EnemyLauncher l) {
	    warModel.addEnemyLauncher(l);
	}
	
	public WarStatistics getStatistics(){
	    return warModel.getStatistics();
	}

}
