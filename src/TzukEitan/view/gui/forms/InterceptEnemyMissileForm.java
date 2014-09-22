package TzukEitan.view.gui.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import TzukEitan.listeners.WarEventUIListener;

public class InterceptEnemyMissileForm extends FormPanel {
	private JComboBox<String> missileComboBox;

	public InterceptEnemyMissileForm(WarEventUIListener l) {
		
		if(l.chooseMissileToIntercept() == null){
			JLabel tempLbl1 = new JLabel("No Missiles available!");
			JLabel tempLbl2 = new JLabel("Add a new missile first");
			tempLbl1.setForeground(Color.RED);
			tempLbl2.setForeground(Color.RED);
			tempLbl1.setFont(new Font("Courier New", Font.BOLD, 18));
			tempLbl2.setFont(new Font("Courier New", Font.BOLD, 18));
			add(tempLbl1);
			add(tempLbl2);
		}
		else{
		add(new JLabel("Choose id"));
		add(missileComboBox = new JComboBox<>(l.chooseMissileToIntercept()));
		}
		this.setPreferredSize(new Dimension(280, 100));
	}

	@Override
	public Object[] getParams(){
		if(missileComboBox == null)
			return null;
		else
			return new Object[] { missileComboBox.getSelectedItem() };
	}

}
