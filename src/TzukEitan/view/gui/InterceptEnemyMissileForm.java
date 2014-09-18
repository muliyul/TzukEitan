package TzukEitan.view.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;

public class InterceptEnemyMissileForm extends JPanel implements FormPanel {
	private JTextField idTF;

	public InterceptEnemyMissileForm(WarEventUIListener l) {
		add(new JLabel("Enter id"));
		add(idTF = new JTextField());
	}

	@Override
	public Object[] getParams() throws Exception {
		return new Object[] { idTF.getText() };
	}

}
