package view;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import utils.Utils;
import listeners.WarEventUIListener;
import model.WarController;

public class ConsoleView extends Thread implements AbstractWarView{
	private List<WarEventUIListener> allListeners;
	private Scanner input = new Scanner(System.in);
	private StringBuilder menu = new StringBuilder(1000);
	private boolean isRunning = true;

	public ConsoleView() {
		allListeners = new LinkedList<WarEventUIListener>();
		createMenu();
	}// cons't

	public void run() {
		try {
			sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (isRunning) {
			selectUserChoiceMethod();
		}
	}// run

	public void registerListeners(WarEventUIListener listener) {
		allListeners.add(listener);
	}

	private void createMenu() {
		menu.append("choose one option: \n\n");
		menu.append("1. Add Munition to Intercept launchers.\n");
		menu.append("2. Add Munition to Intercept missile.\n");
		menu.append("3. Add launcher.\n");
		menu.append("4. Launch a missile.\n");
		menu.append("5. Intercept a launcher.\n");
		menu.append("6. Intercept a missile.\n");
		menu.append("7. Show statistics.\n");
		menu.append("8. End the war and show statistics.\n");
	}

	private int readUserChoise() {
		boolean flag = false;
		int choise = -1;
		System.out.println(menu);

		while (!flag) {

			try {
				choise = input.nextInt();
				flag = true;

			} catch (NumberFormatException e) {
				System.out.println("Wrong input, please try again:");
				choise = input.nextInt();
			}

		}

		return choise;
	}

	public void selectUserChoiceMethod() {
		int choice = readUserChoise();

		switch (choice) {
		case 1:
			fireAddDefenseLauncherDestructor();
			break;

		case 2:
			fireAddDefenseIronDome();
			break;

		case 3:
			fireAddEnemyLauncher();
			break;

		case 4:
			fireAddEnemyMissile();
			break;

		case 5:
			fireInterceptEnemyLauncher();
			break;

		case 6:
			fireInterceptMissile();
			break;

		case 7:
			fireShowStatistics();
			break;

		case 8:
			fireFinishWar();
			break;
		}
	}

	/* When user is select, an event is throw to the control */
	public void fireAddDefenseLauncherDestructor() {
		System.out
				.println("Please choose between Plane or Ship, for exit press enter");
		input.nextLine();

		String type = input.nextLine();

		type = type.toLowerCase();

		if (type.equals("plane") || type.equals("ship"))
			for (WarEventUIListener l : allListeners)
				l.addDefenseLauncherDestructor(type);
	}

	public void fireAddDefenseIronDome() {
		for (WarEventUIListener l : allListeners)
			l.addIronDome();
	}

	public void fireAddEnemyLauncher() {
		for (WarEventUIListener l : allListeners)
			l.addEnemyLauncher();
	}

	public void fireAddEnemyMissile() {
		for (WarEventUIListener l : allListeners) {
			Vector<String> launchersIds = l.showAllLaunchers();

			if (launchersIds != null) {
				System.out.println("Launchers to launch with:");

				int size = launchersIds.size();
				for (int i = 0; i < size; i++)
					System.out.println("\t" + (i + 1) + ")"
							+ launchersIds.get(i));

				System.out
						.println("Choose launcher id to equip, else press enter to continue");

				input.nextLine();
				String launcher = input.nextLine();

				if (launchersIds.contains(launcher)) {
					System.out.println("Destination cities to destory:");
					String[] destinations = l.getAllWarDestinations();

					for (int j = 0; j < destinations.length; j++)
						System.out.println((j + 1) + ") " + destinations[j]);

					System.out.println("Enter your choise:");
					// input.nextLine();
					String destination = input.nextLine();

					int damage = (int) ((Math.random() * Utils.SECOND) + Utils.SECOND * 2);
					int flyTime = (int) ((Math.random() * Utils.FLY_TIME) + Utils.FLY_TIME);

					l.addEnemyMissile(launcher, destination, damage, flyTime);
				}// if
				else
					System.out
							.println("The launcher you have selected doesn't exist!");
			}// if
			else
				System.out
						.println("There is no launcher yet, please add launcher first");
		}// for
	}// method

	public void fireInterceptEnemyLauncher() {
		for (WarEventUIListener l : allListeners) {
			Vector<String> launcersId = l.chooseLauncherToIntercept();

			if (launcersId != null) {
				System.out.println("Launcher to intercept:");

				int size = launcersId.size();
				for (int i = 0; i < size; i++)
					System.out
							.println("\t" + (i + 1) + ")" + launcersId.get(i));

				System.out
						.println("Choose launcher id to intercept, else press enter to continue");
				input.nextLine();
				String launcher = input.nextLine();

				if (launcersId.contains(launcher))
					l.interceptGivenLauncher(launcher);
				else
					System.out
							.println("The launcher you have selected doesn't exist!");
			}// if
			else
				System.out.println("There is no launcher to intercept!");
		}// for
	}// method

	public void fireInterceptMissile() {
		for (WarEventUIListener l : allListeners) {
			Vector<String> missilesId = l.chooseMissileToIntercept();

			if (missilesId != null) {
				System.out.println("Missiles to intercept:");

				int size = missilesId.size();
				for (int i = 0; i < size; i++)
					System.out
							.println("\t" + (i + 1) + ")" + missilesId.get(i));

				System.out
						.println("Choose missile id to intercept, else press enter to continue");
				input.nextLine();
				String missile = input.nextLine();

				if (missilesId.contains(missile))
					l.interceptGivenMissile(missile);
				else
					System.out
							.println("The missile you selected doesn't exist!");
			}// if
			else
				System.out.println("There is no missiles to intercept!");
		}// for
	}// method

	public void fireShowStatistics() {
		for (WarEventUIListener l : allListeners)
			l.showStatistics();
	}

	public void fireFinishWar() {
		isRunning = false;
			
		for (WarEventUIListener l : allListeners) {
			l.finishWar();
		}
	}

	/* Prints to screen event from controller */
	public void showDefenseLaunchMissile(String MunitionsId, String missileId,
			String enemyMissileId) {
		System.out.println("[" + Utils.getCurrentTime() + "] Iron dome: "
				+ MunitionsId + " just launched missile: " + missileId
				+ " towards missile: " + enemyMissileId);
	}

	public void showDefenseLaunchMissile(String MunitionsId, String type,
			String missileId, String enemyLauncherId) {
		System.out.println("[" + Utils.getCurrentTime() + "] " + type + ": "
				+ MunitionsId + " just launched missile: " + missileId
				+ " towards launcher: " + enemyLauncherId);
	}

	public void showEnemyLaunchMissile(String MunitionsId, String missileId,
			String destination, int damage) {
		System.out.println("[" + Utils.getCurrentTime() + "] Launcher: "
				+ MunitionsId + " just launched missile: " + missileId
				+ " towards: " + destination
				+ " is about to cause damage of: " + damage);
	}

	public void showLauncherIsVisible(String id, boolean visible) {
		String str = visible ? "visible" : "hidden";
		System.out.println("[" + Utils.getCurrentTime() + "] Launcher: " + id
				+ " just turned " + str);
	}

	public void showMissInterceptionMissile(String whoLaunchedMeId, String id,
			String enemyMissileId) {
		System.out.println("[" + Utils.getCurrentTime() + "] Iron Dome: "
				+ whoLaunchedMeId + " fired missile: " + id
				+ " but missed the missile: " + enemyMissileId);
	}

	public void showHitInterceptionMissile(String whoLaunchedMeId, String id,
			String enemyMissileId) {
		System.out.println("[" + Utils.getCurrentTime() + "] Iron Dome: "
				+ whoLaunchedMeId + " fired missile: " + id
				+ " and intercept succesfully the missile: " + enemyMissileId);
	}

	public void showEnemyHitDestination(String whoLaunchedMeId, String id,
			String destination, int damage) {
		System.out.println("[" + Utils.getCurrentTime() + "] Enemy Missile: "
				+ id + " HIT " + destination + ". the damage is: " + damage
				+ ". Launch by: " + whoLaunchedMeId);
	}

	public void showEnemyMissDestination(String whoLaunchedMeId, String id,
			String destination, String launchTime) {
		System.out.println("[" + Utils.getCurrentTime() + "] Enemy Missile: "
				+ id + " MISSED " + destination + " launch at: " + launchTime
				+ ". Launch by: " + whoLaunchedMeId);
	}

	public void showMissInterceptionLauncher(String whoLaunchedMeId,
			String type, String enemyLauncherId, String missileId) {
		System.out.println("[" + Utils.getCurrentTime() + "] " + type + ": "
				+ whoLaunchedMeId + " fired missile: " + missileId
				+ " but missed the Launcher: " + enemyLauncherId);
	}

	public void showMissInterceptionHiddenLauncher(String whoLaunchedMeId,
			String type, String enemyLauncherId) {
		System.out.println("[" + Utils.getCurrentTime() + "] " + type + ": "
				+ whoLaunchedMeId + " missed the Launcher: " + enemyLauncherId
				+ " because he is hidden");
	}

	public void showHitInterceptionLauncher(String whoLaunchedMeId,
			String type, String enemyLauncherId, String missileId) {
		System.out
				.println("[" + Utils.getCurrentTime() + "] " + type + ": "
						+ whoLaunchedMeId + " fired missile: " + missileId
						+ " and intercept succesfully the Launcher: "
						+ enemyLauncherId);
	}

	// prints all war statistics
	public void showStatistics(long[] array) {
		StringBuilder msg = new StringBuilder();
		msg.append("\n[" + Utils.getCurrentTime() + "]"
				+ "\t\t   War Statistics\n");
		msg.append("\t\t\t=========================================\n");
		msg.append("\t\t\t||\tNum of launch missiles: " + array[0] + "\t||\n");
		msg.append("\t\t\t||\tNum of intercept missiles: " + array[1]
				+ "\t||\n");
		msg.append("\t\t\t||\tNum of hit target missiles: " + array[2]
				+ "\t||\n");
		msg.append("\t\t\t||\tNum of launchers destroyed: " + array[3]
				+ "\t||\n");
		msg.append("\t\t\t||\ttotal damage: " + array[4] + "\t\t||\n");
		msg.append("\t\t\t==========================================\n");
		System.out.println(msg.toString());
	}

	public void showWarHasBeenFinished() {
	    	isRunning = false;
		for (WarEventUIListener l : allListeners) {
			l.showStatistics();
		}

		System.out.println("[" + Utils.getCurrentTime()
				+ "] =========>> Finally THIS WAR IS OVER!!! <<=========");
		// System.out.println("[" + Utils.getCurrentTime() + "]");
	}

	public void showWarHasBeenStarted() {
		System.out.println("[" + Utils.getCurrentTime()
				+ "] =========>> War has been started!!! <<=========");
		// System.out.println("[" + Utils.getCurrentTime() + "]");
	}

	public void showNoSuchObject(String type) {
		System.out.println("[" + Utils.getCurrentTime()
				+ "] ERROR: Cannot find " + type + " you selected in war");
	}

	public void showMissileNotExist(String defenseLauncherId, String enemyId) {
		System.out.println("[" + Utils.getCurrentTime() + "] ERROR: "
				+ defenseLauncherId + " tried to intercept, " + "but missed: "
				+ enemyId + " doesn't exist!");
	}

	public void showLauncherNotExist(String defenseLauncherId, String launcherId) {
		System.out.println("[" + Utils.getCurrentTime() + "] ERROR: "
				+ defenseLauncherId + " tried to intercept, " + "but missed: "
				+ launcherId + " doesn't exist!");
	}

	@Override
	public void registerListener(WarController controller) {
		allListeners.add(controller);
	}

	@Override
	public String getWarNameFromUser() {
	    System.out.print("Enter war name: ");
	    return input.nextLine();
	}

}
