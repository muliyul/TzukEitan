package view.gui.polygons;

import java.awt.Polygon;

public class GUIMissile extends Polygon {
    private static final long serialVersionUID = -8586460405991855658L;

    private int flyTime;

    public GUIMissile(int x, int y, int flyTime) {
	this(x, y);
	this.flyTime = flyTime;
    }

    private GUIMissile(int x, int y) {
	super();
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

    public void move(int dx) {
	translate(dx, 0);
    }

    public int getFlyTime() {
	return flyTime;
    }

}
