package TzukEitan.view.gui.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;

public class InterceptEnemyLauncherForm extends FormPanel {
	
	private JComboBox<String> launcherCombo;

	public InterceptEnemyLauncherForm(WarEventUIListener l) {
		
		if(l.chooseLauncherToIntercept() == null){
			JLabel tempLbl1 = new JLabel("No launchers available!");
			JLabel tempLbl2 = new JLabel("Add a new launcher first");
			tempLbl1.setForeground(Color.RED);
			tempLbl2.setForeground(Color.RED);
			tempLbl1.setFont(new Font("Courier New", Font.BOLD, 18));
			tempLbl2.setFont(new Font("Courier New", Font.BOLD, 18));
			add(tempLbl1);
			add(tempLbl2);
		}
		else{
		add(new JLabel("Choose id:"));
		add(launcherCombo = new JComboBox<>(l.chooseLauncherToIntercept()));
		}
		this.setPreferredSize(new Dimension(280, 100));
	}

	@Override
	public Object[] getParams(){
		if(launcherCombo == null)
			return null;
		else
			return new Object[] { launcherCombo.getSelectedItem() };
	}

}
