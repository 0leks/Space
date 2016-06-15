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
	private int width;
	
	public Laser(int x, int y, Ship s, int dmg, Color c, int time, boolean sp) {
		spec = sp;
		cur = new Point(x, y);
		tar1 = s;
		damage = dmg;
		player = c;
		timetohit = time;
		this.setWidthFromDamage(damage);
	}
	public Laser(int x, int y, Base b, int dmg, Color c, int time, boolean sp) {
		spec = sp;
		cur = new Point(x, y);
		tar2  = b;
		damage = dmg;
		player = c;
		timetohit = time;
    this.setWidthFromDamage(damage);
	}
	
	public void setWidthFromDamage(int damage) {
	  this.width = (int)( Math.sqrt(damage)/2 );
	  if( this.width <= 0 ) 
	    this.width = 1;
	}
	public int getWidth() { return width;	}
	
	public void draw(Graphics2D g, Point focus) {
		g.setColor(player);
		if(tar1!=null) {
		  int start = -getWidth()/2;
		  for( int i = 0; i < getWidth(); i++ ) {
		    g.drawLine(cur.x-focus.x + start, cur.y-focus.y, tar1.cur.x-focus.x + start, tar1.cur.y-focus.y);
        g.drawLine(cur.x-focus.x, cur.y-focus.y + start, tar1.cur.x-focus.x, tar1.cur.y-focus.y + start);
		    start++;
		  }
		}
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
}
