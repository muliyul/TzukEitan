package TzukEitan.view.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;

public class AddDefenseIronDomeForm extends JPanel implements FormPanel {
	private JTextField idTF;

	public AddDefenseIronDomeForm(WarEventUIListener l) {
		// TODO Auto-generated constructor stub
		add(new JLabel("Enter id:"));
		add(idTF = new JTextField(10));
	}

	@Override
	public Object[] getParams() throws Exception {
		return new Object[] { idTF.getText() };
	}

}
