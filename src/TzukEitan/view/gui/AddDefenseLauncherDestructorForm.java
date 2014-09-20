package TzukEitan.view.gui;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AddDefenseLauncherDestructorForm extends JPanel implements
		FormPanel {
	
	private JComboBox<String> launcherDestructorCombo;
	private String[] type = {"Ship", "Plane"};

	public AddDefenseLauncherDestructorForm() {
		add(new JLabel("Enter type:"));
		add(launcherDestructorCombo = new JComboBox<String>(type));
	}

	@Override
	public Object[] getParams() throws Exception {
		return new Object[] { launcherDestructorCombo.getSelectedItem() };
	}

}
