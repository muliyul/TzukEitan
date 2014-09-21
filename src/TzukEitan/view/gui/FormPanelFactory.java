package TzukEitan.view.gui;

import TzukEitan.listeners.WarEventUIListener;

public class FormPanelFactory {
	public enum FormType{
		ADD_DEFENSE_LAUNCHER_DESTRUCTOR,
		ADD_ENEMY_MISSILE,
		ADD_ENEMY_LAUNCHER,
		ADD_DEFENSE_IRON_DOME,
		INTERCEPT_ENEMY_MISSILE,
		INTERCEPT_ENEMY_LAUNCHER,
	}
	
	public static FormPanel get(FormType type,WarEventUIListener l){
		switch(type){
		case ADD_DEFENSE_LAUNCHER_DESTRUCTOR: return new AddDefenseLauncherDestructorForm(l);
		case ADD_ENEMY_LAUNCHER: return new AddEnemyLauncherForm(l);
		case ADD_ENEMY_MISSILE: return new AddEnemyMissilesForm(l);
		case ADD_DEFENSE_IRON_DOME: return new AddDefenseIronDomeForm(l);
		case INTERCEPT_ENEMY_MISSILE: return new InterceptEnemyMissileForm(l);
		case INTERCEPT_ENEMY_LAUNCHER: return new InterceptEnemyLauncherForm(l);
		default: return null;
		}
	}
}
