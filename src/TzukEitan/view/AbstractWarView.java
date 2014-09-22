package TzukEitan.view;

import TzukEitan.war.WarController;

public interface AbstractWarView {
	void registerListener(WarController controller);
	void showDefenseLaunchMissile(String myMunitionsId, String missileId,
			String enemyMissileId);
	void showDefenseLaunchMissile(String myMunitionsId, String type,
			String missileId, String enemyLauncherId);
	void showEnemyLaunchMissile(String myMunitionsId, String missileId,
			String destination, int damage);
	void showLauncherIsVisible(String id, boolean visible);
	void showMissInterceptionMissile(String whoLaunchedMeId, String missileId,
			String enemyMissileId);
	void showHitInterceptionMissile(String whoLaunchedMeId, String missileId,
			String enemyMissileId);
	void showEnemyHitDestination(String whoLaunchedMeId, String missileId,
			String destination, int damage);
	void showMissInterceptionLauncher(String whoLaunchedMeId, String type,
			String enemyLauncherId, String missileId);
	void showMissInterceptionHiddenLauncher(String whoLaunchedMeId,
			String type, String enemyLauncherId);
	void showHitInterceptionLauncher(String whoLaunchedMeId, String type,
			String enemyLauncherId, String missileId);
	void showStatistics(long[] statisticsToArray);
	void join() throws InterruptedException;
	void showWarHasBeenFinished();
	void showWarHasBeenStarted();
	void showNoSuchObject(String type);
	void showMissileNotExist(String defenseLauncherId, String enemyId);
	void showLauncherNotExist(String defenseLauncherId, String launcherId);
	void showEnemyMissDestination(String whoLaunchedMeId, String id,
			String destination, String launchTime);
	String getWarName();
	
}
