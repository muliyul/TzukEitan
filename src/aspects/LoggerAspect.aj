package aspects;

import model.War;
import launchers.EnemyLauncher;
import launchers.IronDome;
import launchers.LauncherDestructor;
import missiles.DefenseMissile;
import missiles.DefenseDestructorMissile;
import missiles.EnemyMissile;

public aspect LoggerAspect {

	WarLogger warLogger = new WarLogger();

	pointcut addWarHandler(String warName) : execution (War.new(String)) && args(warName);

	after(String warName) : addWarHandler(warName){

		WarLogger.addWarLoggerHandler(warName);
	}

	pointcut addLancherHandler(String id) : execution (EnemyLauncher.new(*,*,*,*)) && args(id,*,*,*);

	after(String id) : addLancherHandler(id){

		WarLogger.addLoggerHandler("Launcher", id);
	}

	pointcut addIronDomeHandler(String id) : execution (IronDome.new(*,*,*)) && args(id,*,*);

	after(String id) : addIronDomeHandler(id){

		WarLogger.addLoggerHandler("IronDome", id);
	}

	pointcut addDestructorHandler(String type, String id) : execution (LauncherDestructor.new(*,*,*,*)) && args(type, id,*,*);

	after(String type, String id) : addDestructorHandler(type,id){

		WarLogger.addLoggerHandler(type, id);
		;
	}

	pointcut closeHandlers() : execution (private void fireWarHasBeenFinished()) && args();

	after() : closeHandlers(){

		warLogger.warHasBeenFinished();
		WarLogger.closeAllHandlers();
		WarLogger.closeWarHandler();
	}

	pointcut warHasStarted() : execution (private void fireWarHasBeenStarted()) && args();

	after() : warHasStarted(){
		warLogger.warHasBeenStarted();
	}

	pointcut defenseIronDomeLaunch(String missileId) : execution ( private void IronDome.fireLaunchMissileEvent(String)) && args(missileId);

	after(String missileId) : defenseIronDomeLaunch(missileId){
		IronDome id = (IronDome) thisJoinPoint.getTarget();
		warLogger.defenseLaunchMissile(id.getIdId(), missileId, id
				.getToDestroy().getMId());

	}

	pointcut defenseDestructorLaunch(String missileId) : execution ( private void LauncherDestructor.fireLaunchMissileEvent(String)) && args(missileId);

	after(String missileId) : defenseDestructorLaunch(missileId){
		LauncherDestructor ld = (LauncherDestructor) thisJoinPoint.getTarget();
		warLogger.defenseLaunchMissile(ld.getDestructorId(), ld.getType(),
				missileId, ld.getToDestroy().getLauncherId());

	}

	pointcut defenseHitMissile() : execution ( private void DefenseMissile.fireHitEvent()) && args();

	after() : defenseHitMissile(){
		DefenseMissile dm = (DefenseMissile) thisJoinPoint.getTarget();
		warLogger.defenseHitInterceptionMissile(dm.getWhoLaunchedMe()
				.getIronDomeId(), dm.getMissileId(), dm.getMissileToDestroy()
				.getMId());

	}

	pointcut defenseHitLauncher() : execution ( private void DefenseDestructorMissile.fireHitEvent()) && args();

	after() : defenseHitLauncher(){
		DefenseDestructorMissile ddm = (DefenseDestructorMissile) thisJoinPoint
				.getTarget();
		warLogger.defenseHitInterceptionLauncher(ddm.getWhoLaunchedMe()
				.getDestructorId(), ddm.getWhoLaunchedMe().getType(), ddm
				.getMissileId(), ddm.getLauncherToDestroy().getLauncherId());
	}

	pointcut defenseMissMissile() : execution ( private void DefenseMissile.fireMissEvent()) && args();

	after() : defenseMissMissile(){
		DefenseMissile dm = (DefenseMissile) thisJoinPoint.getTarget();
		warLogger.defenseMissInterceptionMissile(dm.getWhoLaunchedMe()
				.getIronDomeId(), dm.getMissileId(), dm.getMissileToDestroy()
				.getMId(), dm.getMissileToDestroy().getDamage());

	}

	pointcut defenseMissHiddenLauncher(String launcherId) : execution ( private void LauncherDestructor.fireLauncherIsHiddenEvent(String)) && args(launcherId);

	after(String launcherId) : defenseMissHiddenLauncher(launcherId){
		LauncherDestructor ld = (LauncherDestructor) thisJoinPoint.getTarget();
		warLogger.defenseMissInterceptionHiddenLauncher(ld.getDestructorId(),
				ld.getType(), launcherId);

	}

	pointcut defenseMissLauncher() : execution ( private void DefenseDestructorMissile.fireMissEvent()) && args();

	after() : defenseMissLauncher(){
		DefenseDestructorMissile ddm = (DefenseDestructorMissile) thisJoinPoint
				.getTarget();
		warLogger.defenseMissInterceptionLauncher(ddm.getWhoLaunchedMe()
				.getDestructorId(), ddm.getWhoLaunchedMe().getType(), ddm
				.getMissileId(), ddm.getLauncherToDestroy().getLauncherId());
	}

	pointcut enemyMissileLaunch(String missileId) : execution ( private void EnemyLauncher.fireLaunchMissileEvent(String)) && args(missileId);

	after(String missileId) : enemyMissileLaunch(missileId){
		EnemyLauncher em = (EnemyLauncher) thisJoinPoint.getTarget();
		warLogger.enemyLaunchMissile(em.getLauncherId(), missileId,
				em.getDestination(), em.getFlyTime(), em.getDamage());
	}

	pointcut launcherVisible(boolean visible) : execution ( private void EnemyLauncher.fireEnemyLauncherIsVisibleEvent(boolean)) && args(visible);

	after(boolean visible) : launcherVisible(visible){
		EnemyLauncher el = (EnemyLauncher) thisJoinPoint.getTarget();
		warLogger.enemyLauncherIsVisible(el.getLauncherId(), visible);
	}

	pointcut enemyMissileHit() : execution (private void EnemyMissile.fireHitEvent()) && args();

	after() : enemyMissileHit(){
		EnemyMissile em = (EnemyMissile) thisJoinPoint.getTarget();
		warLogger.enemyHitDestination(em.getWhoLaunchedMe().getLauncherId(),
				em.getMId(), em.getDestination(), em.getDamage(),
				em.getLaunchTime());
	}

	pointcut enemyMissileMiss() : execution (private void EnemyMissile.fireMissEvent()) && args();

	after() : enemyMissileMiss(){
		EnemyMissile em = (EnemyMissile) thisJoinPoint.getTarget();
		warLogger.enemyMissDestination(em.getWhoLaunchedMe().getLauncherId(),
				em.getMId(), em.getDestination(), em.getLaunchTime());
	}

	pointcut noMissile(String missileId) : execution ( private void IronDome.fireMissileNotExist(String)) && args(missileId);

	after(String missileId) : noMissile(missileId){
		IronDome id = (IronDome) thisJoinPoint.getTarget();
		warLogger.missileNotExist(id.getIronDomeId(), missileId);

	}

	pointcut noLauncher(String launcherId) : execution ( private void LauncherDestructor.fireLauncherNotExist(String)) && args(launcherId);

	after(String launcherId) : noLauncher(launcherId){
		LauncherDestructor ld = (LauncherDestructor) thisJoinPoint.getTarget();
		warLogger.enemyLauncherNotExist(ld.getDestructorId(), launcherId);

	}

}
