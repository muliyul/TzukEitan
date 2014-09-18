package TzukEitan.view.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AddDefenseLauncherDestructorForm extends JPanel implements
		FormPanel {
	private JTextField type;

	public AddDefenseLauncherDestructorForm() {
		add(new JLabel("Enter type:"));
		add(type = new JTextField());
	}

	@Override
	public Object[] getParams() throws Exception {
		return new Object[] { type.getText() };
	}

}
