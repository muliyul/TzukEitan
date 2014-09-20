package TzukEitan.view.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;

public class AddEnemyMissilesForm extends JPanel implements FormPanel {
	private JComboBox<String> launcherCombo;
	private JComboBox<String> destinationCombo;
	private JSlider damageSlider;
	private JSlider flyTimeSlider;

	public AddEnemyMissilesForm(WarEventUIListener l) {
		setLayout(new GridLayout(5, 2, 1, 1));
		add(new JLabel("Choose launcher:"));
		add(launcherCombo = new JComboBox<String>(l.showAllLaunchers()));
		add(new JLabel("Choose destination:"));
		add(destinationCombo = new JComboBox<String>(l.getAllWarDestinations()));
		add(new JLabel("Enter damage:"));
		add(damageSlider = new JSlider());
		damageSlider.setMaximum(10000);
		damageSlider.setMajorTickSpacing(1000);
		add(new JLabel("Enter flytime"));
		add(flyTimeSlider = new JSlider());
		flyTimeSlider.setMaximum(30);
		flyTimeSlider.setMajorTickSpacing(5);
		this.setPreferredSize(new Dimension(280, 150));
	}

	@Override
	public Object[] getParams() throws Exception {
		return new Object[] { launcherCombo.getSelectedItem(),
				destinationCombo.getSelectedItem(), damageSlider.getValue(),
				flyTimeSlider.getValue() };
	}

}
