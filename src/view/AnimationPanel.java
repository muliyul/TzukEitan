package view;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

import view.gui.polygons.GUIMissile;

public class AnimationPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 5350244391632396656L;
    private Arc2D arc;
    private List<GUIMissile> missiles = new Vector<>();
    private List<Polygon> polygons = new Vector<>();
    private Timer refresh = new Timer(200, this);

    public AnimationPanel() {
	refresh.start();
    }

    public void addMissile() {
	int lastMissileY = missiles.get(missiles.size() - 1).getY();
	missiles.add(new GUIMissile(lastMissileY + 50));
    }

    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	for (GUIMissile m : missiles) {
	    g.drawPolygon(m);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	polygons.clear();
	for (GUIMissile m : missiles) {
	    polygons.add(m.getAngledMissile(m.getAngle()));
	}
	repaint();
    }

    protected Arc2D getArcPath() {
	if (arc == null) {
	    arc = new Arc2D.Double();
	    arc.setArcByCenter(getWidth() / 2, getHeight() / 2, getWidth() / 2,
		    0, 180, Arc2D.OPEN);
	}
	return arc;
    }
}
