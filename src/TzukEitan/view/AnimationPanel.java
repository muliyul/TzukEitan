package TzukEitan.view;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

import TzukEitan.view.gui.GUIMissile;

public class AnimationPanel extends JPanel implements ActionListener {
    private List<GUIMissile> missiles = new Vector<>();
    private Timer refresh = new Timer(200, this);
    private int flyTime;

    public static void main(String[] args) {
	QuadCurve2D q = new QuadCurve2D.Double();
	double[] eqn = {0, 0, 8}; 
	QuadCurve2D.solveQuadratic(eqn);
	System.out.println();
    }
    
    public void addMissile(int x, int y, int flyTime) {
	missiles.add(new GUIMissile(x, y));
	this.flyTime = flyTime;
	
    }

    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	for (GUIMissile m : missiles) {
	    g.drawPolygon(m);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	for (GUIMissile m : missiles) {
	    m.getAngledMissile(
		    m.xpoints[0] * Math.cos(Math.toRadians(m.getAngle() + 1)),
		    m.ypoints[0] * Math.sin(Math.toRadians(m.getAngle() + 1)),
		    m.getAngle() + 1);
	}
	repaint();
    }
}
