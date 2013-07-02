package one;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

public class Meteor {
	public static Image image;
	public int x, y;
	public int dx, dy;
	public int width = 60;
	public int half = 30;
	ArrayList<Integer> state = new ArrayList<Integer>();
	
	public Meteor(int sx, int sy, int w) {// virtual
		x = sx;
		y = sy;
		width = w;
		state.add(x);
		state.add(y);
		state.add(width);
	}
	public Meteor(Point start, int spd, Point end) {// actual create
		System.out.println("creating meteor");
		x = start.x;
		y = start.y;
		int ddx = end.x-start.x;
		int ddy = end.y-start.y;
		if(ddx==0) {
			System.out.println("dx=0, terminate");
			disable();
			return;
		}
		double ratio = spd/Math.sqrt(ddx*ddx + ddy*ddy);
		dx = (int)(ddx*ratio);
		dy = (int)(ddy*ratio);
		if(ddx<0) {
			while(x<=1000+width && y>=-width && y<=800+width) {
				x-=dx;
				y-=dy;
			}
		} else if(ddx>0) {
			while(x>=-width && y>=-width && y<=800+width) {
				x-=dx;
				y-=dy;
			}
		}
	}
	public void collidedwith(Ship s) {
		s.damage(5);
		width+=2;
		half+=1;
	}
	public void draw(Graphics2D g) {
		g.setColor(Color.white);
		g.fillRect(x-width/2, y-width/2, width, width);
		g.setColor(Color.black);
		g.setFont(new Font("Arial", 50, 10));
		g.drawString("METEOR", x-width/2+6, y+5);
		g.drawImage(image, x-width/2, y-width/2, width, width, null);
	}
	public void tic() {
		x+=dx;
		y+=dy;
		if(x<=-width || x>1200+width || y<-width || y>800+width) {
			disable();
		}	
	}
	public boolean disabled() {
		return width==0;
	}
	public void disable() {
//		System.out.println("met is disabled");
		x = -999;
		y = -999;
		dx = 0;
		dy = 0;
		width = 0;
		half=0;
	}
	public ArrayList<Integer> convert() {
		state.set(0, x);
		state.set(1, y);
		state.set(2, width);
		return state;
	}
}
