package view.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import utils.ImageUtils;
import view.gui.polygons.GUIMissile;

public class AnimationPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 5350244391632396656L;
	private HashMap<Point, BufferedImage> explosions;
	private HashMap<String, GUIMissile> missilesMap;
	private Timer refresh;
	private final static int DIFF_HEIGHT = 50;
	private final static int EXPLOSION_TIME = 3000;
	private static int missY;

	public AnimationPanel() {
		explosions = new HashMap<>();
		missilesMap = new HashMap<String, GUIMissile>();
		refresh = new Timer(20, this);
		missY = 0;
		setBackground(Color.WHITE);
		refresh.start();
	}

	public void addMissile(String id, int flyTime) {
		missilesMap.put(id, new GUIMissile(id, 10, missY += DIFF_HEIGHT,
				flyTime));
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (GUIMissile m : missilesMap.values()) {
			g.drawPolygon(m);
			g.drawString(m.getId(), m.xpoints[0] + 10, m.ypoints[0] + 15);
		}
		for (Map.Entry<Point, BufferedImage> e : explosions.entrySet()) {
			g.drawImage(e.getValue(), e.getKey().x, e.getKey().y, null);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (GUIMissile m : missilesMap.values()) {
			if (m.xpoints[0] < getWidth())
				m.move(((getWidth() - 20) / (m.getFlyTime() * (1000.f / refresh
						.getDelay()))));
			else
				missilesMap.remove(m);
		}
		repaint();
	}

	public void stop() {
		refresh.stop();
	}

	public void intercept(String id) {
		GUIMissile m = missilesMap.remove(id);
		try {
			addExplosion(m.xpoints[0], m.ypoints[0], ImageIO.read(getClass()
					.getResource("/utils/Explosion.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void addExplosion(int x, int y, BufferedImage img) {
		new Thread() {
			public void run() {
				synchronized (explosions) {
					explosions.put(new Point(x, y), img);
				}
				try {
					Thread.sleep(EXPLOSION_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					synchronized (explosions) {
						explosions.remove(new Point(x, y));
					}
				}
			}
		}.start();

	}
}
