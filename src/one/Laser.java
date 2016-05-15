package one;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

public class Laser implements Serializable {
	public transient static World world;
	Point cur;
	Ship tar1;
	Base tar2;
	int damage;
	boolean hit = false;
	Color player;
	boolean spec;
	int timetohit;
	transient ArrayList<Integer> state = new ArrayList<Integer>();
	
	public Laser(int x, int y, Ship s, int dmg, Color c, int time, boolean sp) {
		spec = sp;
		cur = new Point(x, y);
		tar1 = s;
		damage = dmg;
		player = c;
		timetohit = time;
		
		state.add(player.getRed());
		state.add(player.getGreen());
		state.add(player.getBlue());
		state.add(cur.x);
		state.add(cur.y);
		state.add(damage);
		state.add(this.timetohit);
		state.add(0);
		state.add(3);
		state.add(0);
		state.add(0);
	}
	public Laser(int x, int y, Base b, int dmg, Color c, int time, boolean sp) {
		spec = sp;
		cur = new Point(x, y);
		tar2  = b;
		damage = dmg;
		player = c;
		timetohit = time;
		
		state.add(player.getRed());
		state.add(player.getGreen());
		state.add(player.getBlue());
		state.add(cur.x);
		state.add(cur.y);
		state.add(damage);
		state.add(this.timetohit);
		state.add(0);
		state.add(3);
		state.add(0);
		state.add(0);
	}
	public void draw(Graphics2D g, Point focus) {
		g.setColor(player);
		if(tar1!=null)
			g.drawLine(cur.x-focus.x, cur.y-focus.y, tar1.cur.x-focus.x, tar1.cur.y-focus.y);
		if(tar2!=null)
			g.drawLine(cur.x-focus.x, cur.y-focus.y, tar2.cur.x-focus.x, tar2.cur.y-focus.y);
	}
	public int tarx() {
		if(tar1!=null)
			return tar1.cur.x;
		if(tar2!=null)
			return tar2.cur.x;
		return 0;
	}
	public int tary() {
		if(tar1!=null)
			return tar1.cur.y;
		if(tar2!=null)
			return tar2.cur.y;
		return 0;
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
					world.baseAttacked(tar2);
					if(tar2.damage(damage)) {
						world.gotbasekill(player);
					}
					hit = true;
				}
			}
		}
	}
	public int getIDShipTarget() {
	  if( tar1 != null ) {
	    return tar1.getID();
	  }
	  return -1;
	}
	public int getTypeOfTarget() {
	  if(tar1!=null) {
      return 1;
    }
    
    if(tar2!=null) {
      return 2;
    }
    return 3;
	}
	public ArrayList<Integer> convert() {
		state.set(3, cur.x);
		state.set(4, cur.y);
		state.set(5, damage);
		state.set(6, this.timetohit);
		
		if (hit)
			state.set(7,1);
		else
			state.set(7,0);
			
		if(tar1!=null) {
			state.set(8,1);
			state.add(9,tar1.getID());
		}
		
		if(tar2!=null) {
			state.set(8,2);
			state.set(9,tar2.cur.x);
			state.set(10,tar2.cur.y);
		}
		
		if(tar1==null && tar2==null) {
			state.set(8,3);
		}
		
		return state;
	}
}
