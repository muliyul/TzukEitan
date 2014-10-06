package view.gui.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import listeners.WarEventUIListener;

public class AddEnemyMissilesForm extends FormPanel {
	private JComboBox<String> launcherCombo;
	private JComboBox<String> destinationCombo;
	private JSlider damageSlider;
	private JSlider flyTimeSlider;

	public AddEnemyMissilesForm(WarEventUIListener l) {
		if(l.showAllLaunchers() == null){
			JLabel tempLbl1 = new JLabel("No launchers available right now !");
			JLabel tempLbl2 = new JLabel("Add a new launcher first");
			tempLbl1.setForeground(Color.RED);
			tempLbl2.setForeground(Color.RED);
			tempLbl1.setFont(new Font("Courier New", Font.BOLD, 18));
			tempLbl2.setFont(new Font("Courier New", Font.BOLD, 18));
			add(tempLbl1);
			add(tempLbl2);
		}
		else{
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
		}
		this.setPreferredSize(new Dimension(280, 150));
	}

	@Override
	public Object[] getParams(){
		return new Object[] { launcherCombo.getSelectedItem(),
				destinationCombo.getSelectedItem(), damageSlider.getValue(),
				flyTimeSlider.getValue() };
	}

}
