package TzukEitan.view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;
import TzukEitan.utils.ImageUtils;
import TzukEitan.view.AbstractWarView;
import TzukEitan.war.WarControl;

public class GUIView implements AbstractWarView {
	private List<WarEventUIListener> listeners;

	private String[] enemyImgsString = { "launcher.png", "missile.png" };
	private String[] enemyActionString = { "Add Launcher", "Launch Missile" };

	private String[] friendlyImgsString = { "irondome.png","","","" };
	private String[] friendlyActionString = { "Add Iron Dome","Add Launcher Destructor", "Intercept Missile","Intercept Launcher" };

	private JFrame mainFrame;
	private JPanel mainPanel;
	private JPanel enemyPanel;
	private JPanel friendlyPanel;
	private JPanel logsPanel;
	private JPanel animationPanel;
	private JPanel centerPanel;

	
	private JTextArea enemyInventory;
	private JScrollPane enemyInvPane;
	private JButton[] enemyActionBtnArray;

	
	private JTextArea friendlyInventory;
	private JScrollPane friendlyInvPane;
	private JButton[] friendlyActionBtnArray;

	private JPanel enemyActionsPanel;
	private JPanel friendlyActionPanel;

	private JPanel enemyFormPanel;
	private JPanel friendlyFormPanel;

	private JButton enemyApplyBtn;
	private JButton friendlyApplyBtn;

	private JButton enemyCancelBtn;
	private JButton friendlyCancelBtn;

	public GUIView() {
		listeners = new LinkedList<WarEventUIListener>();
		mainFrame = new JFrame("WarSim");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(1300, 800);
		mainPanel = new JPanel(new BorderLayout());
		logsPanel = new JPanel();
		animationPanel = new JPanel();
		centerPanel = new JPanel(new BorderLayout());
		enemyActionBtnArray = createButtonsArray(enemyImgsString,
				enemyActionString);
		
		friendlyActionBtnArray = createButtonsArray(friendlyImgsString,
				friendlyActionString);
		mainFrame.setLocationRelativeTo(null);

		enemyPanel = createSidePanels(enemyInventory = new JTextArea(),
				enemyInvPane = new JScrollPane(), enemyActionsPanel = new JPanel(), enemyActionBtnArray,
				enemyFormPanel = new JPanel());
		friendlyPanel = createSidePanels(friendlyInventory = new JTextArea(),
				friendlyInvPane = new JScrollPane(), friendlyActionPanel = new JPanel(), friendlyActionBtnArray,
				friendlyFormPanel = new JPanel());

		addBottomBtn(enemyPanel, enemyApplyBtn = new JButton("Apply"), enemyCancelBtn = new JButton("Cancel"));
		addBottomBtn(friendlyPanel, friendlyApplyBtn = new JButton("Apply"), friendlyCancelBtn = new JButton("Cancel"));
		enemyPanel.setBackground(Color.RED);
		friendlyPanel.setBackground(Color.GREEN);
		enemyPanel.setBorder(BorderFactory.createTitledBorder("Enemy Panel"));
		friendlyPanel.setBorder(BorderFactory
				.createTitledBorder("Friendly Panel"));
		logsPanel.setBorder(BorderFactory.createTitledBorder("Logs Panel"));
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
		
		enemyActionBtnArray[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : enemyActionBtnArray)
					jbt.setEnabled(true);
				fireAddEnemyLauncher();
				enemyActionBtnArray[0].setEnabled(false);
			}
		});
		enemyActionBtnArray[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : enemyActionBtnArray)
					jbt.setEnabled(true);
				fireAddEnemyMissile();
				enemyActionBtnArray[1].setEnabled(false);
			}
		});
		friendlyActionBtnArray[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
					jbt.setEnabled(true);
				fireAddDefenseIronDome();
				friendlyActionBtnArray[0].setEnabled(false);
			}
		});
		friendlyActionBtnArray[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
					jbt.setEnabled(true);
				fireAddDefenseLauncherDestructor();
				friendlyActionBtnArray[1].setEnabled(false);
			}
		});
		friendlyActionBtnArray[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
					jbt.setEnabled(true);
				fireInterceptMissile();
				friendlyActionBtnArray[2].setEnabled(false);
			}
		});
		friendlyActionBtnArray[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
					jbt.setEnabled(true);
				fireInterceptEnemyLauncher();
				friendlyActionBtnArray[3].setEnabled(false);
			}
		});
		
		enemyCancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : enemyActionBtnArray)
					jbt.setEnabled(true);
				enemyFormPanel.removeAll();
				enemyPanel.validate();				
			}
		});
		
		friendlyCancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JButton jbt : friendlyActionBtnArray)
					jbt.setEnabled(true);
				friendlyFormPanel.removeAll();
				friendlyPanel.validate();				
			}
		});
		
		mainFrame.setVisible(true);
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

	public JPanel createSidePanels(JTextArea txtArea,
			JScrollPane topScroll, JPanel actionPanel, JButton[] actionBtn,
			JPanel formPanel) {
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		sidePanel.setPreferredSize(new Dimension(300, 800));

		JPanel invenPanel = new JPanel();
		invenPanel.setLayout(new BoxLayout(invenPanel, BoxLayout.Y_AXIS));
		txtArea = new JTextArea(5, 1);
		topScroll = new JScrollPane(txtArea);
		invenPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
		invenPanel.add(topScroll);
		

		actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

		for (int i = 0; i < actionBtn.length; i++)
			actionPanel.add(actionBtn[i]);

		formPanel.setBorder(BorderFactory.createTitledBorder("Form"));

		sidePanel.add(invenPanel);
		sidePanel.add(actionPanel);
		sidePanel.add(formPanel);

		return sidePanel;
	}

	public void fireAddDefenseLauncherDestructor() {
		friendlyFormPanel.removeAll();
		for (WarEventUIListener l : listeners) {
			friendlyFormPanel.add(FormPanelFactory.get(
					FormPanelFactory.FormType.ADD_DEFENSE_LAUNCHER_DESTRUCTOR,
					l));
		}
		friendlyPanel.validate();
	}

	
	public void fireAddDefenseIronDome() {
		friendlyFormPanel.removeAll();
		for (WarEventUIListener l : listeners) {
			friendlyFormPanel.add(FormPanelFactory.get(
					FormPanelFactory.FormType.ADD_DEFENSE_IRON_DOME, l));
		}
		friendlyPanel.validate();
	}

	
	public void fireAddEnemyLauncher() {
		enemyFormPanel.removeAll();		
		for (WarEventUIListener l : listeners) {
			enemyFormPanel.add(FormPanelFactory.get(
					FormPanelFactory.FormType.ADD_ENEMY_LAUNCHER, l));
		}
		enemyPanel.validate();
	}

	
	public void fireAddEnemyMissile() {
		enemyFormPanel.removeAll();
		for (WarEventUIListener l : listeners) {
			enemyFormPanel.add(FormPanelFactory.get(
					FormPanelFactory.FormType.ADD_ENEMY_MISSILE, l));
		}
		enemyPanel.validate();
	}

	
	public void fireInterceptEnemyLauncher() {
		friendlyFormPanel.removeAll();
		for (WarEventUIListener l : listeners) {
			friendlyFormPanel.add(FormPanelFactory.get(
					FormPanelFactory.FormType.INTERCEPT_ENEMY_LAUNCHER, l));
		}
		friendlyPanel.validate();
	}

	
	public void fireInterceptMissile() {
		friendlyFormPanel.removeAll();
		for (WarEventUIListener l : listeners) {
			friendlyFormPanel.add(FormPanelFactory.get(
					FormPanelFactory.FormType.INTERCEPT_ENEMY_MISSILE, l));
		}
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
	public void showWarHasBeenStarted() {
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
	public void start() {
		// TODO Auto-generated method stub
		
	}
}
