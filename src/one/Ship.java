package one;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;


public class Ship implements Serializable {
  public transient static long collisionTime;
  public transient static int collisionCalls;
  
  public transient static boolean DRAWASCIRCLE = false;
	public transient static int COUNTER;
	public transient static World world;
	public Color player;
	public transient static final int WIDTH = 10;
	public transient static final int HALF = 5;
	public Point cur;
	private Point tar;
	private int dx;
	private int dy;
	public int width;
	public int half;
	public boolean dead;
  public boolean removeThis;
  private boolean invisible = false;
	public int shoot;
	public int SPEED;
	public int HEALTH;
	public int RANGE;
	public int DAMAGE;
	public int ATTACKCD;
	public int id;
	int supe;
	
	int lastdx;
	int lastdy;
	
//	transient ArrayList<Integer> state = new ArrayList<Integer>();
	
	public Ship() {
	  
	}
	public Ship(Ship other) { // create virtual copy of other ship
	  this(other.player, other.cur.x, other.cur.y, other.dead, other.id, other.width);
//	  if( other.isInvisible() ) {
//	    System.out.println("Making virtual copy of invisible ship");
//	  }
	  this.setInvisible(other.isInvisible());
	}
	
	public Ship(Color spl, int sx, int sy, boolean de, int nid, int wid) {// create virtual
		id=nid;
		player = spl;
		dead = de;
		cur = new Point(sx, sy);
		RANGE = 30;
		width = wid;
		half = wid/2;
		SPEED = 10;
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
		removeThis = false;
		width = wid;
		half = wid/2;
		
	}
	public void setInvisible(boolean invis) {
	  invisible = invis;
	}
	public boolean isInvisible() { return invisible; }
	/**
	 * used solely in main menu for cool animations
	 */
	public boolean moveToTarget() {
		if(tar == null)
			return false;
		cur = new Point(cur.x+dx, cur.y+dy);
		if(Math.abs(cur.x-tar.x)<SPEED) {
			dx = 0;
		}
		if(Math.abs(cur.y-tar.y)<SPEED) {
			dy = 0;
		}
		if(dx == 0 && dy == 0) {
			return true;
		}
		return false;
	}
	public void setD(int x, int y) {
		dx = x;
		dy = y;
	}
//	public void 
	public void tic() {
		if(dead) {
			world.removeShip(this);
			return;
		}
//		if(!world.collides(new Rectangle(cur.x+dx-half, cur.y+dy-half, width, width), this)) {
//			cur = new Point(cur.x+dx, cur.y+dy);
//		} else {
			if(!world.collides(new Rectangle(cur.x+dx-half, cur.y-half, width, width), this)) {//very inefficient collision checking
				cur = new Point(cur.x+dx, cur.y);
			}
			if(!world.collides(new Rectangle(cur.x-half, cur.y+dy-half, width, width), this)) {// (#ships)^2 checks per tic of world
				cur = new Point(cur.x, cur.y+dy);
			}
      Ship.collisionCalls += 2;
			
//		}
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
	public void drawMyShip(Graphics2D g, Point focus) {
    if(dead)
      g.setColor(Color.white);
    else {
      g.setColor(player);
      if( Ship.DRAWASCIRCLE ) {
        g.fillOval(cur.x-width/2-focus.x, cur.y-width/2-focus.y, width, width);
      } else {
        g.fillRect(cur.x-width/2-focus.x, cur.y-width/2-focus.y, width, width);
      }
      if( isInvisible()) { 
        g.setColor(Color.black);
        if( Ship.DRAWASCIRCLE ) {
          g.fillOval(cur.x-width/2-focus.x+2, cur.y-width/2-focus.y+2, width-4, width-4);
        } else {
          g.fillRect(cur.x-width/2-focus.x+2, cur.y-width/2-focus.y+2, width-4, width-4);
        }
        g.setColor(Color.black);
      }
    }
	}
	public void draw(Graphics2D g, Point focus) {
		if(dead)
			g.setColor(Color.white);
		else if( isInvisible() ) {
		  g.setColor(Color.black);
//      g.setColor(Color.red);
		}
		else 
			g.setColor(player);
		if(supe==2)
			g.fillRect(cur.x-width/2, cur.y-width/2, width, width);
		else {
		  if( Ship.DRAWASCIRCLE ) {
		    g.fillOval(cur.x-width/2-focus.x, cur.y-width/2-focus.y, width, width);
		  } else {
        g.fillRect(cur.x-width/2-focus.x, cur.y-width/2-focus.y, width, width);
		  }
		}
	}
	public boolean collides(Ship other) {
	  if( distance(other) < (this.width + other.width)/2 ) {
	    return true;
	  }
	  return false;
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
//	public String toString() {
//		String s = player.getRed()+" "+player.getGreen()+" "+player.getBlue()+" "+cur.x+" "+cur.y+" "+id+" ";
//		if(dead)
//			s+="dead ";
//		else 
//			s+="not ";
//		return s;
//	}
}
