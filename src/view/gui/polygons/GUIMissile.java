package view.gui.polygons;

import java.awt.Polygon;

public class GUIMissile extends Polygon {
	private static final long serialVersionUID = -8586460405991855658L;

	private float xmath;
	private int flyTime;
	private String id;

	public GUIMissile(String id, int x, int y, int flyTime) {
		this(x, y);
		this.flyTime = flyTime;
		this.id = id;
	}

	private GUIMissile(int x, int y) {
		super();
		this.xmath = x;
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

	public void move(float dx) {
		xmath += dx;
		int temp = Math.round(xmath);
		translate(temp - xpoints[0], 0);
	}

	public int getFlyTime() {
		return flyTime;
	}

	public String getId() {
		return id;
	}
}
