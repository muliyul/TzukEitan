package TzukEitan.view.gui;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;


public class AddDefenseLauncherDestructorForm extends FormPanel {
	
	private JComboBox<String> launcherDestructorCombo;
	private String[] type = {"Ship", "Plane"};

	public AddDefenseLauncherDestructorForm(WarEventUIListener l) {
		add(new JLabel("Enter type:"));
		add(launcherDestructorCombo = new JComboBox<String>(type));
	}

	@Override
	public Object[] getParams(){
		return new Object[] { launcherDestructorCombo.getSelectedItem() };
	}

}
