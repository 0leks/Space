package one;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Wall {
	int x, y, w, h;
	ArrayList<Integer> state = new ArrayList<Integer>();
	public Wall(int xx, int yy, int ww, int hh) {
		x = xx;
		y = yy;
		w = ww;
		h = hh;
		state.add(x);
		state.add(y);
		state.add(w);
		state.add(h);
		
	}
	public ArrayList<Integer> convert() {
		return state;
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
