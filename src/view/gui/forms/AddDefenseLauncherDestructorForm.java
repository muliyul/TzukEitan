package view.gui.forms;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import listeners.WarEventUIListener;


public class AddDefenseLauncherDestructorForm extends FormPanel {
    private static final long serialVersionUID = 7674793356713730124L;
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
