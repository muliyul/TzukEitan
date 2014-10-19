package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import controller.WarController;
import utils.ImageUtils;
import utils.Utils;
import view.gui.AnimationPanel;
import view.gui.DBDialog;
import view.gui.forms.FormPanel;
import view.gui.forms.FormPanelFactory;
import listeners.WarEventUIListener;

public class GUIView extends JFrame implements AbstractWarView {
    private static final long serialVersionUID = -5779778943760585343L;

    private List<WarEventUIListener> listeners;

    private String[] enemyImgsString = { "launcher.png", "missile.png" };
    private String[] enemyActionString = { "Add Launcher", "Launch Missile" };

    private String[] friendlyImgsString = { "irondome.png", "", "", "" };
    private String[] friendlyActionString = { "Add Iron Dome",
	    "Add Launcher Destructor", "Intercept Missile",
	    "Intercept Launcher" };

    private JPanel mainPanel;
    private JPanel enemyPanel;
    private JPanel friendlyPanel;
    private AnimationPanel animationPanel;
    private JPanel centerPanel;

    private JTextArea enemyInventory;
    private JButton[] enemyActionBtnArray;

    private JTextArea friendlyInventory;
    private JButton[] friendlyActionBtnArray;

    private JPanel enemyFormPanelContainer;
    private JPanel friendlyFormPanelContainer;

    private FormPanel enemyFormPanel;
    private FormPanel friendlyFormPanel;

    private JButton enemyApplyBtn;
    private JButton friendlyApplyBtn;

    private JButton enemyCancelBtn;
    private JButton friendlyCancelBtn;

    private Timer refreshInv;

    private JTextArea logsTxtArea;
    
    public GUIView() {
	listeners = new LinkedList<WarEventUIListener>();
	SwingUtilities.invokeLater(new Runnable() {
	    @SuppressWarnings("unused")
	    public void run() {
		GUIView.this.setTitle("WarSim");
		GUIView.this.addWindowListener(new WindowListener() {
		    public void windowOpened(WindowEvent e) {
		    }

		    @Override
		    public void windowIconified(WindowEvent e) {
		    }

		    @Override
		    public void windowDeiconified(WindowEvent e) {
		    }

		    @Override
		    public void windowDeactivated(WindowEvent e) {
		    }

		    @Override
		    public void windowClosing(WindowEvent e) {
			for (WarEventUIListener l : listeners)
			    l.finishWar();
			refreshInv.stop();
		    }

		    @Override
		    public void windowClosed(WindowEvent e) {
			
		    }

		    @Override
		    public void windowActivated(WindowEvent e) {
		    }
		});
		setSize(1280, 900);
		mainPanel = new JPanel(new BorderLayout());
		JPanel logsPanel = new JPanel();
		logsPanel.add(new JScrollPane(logsTxtArea =
			new JTextArea(10, 61)));
		logsTxtArea.setFont(new Font("Arial", Font.BOLD, 12));
		logsTxtArea.setForeground(Color.RED);
		logsTxtArea.setEditable(false);
		animationPanel = new AnimationPanel();

		centerPanel = new JPanel(new BorderLayout());
		JPanel enemyActionsPanel, friendlyActionPanel;
		enemyActionBtnArray =
			createButtonsArray(enemyImgsString, enemyActionString);

		friendlyActionBtnArray =
			createButtonsArray(friendlyImgsString,
				friendlyActionString);
		setLocationRelativeTo(null);

		enemyPanel =
			createSidePanels(enemyInventory = new JTextArea(),
				enemyActionsPanel = new JPanel(),
				enemyActionBtnArray, enemyFormPanelContainer =
					new FormPanel());

		friendlyPanel =
			createSidePanels(friendlyInventory = new JTextArea(),
				friendlyActionPanel = new JPanel(),
				friendlyActionBtnArray,
				friendlyFormPanelContainer = new FormPanel());

		addBottomBtn(enemyPanel, enemyApplyBtn = new JButton("Apply"),
			enemyCancelBtn = new JButton("Cancel"));
		addBottomBtn(friendlyPanel, friendlyApplyBtn =
			new JButton("Apply"), friendlyCancelBtn =
			new JButton("Cancel"));

		enemyPanel.setBackground(Color.RED);
		friendlyPanel.setBackground(Color.GREEN);
		enemyPanel.setBorder(BorderFactory
			.createTitledBorder("Enemy Panel"));
		friendlyPanel.setBorder(BorderFactory
			.createTitledBorder("Friendly Panel"));
		logsPanel.setBorder(BorderFactory
			.createTitledBorder("Logs Panel"));
		animationPanel.setBorder(BorderFactory
			.createTitledBorder("Animation Panel"));
		animationPanel.setPreferredSize(new Dimension(700, 600));
		logsPanel.setPreferredSize(new Dimension(700, 200));
		centerPanel.add(animationPanel, BorderLayout.CENTER);
		centerPanel.add(logsPanel, BorderLayout.SOUTH);
		mainPanel.add(enemyPanel, BorderLayout.WEST);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(friendlyPanel, BorderLayout.EAST);
		setContentPane(mainPanel);

		friendlyApplyBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			for (WarEventUIListener l : listeners) {
			    switch (e.getActionCommand()) {
			    case "adddefenselauncherdestructor": {
				l.addDefenseLauncherDestructor((String) friendlyFormPanel
					.getParams()[0]);
				break;
			    }
			    case "adddefenseirondome": {
				l.addIronDome();
				break;
			    }
			    case "interceptlauncher": {
				if (friendlyFormPanel.getParams() != null)
				    l.interceptGivenLauncher((String) friendlyFormPanel
					    .getParams()[0]);
				break;
			    }
			    case "interceptmissile": {
				if (friendlyFormPanel.getParams() != null)
				    l.interceptGivenMissile((String) friendlyFormPanel
					    .getParams()[0]);
				break;
			    }
			    }
			}
			friendlyCancelBtn.doClick();
		    }
		});

		enemyApplyBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			for (WarEventUIListener l : listeners) {
			    switch (e.getActionCommand()) {
			    case "addenemylauncher": {
				l.addEnemyLauncher();
				break;
			    }
			    case "addenemymissile": {
				Object[] params = enemyFormPanel.getParams();
				String id = (String) params[0];
				String destination = (String) params[1];
				int damage = (int) params[2];
				int flyTime = (int) params[3];
				l.addEnemyMissile(id, destination, damage,
					flyTime);
				break;
			    }
			    }
			}
			enemyCancelBtn.doClick();
		    }
		});

		enemyActionBtnArray[0].addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			for (JButton jbt : enemyActionBtnArray)
			    jbt.setEnabled(true);
			fireAddEnemyLauncher();
			enemyFormPanelContainer.repaint();
			enemyActionBtnArray[0].setEnabled(false);
		    }
		});
		enemyActionBtnArray[1].addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			for (JButton jbt : enemyActionBtnArray)
			    jbt.setEnabled(true);
			fireAddEnemyMissile();
			enemyFormPanelContainer.repaint();
			enemyActionBtnArray[1].setEnabled(false);
		    }
		});

		friendlyActionBtnArray[0]
			.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
				    jbt.setEnabled(true);
				fireAddDefenseIronDome();
				friendlyFormPanelContainer.repaint();
				friendlyActionBtnArray[0].setEnabled(false);
			    }
			});
		friendlyActionBtnArray[1]
			.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
				    jbt.setEnabled(true);
				fireAddDefenseLauncherDestructor();
				friendlyFormPanelContainer.repaint();
				friendlyActionBtnArray[1].setEnabled(false);
			    }
			});
		friendlyActionBtnArray[2]
			.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
				    jbt.setEnabled(true);
				fireInterceptMissile();
				friendlyFormPanelContainer.repaint();
				friendlyActionBtnArray[2].setEnabled(false);
			    }
			});
		friendlyActionBtnArray[3]
			.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
				    jbt.setEnabled(true);
				fireInterceptEnemyLauncher();
				friendlyFormPanelContainer.repaint();
				friendlyActionBtnArray[3].setEnabled(false);
			    }
			});

		enemyCancelBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			enemyApplyBtn.setActionCommand(null);
			for (JButton jbt : enemyActionBtnArray)
			    jbt.setEnabled(true);
			enemyFormPanelContainer.removeAll();
			enemyFormPanelContainer.repaint();
			enemyPanel.validate();
		    }
		});

		friendlyCancelBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			friendlyApplyBtn.setActionCommand(null);
			for (JButton jbt : friendlyActionBtnArray)
			    jbt.setEnabled(true);
			friendlyFormPanelContainer.removeAll();
			friendlyFormPanelContainer.repaint();
			friendlyPanel.validate();
		    }
		});

		enemyInventory.setEditable(false);
		friendlyInventory.setEditable(false);

		refreshInv = new Timer(Utils.SECOND, new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			for (WarEventUIListener l : listeners) {
			    String inv;
			    if (!(inv = l.requestEnemyInventory())
				    .equals(enemyInventory.getText()))
				enemyInventory.setText(inv);
			    if (!(inv = l.requestFriendlyInventory())
				    .equals(friendlyInventory.getText()))
				friendlyInventory.setText(inv);
			}
		    }
		});
		refreshInv.start();
		toFront();
	    }
	});
    }

    public JButton[] createButtonsArray(String[] imgNames, String[] actionName) {
	JButton[] btnArray = new JButton[imgNames.length];
	ImageIcon temp;

	for (int i = 0; i < btnArray.length; i++) {
	    temp = ImageUtils.getImageIcon(imgNames[i]);
	    btnArray[i] = new JButton(actionName[i], temp);
	    btnArray[i].setVerticalTextPosition(AbstractButton.TOP);
	    btnArray[i].setHorizontalTextPosition(AbstractButton.CENTER);
	}
	return btnArray;
    }

    public void addBottomBtn(JPanel sidePanel, JButton applyBtn,
	    JButton cancelBtn) {
	JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
	bottomPanel.add(applyBtn);
	bottomPanel.add(cancelBtn);
	sidePanel.add(bottomPanel);
    }

    public JPanel createSidePanels(JTextArea txtArea, JPanel actionPanel,
	    JButton[] actionBtn, JPanel formPanel) {
	JPanel sidePanel = new JPanel();
	sidePanel.setLayout(new GridLayout(4, 1, 2, 2));
	sidePanel.setPreferredSize(new Dimension(300, 800));

	JPanel invenPanel = new JPanel();
	invenPanel.setLayout(new BoxLayout(invenPanel, BoxLayout.Y_AXIS));
	invenPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
	invenPanel.add(new JScrollPane(txtArea));

	actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

	for (int i = 0; i < actionBtn.length; i++)
	    actionPanel.add(actionBtn[i]);

	formPanel.setBorder(BorderFactory.createTitledBorder("Form"));

	invenPanel.setPreferredSize(new Dimension(280, 300));
	sidePanel.add(invenPanel);
	sidePanel.add(actionPanel);
	sidePanel.add(formPanel);

	return sidePanel;
    }

    public void fireAddDefenseLauncherDestructor() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer
		    .add(friendlyFormPanel =
			    FormPanelFactory
				    .get(FormPanelFactory.FormType.ADD_DEFENSE_LAUNCHER_DESTRUCTOR,
					    l));
	}
	friendlyApplyBtn.setActionCommand("adddefenselauncherdestructor");
	friendlyPanel.validate();
    }

    public void fireAddDefenseIronDome() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer
		    .add(friendlyFormPanel =
			    FormPanelFactory
				    .get(FormPanelFactory.FormType.ADD_DEFENSE_IRON_DOME,
					    l));
	}
	friendlyApplyBtn.setActionCommand("adddefenseirondome");
	friendlyPanel.validate();
    }

    public void fireAddEnemyLauncher() {
	enemyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    enemyFormPanelContainer.add(enemyFormPanel =
		    FormPanelFactory.get(
			    FormPanelFactory.FormType.ADD_ENEMY_LAUNCHER, l));
	}
	enemyApplyBtn.setActionCommand("addenemylauncher");
	enemyPanel.validate();
    }

    public void fireAddEnemyMissile() {
	enemyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    enemyFormPanelContainer.add(enemyFormPanel =
		    FormPanelFactory.get(
			    FormPanelFactory.FormType.ADD_ENEMY_MISSILE, l));
	}
	enemyApplyBtn.setActionCommand("addenemymissile");
	enemyPanel.validate();
    }

    public void fireInterceptEnemyLauncher() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer.add(friendlyFormPanel =
		    FormPanelFactory.get(
			    FormPanelFactory.FormType.INTERCEPT_ENEMY_LAUNCHER,
			    l));
	}
	friendlyApplyBtn.setActionCommand("interceptlauncher");
	friendlyPanel.validate();
    }

    public void fireInterceptMissile() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer.add(friendlyFormPanel =
		    FormPanelFactory.get(
			    FormPanelFactory.FormType.INTERCEPT_ENEMY_MISSILE,
			    l));
	}
	friendlyApplyBtn.setActionCommand("interceptmissile");
	friendlyPanel.validate();
    }

    /* Prints to screen event from controller */
    public void showDefenseLaunchMissile(String MunitionsId, String missileId,
	    String enemyMissileId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Iron dome: "
		+ MunitionsId + " just launched missile: " + missileId
		+ " towards missile: " + enemyMissileId + "\r\n");
    }

    public void showDefenseLaunchMissile(String MunitionsId, String type,
	    String missileId, String enemyLauncherId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] " + type + ": "
		+ MunitionsId + " just launched missile: " + missileId
		+ " towards launcher: " + enemyLauncherId + "\r\n");
    }

    public void showEnemyLaunchMissile(String MunitionsId, String missileId,
	    String destination, int flyTime, int damage) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Launcher: "
		+ MunitionsId + " just launched missile: " + missileId
		+ " towards: " + destination + " is about to cause damage of: "
		+ damage + "\r\n");
	animationPanel.addMissile(missileId,flyTime);
    }

    public void showLauncherIsVisible(String id, boolean visible) {
	String str = visible ? "visible" : "hidden";
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Launcher: " + id
		+ " just turned " + str + "\r\n");
    }

    public void showMissInterceptionMissile(String whoLaunchedMeId, String id,
	    String enemyMissileId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Iron Dome: "
		+ whoLaunchedMeId + " fired missile: " + id
		+ " but missed the missile: " + enemyMissileId + "\r\n");
    }

    public void showHitInterceptionMissile(String whoLaunchedMeId, String id,
	    String enemyMissileId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Iron Dome: "
		+ whoLaunchedMeId + " fired missile: " + id
		+ " and intercept succesfully the missile: " + enemyMissileId
		+ "\r\n");
	animationPanel.intercept(enemyMissileId);
    }

    public void showEnemyHitDestination(String whoLaunchedMeId, String id,
	    String destination, int damage) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Enemy Missile: "
		+ id + " HIT " + destination + ". the damage is: " + damage
		+ ". Launch by: " + whoLaunchedMeId + "\r\n");
    }

    public void showEnemyMissDestination(String whoLaunchedMeId, String id,
	    String destination, String launchTime) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] Enemy Missile: "
		+ id + " MISSED " + destination + " launch at: " + launchTime
		+ ". Launch by: " + whoLaunchedMeId + "\r\n");
    }

    public void showMissInterceptionLauncher(String whoLaunchedMeId,
	    String type, String enemyLauncherId, String missileId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] " + type + ": "
		+ whoLaunchedMeId + " fired missile: " + missileId
		+ " but missed the Launcher: " + enemyLauncherId + "\r\n");
    }

    public void showMissInterceptionHiddenLauncher(String whoLaunchedMeId,
	    String type, String enemyLauncherId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] " + type + ": "
		+ whoLaunchedMeId + " missed the Launcher: " + enemyLauncherId
		+ " because he is hidden" + "\r\n");
    }

    public void showHitInterceptionLauncher(String whoLaunchedMeId,
	    String type, String enemyLauncherId, String missileId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] " + type + ": "
		+ whoLaunchedMeId + " fired missile: " + missileId
		+ " and intercept succesfully the Launcher: " + enemyLauncherId
		+ "\r\n");
    }

    // prints all war statistics
    public void showStatistics(long[] array) {
	StringBuilder msg = new StringBuilder();
	JFrame statswindow = new JFrame("Statistics");
	msg.append("\tWar Statistics\n");
	msg.append("=========================================\n");
	msg.append("\tNum of launch missiles: " + array[0] + "\n");
	msg.append("\tNum of intercept missiles: " + array[1] + "\n");
	msg.append("\tNum of hit target missiles: " + array[2] + "\n");
	msg.append("\tNum of launchers destroyed: " + array[3] + "\n");
	msg.append("\ttotal damage: " + array[4] + "\n");
	msg.append("==========================================\n");
	statswindow.add(new JTextArea(msg.toString()));
	statswindow.setLocationRelativeTo(null);
	statswindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	statswindow.pack();
	statswindow.setVisible(true);
	animationPanel.stop();
	refreshInv.stop();
    }

    public void showWarHasBeenFinished() {
	logsTxtArea.append("[" + Utils.getCurrentTime()
		+ "] =========>> Finally THIS WAR IS OVER!!! <<========="
		+ "\r\n");
	for (WarEventUIListener l : listeners)
	    l.showStatistics();
	// System.out.println("[" + Utils.getCurrentTime() + "]");
    }

    public void showWarHasBeenStarted() {
	logsTxtArea.append("[" + Utils.getCurrentTime()
		+ "] =========>> War has been started!!! <<=========" + "\r\n");
	// System.out.println("[" + Utils.getCurrentTime() + "]");
    }

    public void showNoSuchObject(String type) {
	logsTxtArea.append("[" + Utils.getCurrentTime()
		+ "] ERROR: Cannot find " + type + " you selected in war"
		+ "\r\n");
    }

    public void showMissileNotExist(String defenseLauncherId, String enemyId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] ERROR: "
		+ defenseLauncherId + " tried to intercept, " + "but missed: "
		+ enemyId + " doesn't exist!" + "\r\n");
    }

    public void showLauncherNotExist(String defenseLauncherId, String launcherId) {
	logsTxtArea.append("[" + Utils.getCurrentTime() + "] ERROR: "
		+ defenseLauncherId + " tried to intercept, " + "but missed: "
		+ launcherId + " doesn't exist!" + "\r\n");
    }

    @Override
    public void registerListener(WarController controller) {
	listeners.add(controller);
    }

    @Override
    public void join() throws InterruptedException {
    }

    @Override
    public String getWarNameFromUser() {
	String s = JOptionPane.showInputDialog("Enter war name:");
	return s;
    }

    @Override
    public int showFirstDialog() {
	Object[] options = new Object[] { "Start war", "Show database view" };
	return JOptionPane.showOptionDialog(null,
		"Start war or show database view?", "Select an Option",
		JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
		options, options[0]);
    }

    @Override
    public void showDBDialog() {
	Application.launch(DBDialog.class, "");
    }

    @Override
    public void flushBuffers() {
    }
}
