package view.gui.forms;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import listeners.WarEventUIListener;

public class AddDefenseIronDomeForm extends FormPanel {
    private static final long serialVersionUID = 6891886905372180446L;

	public AddDefenseIronDomeForm(WarEventUIListener l) {
		// TODO Auto-generated constructor stub
		JLabel tempLbl1 = new JLabel("Press Apply to add");
		JLabel tempLbl2 = new JLabel("a new Iron Dome");
		tempLbl1.setFont(new Font("Courier New", Font.BOLD, 18));
		tempLbl2.setFont(new Font("Courier New", Font.BOLD, 18));
		add(tempLbl1);
		add(tempLbl2);
		this.setPreferredSize(new Dimension(280, 100));
	}

	@Override
	public Object[] getParams(){
		return null;
	}

}
