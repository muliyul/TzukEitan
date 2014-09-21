package TzukEitan.view.gui;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

public class GUIMissile extends Polygon {
    public static GUIMissile FLAT_MISSILE = new GUIMissile(0, 0);
    
    private int x;
    private int y;
    private int flyTime;
    private int angle;

    public GUIMissile(int x, int y) {
	this.x = x;
	this.y = y;
	angle = 0;
	addPoint(x, y);
	addPoint(x += 15, y += 5);
	addPoint(x -= 15, y);
	addPoint(x += 55, y);
	addPoint(x, y += 10);
	addPoint(x, y -= 10);
	addPoint(x += 15, y += 5);
	addPoint(x -= 15, y += 5);
	addPoint(x -= 55, y);
	addPoint(x += 15, y);
	addPoint(x -= 15, y += 5);
	addPoint(x, y -= 20);
    }
    
    public void setAngle(int angle) {
	this.angle = angle;
    }
    
    public int getAngle() {
	return angle;
    }

    public GUIMissile getAngledMissile(double d, double e, int angle) {
	GUIMissile poly = new GUIMissile(FLAT_MISSILE.x, FLAT_MISSILE.y);
	for (int i = 0; i < npoints; i++) {
	    Point p = new Point(FLAT_MISSILE.xpoints[i], FLAT_MISSILE.ypoints[i]);
	    AffineTransform.getRotateInstance(Math.toRadians(angle)).transform(
		    p, p);
	    AffineTransform.getTranslateInstance(d, e).transform(p, p);
	    poly.addPoint(p.x, p.y);
	}
	return poly;
    }
}
