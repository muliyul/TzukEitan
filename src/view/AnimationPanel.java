package view;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

import view.gui.polygons.GUIMissile;

public class AnimationPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 5350244391632396656L;
    private Vector<GUIMissile> missiles;
    private Timer refresh;
    private final static int DIFF_HEIGHT = 50;
    private static int missY;

    public AnimationPanel() {
	missiles = new Vector<>();
	refresh = new Timer(20, this);
	missY = 0;
	refresh.start();
    }

    protected void addMissile(int flyTime) {
	missiles.add(new GUIMissile(10, missY += DIFF_HEIGHT, flyTime));
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
	    m.move(((getWidth() - 10) / (m.getFlyTime() * (1000.f / refresh
		    .getDelay()))));
	}
	repaint();
    }

    protected void stop() {
	refresh.stop();
    }
}
