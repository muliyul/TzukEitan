package view.gui.polygons;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import com.sun.org.apache.xpath.internal.operations.And;

public class GUIMissile extends Polygon {
    public static Polygon FLAT_MISSILE;

    static {
	int x=0,y=0;
	FLAT_MISSILE.addPoint(x, y);
	FLAT_MISSILE.addPoint(x += 15, y += 5);
	FLAT_MISSILE.addPoint(x -= 15, y);
	FLAT_MISSILE.addPoint(x += 55, y);
	FLAT_MISSILE.addPoint(x, y += 10);
	FLAT_MISSILE.addPoint(x, y -= 10);
	FLAT_MISSILE.addPoint(x += 15, y += 5);
	FLAT_MISSILE.addPoint(x -= 15, y += 5);
	FLAT_MISSILE.addPoint(x -= 55, y);
	FLAT_MISSILE.addPoint(x += 15, y);
	FLAT_MISSILE.addPoint(x -= 15, y += 5);
	FLAT_MISSILE.addPoint(x, y -= 20);
    }



    private int x;
    private int y;
    private int angle;
    
    public GUIMissile(int x, int y) {
	this.x = x;
	this.y = y;
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

    public Polygon getAngledMissile(int angle) {
	this.angle = angle;
	Polygon poly = new Polygon();
	for (int i = 0; i < npoints; i++) {
	    Point p = new Point(FLAT_MISSILE.xpoints[i],
		    FLAT_MISSILE.ypoints[i]);
	    AffineTransform.getRotateInstance(Math.toRadians(angle)).transform(
		    p, p);
	    /*AffineTransform.getTranslateInstance(dx, dy).transform(p, p);
	    poly.addPoint(p.x, p.y);*/
	}
	return poly;
    }

    public int getAngle() {
	return angle;
    }

    public int getY() {
	return y;
    }
}
