package TzukEitan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import TzukEitan.listeners.WarEventUIListener;
import TzukEitan.utils.ImageUtils;
import TzukEitan.utils.Utils;
import TzukEitan.view.gui.FormPanel;
import TzukEitan.view.gui.FormPanelFactory;
import TzukEitan.view.gui.GUIMissile;
import TzukEitan.war.WarControl;

public class GUIView implements AbstractWarView {
    private List<WarEventUIListener> listeners;

    private String[] enemyImgsString = { "launcher.png", "missile.png" };
    private String[] enemyActionString = { "Add Launcher", "Launch Missile" };

    private String[] friendlyImgsString = { "irondome.png", "", "", "" };
    private String[] friendlyActionString = { "Add Iron Dome",
	    "Add Launcher Destructor", "Intercept Missile",
	    "Intercept Launcher" };

    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel enemyPanel;
    private JPanel friendlyPanel;
    private JPanel logsPanel;
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

    private Timer refresh;

    public GUIView() {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		listeners = new LinkedList<WarEventUIListener>();
		mainFrame = new JFrame("WarSim");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(1300, 800);
		mainPanel = new JPanel(new BorderLayout());
		logsPanel = new JPanel();
		animationPanel = new AnimationPanel();
		
		animationPanel.addMissile(10,120,10);
		
		centerPanel = new JPanel(new BorderLayout());
		JPanel enemyActionsPanel, friendlyActionPanel;
		enemyActionBtnArray = createButtonsArray(enemyImgsString,
			enemyActionString);

		friendlyActionBtnArray = createButtonsArray(friendlyImgsString,
			friendlyActionString);
		mainFrame.setLocationRelativeTo(null);

		enemyPanel = createSidePanels(enemyInventory = new JTextArea(),
			enemyActionsPanel = new JPanel(), enemyActionBtnArray,
			enemyFormPanelContainer = new FormPanel());

		friendlyPanel = createSidePanels(
			friendlyInventory = new JTextArea(),
			friendlyActionPanel = new JPanel(),
			friendlyActionBtnArray,
			friendlyFormPanelContainer = new FormPanel());

		addBottomBtn(enemyPanel, enemyApplyBtn = new JButton("Apply"),
			enemyCancelBtn = new JButton("Cancel"));
		addBottomBtn(friendlyPanel, friendlyApplyBtn = new JButton(
			"Apply"), friendlyCancelBtn = new JButton("Cancel"));

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
		mainFrame.setContentPane(mainPanel);

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
				l.interceptGivenLauncher((String) friendlyFormPanel
					.getParams()[0]);
				break;
			    }
			    case "interceptmissile": {
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

		refresh = new Timer(Utils.SECOND, new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			for (WarEventUIListener l : listeners) {
			    enemyInventory.setText(l.requestEnemyInventory());
			    friendlyInventory.setText(l
				    .requestFriendlyInventory());
			    enemyInventory.repaint();
			    friendlyInventory.repaint();
			}
		    }
		});
		refresh.start();
		mainFrame.setVisible(true);
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
		    .add(friendlyFormPanel = FormPanelFactory
			    .get(FormPanelFactory.FormType.ADD_DEFENSE_LAUNCHER_DESTRUCTOR,
				    l));
	}
	friendlyApplyBtn.setActionCommand("adddefenselauncherdestructor");
	friendlyPanel.validate();
    }

    public void fireAddDefenseIronDome() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer.add(friendlyFormPanel = FormPanelFactory
		    .get(FormPanelFactory.FormType.ADD_DEFENSE_IRON_DOME, l));
	}
	friendlyApplyBtn.setActionCommand("adddefenseirondome");
	friendlyPanel.validate();
    }

    public void fireAddEnemyLauncher() {
	enemyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    enemyFormPanelContainer.add(enemyFormPanel = FormPanelFactory.get(
		    FormPanelFactory.FormType.ADD_ENEMY_LAUNCHER, l));
	}
	enemyApplyBtn.setActionCommand("addenemylauncher");
	enemyPanel.validate();
    }

    public void fireAddEnemyMissile() {
	enemyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    enemyFormPanelContainer.add(enemyFormPanel = FormPanelFactory.get(
		    FormPanelFactory.FormType.ADD_ENEMY_MISSILE, l));
	}
	enemyApplyBtn.setActionCommand("addenemymissile");
	enemyPanel.validate();
    }

    public void fireInterceptEnemyLauncher() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer
		    .add(friendlyFormPanel = FormPanelFactory.get(
			    FormPanelFactory.FormType.INTERCEPT_ENEMY_LAUNCHER,
			    l));
	}
	friendlyApplyBtn.setActionCommand("interceptlauncher");
	friendlyPanel.validate();
    }

    public void fireInterceptMissile() {
	friendlyFormPanelContainer.removeAll();
	for (WarEventUIListener l : listeners) {
	    friendlyFormPanelContainer.add(friendlyFormPanel = FormPanelFactory
		    .get(FormPanelFactory.FormType.INTERCEPT_ENEMY_MISSILE, l));
	}
	friendlyApplyBtn.setActionCommand("interceptmissile");
	friendlyPanel.validate();
    }

    public void fireShowStatistics() {
	// TODO Auto-generated method stub

    }

    public void fireFinishWar() {
	// TODO Auto-generated method stub

    }

    @Override
    public void registerListener(WarControl controller) {
	listeners.add(controller);

    }

    @Override
    public void showDefenseLaunchMissile(String myMunitionsId,
	    String missileId, String enemyMissileId) {

    }

    @Override
    public void showDefenseLaunchMissile(String myMunitionsId, String type,
	    String missileId, String enemyLauncherId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showEnemyLaunchMissile(String myMunitionsId, String missileId,
	    String destination, int damage) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showLauncherIsVisible(String id, boolean visible) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showMissInterceptionMissile(String whoLaunchedMeId,
	    String missileId, String enemyMissileId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showEnemyHitDestination(String whoLaunchedMeId,
	    String missileId, String destination, int damage) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showHitInterceptionMissile(String whoLaunchedMeId,
	    String missileId, String enemyMissileId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showMissInterceptionLauncher(String whoLaunchedMeId,
	    String type, String enemyLauncherId, String missileId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showMissInterceptionHiddenLauncher(String whoLaunchedMeId,
	    String type, String enemyLauncherId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showHitInterceptionLauncher(String whoLaunchedMeId,
	    String type, String enemyLauncherId, String missileId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void join() throws InterruptedException {
	// TODO Auto-generated method stub

    }

    @Override
    public void showWarHasBeenFinished() {
	// TODO Auto-generated method stub

    }

    @Override
    public void showNoSuchObject(String type) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showMissileNotExist(String defenseLauncherId, String enemyId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showLauncherNotExist(String defenseLauncherId, String launcherId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showEnemyMissDestination(String whoLaunchedMeId, String id,
	    String destination, String launchTime) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showStatistics(long[] statisticsToArray) {
	// TODO Auto-generated method stub

    }

    @Override
    public void showWarHasBeenStarted() {
	// TODO Auto-generated method stub

    }
}
