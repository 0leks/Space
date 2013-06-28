package one;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Laser {
	public static World world;
	Point cur;
	Ship tar1;
	Base tar2;
	int damage;
	boolean hit = false;
	Color player;
	boolean spec;
	int timetohit;
	public Laser(int x, int y, Ship s, int dmg, Color c, int time, boolean sp) {
		spec = sp;
		cur = new Point(x, y);
		tar1 = s;
		damage = dmg;
		player = c;
		timetohit = time;
	}
	public Laser(int x, int y, Base b, int dmg, Color c, int time, boolean sp) {
		spec = sp;
		cur = new Point(x, y);
		tar2  = b;
		damage = dmg;
		player = c;
		timetohit = time;
	}
	public void draw(Graphics2D g) {
		g.setColor(player);
		if(tar1!=null)
			g.drawLine(cur.x, cur.y, tar1.cur.x, tar1.cur.y);
		if(tar2!=null)
			g.drawLine(cur.x, cur.y, tar2.cur.x, tar2.cur.y);
	}
	public void tic() {
		if(timetohit--<0) {
			if(spec) {
				hit = true;
			}
			if(!hit && !spec) {
				if(tar1!=null) {
					if(tar1.damage(damage)) {
						if(tar1.supe==1)
							world.gotshipkill(player);
						else 
							world.gotsuperkill(player);
					}
					hit = true;
				}
				if(tar2!=null) {
					if(tar2.damage(damage)) {
						world.gotbasekill(player);
					}
					hit = true;
				}
			}
		}
	}
	public ArrayList<Integer> convert() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(player.getRed());
		i.add(player.getGreen());
		i.add(player.getBlue());
		i.add(cur.x);
		i.add(cur.y);
		i.add(damage);
		i.add(this.timetohit);
		if(hit)
			i.add(1);
		else
			i.add(0);
		if(tar1!=null) {
			i.add(1);
			i.add(tar1.getID());
		}
		if(tar2!=null) {
			i.add(2);
			i.add(tar2.cur.x);
			i.add(tar2.cur.y);
		}
		if(tar1==null && tar2==null) {
			i.add(3);
		}
		return i;
	}
}
