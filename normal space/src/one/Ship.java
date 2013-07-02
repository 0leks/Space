package one;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Ship {
	public static int COUNTER;
	public static World world;
	public Color player;
	public static final int WIDTH = 10;
	public static final int HALF = 5;
	public Point cur;
	private Point tar;
	private int dx;
	private int dy;
	public int width;
	public int half;
	public boolean dead;
	public int shoot;
	public int SPEED;
	public int HEALTH;
	public int RANGE;
	public int DAMAGE;
	public int ATTACKCD;
	public final int id;
	int supe;
	
	ArrayList<Integer> state = new ArrayList<Integer>();
	
	public Ship(Color spl, int sx, int sy, boolean de, int nid, int wid) {// create virtual
		id=nid;
		player = spl;
		dead = de;
		cur = new Point(sx, sy);
		RANGE = 30;
		width = wid;
		half = wid/2;
	}
	public Ship(Color spl, int sx, int sy, int hp, int spd, int rng, int dmg, int attackcd, int wid, int sup) {// actually create
		supe = sup;
		id=COUNTER++;
		player = spl;
		SPEED = spd;
		ATTACKCD = attackcd;
		cur = new Point(sx, sy);
		RANGE = rng;
		HEALTH = hp;
		DAMAGE = dmg;
		dead = false;
		width = wid;
		half = wid/2;
		
		state.add(player.getRed());
		state.add(player.getGreen());
		state.add(player.getBlue());
		state.add(cur.x);
		state.add(cur.y);
		state.add(id);
		state.add(width);
		state.add(0);	//alive
	}
	public void setD(int x, int y) {
		dx = x;
		dy = y;
	}
	public void tic() {
		if(dead) {
			world.removeShip(this);
			return;
		}
		if(!world.collides(new Rectangle(cur.x+dx-half, cur.y-half, width, width), this)) {
			cur = new Point(cur.x+dx, cur.y);
		}
		if(!world.collides(new Rectangle(cur.x-half, cur.y+dy-half, width, width), this)) {
			cur = new Point(cur.x, cur.y+dy);
		}
		if(this.collides(world.met)) {
			world.met.collidedwith(this);
		}
		if(tar!=null) {
			if(Math.abs(cur.x-tar.x)<SPEED) {
				dx = 0;
			}
			if(Math.abs(cur.y-tar.y)<SPEED) {
				dy = 0;
			}
		}
		if(shoot--<0) {
			Base bas = world.getbaseinrange(this);
			if(bas!=null) {
				Laser l = new Laser(cur.x, cur.y, bas, DAMAGE, player, ATTACKCD, false);
				if(!world.collides(l))
					world.addLaser(l);
			} else  {
				Ship tar = world.getinrange(this);
				if(tar!=null) {
					Laser l = new Laser(cur.x, cur.y, tar, DAMAGE, player, ATTACKCD, false);
					if(!world.collides(l))
						world.addLaser(l);
				}
			} 
			int sub = 0;
			for(int a=13-ATTACKCD; a<13; a++) {
				sub+=a;
			}
			shoot = (90-sub)*2/3;
		}
		
	}
	public boolean damage(int dmg) {
		if(dead)
			return false;
		HEALTH-=dmg;
		if(HEALTH<=0) {
			dead = true;
			state.set(7, 1);
			return true;
		}
		return false;
	}
	public int distance(Ship other) {
		return Math.abs(cur.x-other.cur.x)+ Math.abs(cur.y-other.cur.y); 
	}
	public int distance(Base other) {
		return Math.abs(cur.x-other.cur.x)+ Math.abs(cur.y-other.cur.y); 
	}
	public boolean collides(Meteor other) {
		return (cur.x>=other.x-other.half && cur.x<=other.x+other.half && cur.y>=other.y-other.half && cur.y<=other.y+other.half);
	}
	public void setTarget(Point p ) {
		tar = p;
		int totaldistance = Math.abs(tar.x-cur.x)+Math.abs(tar.y-cur.y);
		if(totaldistance>SPEED) {
			double ratio = (double)SPEED/totaldistance;
			dx = (int) ((tar.x-cur.x)*ratio);
			dy = (int) ((tar.y-cur.y)*ratio);
		}
	}
	public void draw(Graphics2D g) {
		if(dead)
			g.setColor(Color.white);
		else
			g.setColor(player);
		g.fillRect(cur.x-width/2, cur.y-width/2, width, width);
	}
	public boolean collides(Rectangle r) {
		if(r.intersects(new Rectangle(cur.x-half-1, cur.y-half-1, width+2, width+2))) {
			return true;
		}
		return false;
	}
	public boolean equals(Ship other) {
		return player.equals(other.player) && cur.equals(other.cur);
	}
	public boolean equals(int nid) {
		return id==nid;
	}
	public int getID() {
		return id;
	}
	public ArrayList<Integer> convert() {
		state.set(3, cur.x);
		state.set(4, cur.y);
		return state;
	}
//	public String toString() {
//		String s = player.getRed()+" "+player.getGreen()+" "+player.getBlue()+" "+cur.x+" "+cur.y+" "+id+" ";
//		if(dead)
//			s+="dead ";
//		else 
//			s+="not ";
//		return s;
//	}
}
