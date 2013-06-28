package one;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Wall {
	int x, y, w, h;
	public Wall(int xx, int yy, int ww, int hh) {
		x = xx;
		y = yy;
		w = ww;
		h = hh;
	}
	public ArrayList<Integer> convert() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(x);
		ret.add(y);
		ret.add(w);
		ret.add(h);
		return ret;
	}
	public void draw(Graphics2D g) {
		g.setColor(Color.white);
		g.fillRect(x, y, w, h);
	}
	public boolean collides(Rectangle r) {
		if(r.intersects(new Rectangle(x-1, y-1, w+2, h+2))) {
			return true;
		}
		return false;
	}
}
