package one;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Base implements Serializable {
	public transient static World world;
	public transient static Image heart;
	public transient static Image base;
	public final int id;
	public transient static int COUNTER;
	public Point cur;
	public Color player;
	public boolean dead;
	public boolean removeThis;
	
	private boolean invisactive = false;
	private int invistimer;
	private int INVISDURATION;
	private int invisCooldown;
	private int INVISCOOLDOWN;
	public int width;
	public int health;
	public int points;
	public int totalworth;
	public int numships;
	public int shoot;
	public int buildcd;
	public int regcd;
	public int REGEN;
	public int MAXSHIPS;
	public int BUILDCD;
	public int RANGE;
	public int HEALTH;
	public int DAMAGE;
	public int MOVESPEED;
	public int ATTACKCD;
	public int CHANCESUPER;
	
	public Base(Color pla, int x, int y, int wi, int pnts) {// actually create a base
		id = COUNTER++;
		if(heart==null) {
			ImageIcon ii = new ImageIcon("images/heart.png");
			heart = ii.getImage();
			ii = new ImageIcon("images/base.png");
			base = ii.getImage();
		}
		player = pla;
		cur = new Point(x, y);
		width = wi;
		points = pnts;
		totalworth=points;
		health = 1000;
		numships = 0;
		MAXSHIPS = 15;
		BUILDCD = 15;
		buildcd = BUILDCD;
    INVISDURATION = 100;
    INVISCOOLDOWN = 1000;
		RANGE = 50;
		HEALTH = 10;
		DAMAGE = 3;
		MOVESPEED = 10;
		ATTACKCD = 1;
		REGEN = 1;
		CHANCESUPER = 10;
	}
	public Base( Base other ) {
    this(new Color(other.player.getRed(), other.player.getGreen(), other.player.getBlue()), other.cur.x, other.cur.y, other.width, other.points, other.totalworth, other.id);
    this.invisCooldown = other.invisCooldown;
    this.INVISCOOLDOWN = other.INVISCOOLDOWN;
	}
	public Base(Color pla, int x, int y, int wi, int pnts, int totalworth, int sid) {// create a virtual base
		id = sid;
		if(heart==null) {
			ImageIcon ii = new ImageIcon("resources/images/heart.png");
			heart = ii.getImage();
			ii = new ImageIcon("resources/images/base.png");
			base = ii.getImage();
		}
		player = pla;
		cur = new Point(x, y);
		width = wi;
		points = pnts;
		this.totalworth=totalworth;
		health = 1000;
		numships = 0;
		MAXSHIPS = 15;
		BUILDCD = 15;
//		BUILDCD = 2;
		buildcd = BUILDCD;
		RANGE = 50;
		HEALTH = 10;
		DAMAGE = 3;
		MOVESPEED = 10;
		ATTACKCD = 10;
		REGEN = 1;
		CHANCESUPER = 10;
	}
	public void tic() {

    if(dead) {
      world.removeBase(this);
    } else {
      invisCooldown--;
  		if(regcd--<0) {
  			health+=10+REGEN;
  			if(health>1000+REGEN*100) {
  				health = 1000+REGEN*100;
  			}
  			regcd=20-REGEN;
  		}
  		if(buildcd--<0 && numships<MAXSHIPS) {
  			Ship ship = spawnShip();
  			buildcd=0;
  			if(ship!=null) {
  				buildcd = BUILDCD;
  				world.addShip(ship);
  				numships++;
  			}
  		}
  		if(shoot--<0) {
  			Ship tar = world.getinrange(this);
  			if(tar!=null) {
  				Laser l = new Laser(cur.x, cur.y, tar, DAMAGE*3, player, ATTACKCD, false);
  				world.addLaser(l);
  			} 
  			int sub = 0;
  			for(int a=13-ATTACKCD; a<13; a++) {
  				sub+=a;
  			}
  			shoot = (90-sub)*2/3;
  		}
  		if( invisactive ) {
  		  if( --invistimer < 0 ) {
  		    world.disableInvis(player);
  		    invisactive = false;
  		  }
  		}
    }
	}
	public void addpoints(int p) {
		points+=p;
		totalworth+=p;
	}
	public Ship trytospawn(int x, int y, int sup) {
		
		Rectangle r = new Rectangle(x-Ship.HALF*sup, y-Ship.HALF*sup, Ship.WIDTH*sup, Ship.WIDTH*sup);
		if(!world.collides(r)) {
			Ship ship = new Ship(player, x, y, HEALTH*sup, MOVESPEED, RANGE, DAMAGE*sup, ATTACKCD, Ship.WIDTH*sup, sup);
			return ship;
		}
		return null;
	}
	public Ship spawnShip() {
		int sup = 1;
		if(world.superenabled && Math.random()*100<CHANCESUPER) {
			sup = 2;
		}
		int wid = Ship.HALF*sup;
		double ran = Math.random();
		int x;
		int y;
		for(int a=0; a<40; a++) {
			if(ran<.25) {
				x = (int) (cur.x-width/2-Ship.HALF+Math.random()*(width+wid));
				y = cur.y-width/2-wid*2;
			} else if(ran<.5) {
				x = (int) (cur.x-width/2-Ship.HALF+Math.random()*(width+wid));
				y = cur.y+width/2+wid*2;
			} else if(ran<.75) {
				y = (int) (cur.y-width/2-Ship.HALF+Math.random()*(width+wid));
				x = cur.x-width/2-wid*2;
			} else {
				y = (int) (cur.y-width/2-Ship.HALF+Math.random()*(width+wid));
				x = cur.x+width/2+wid*2;
			}
			Ship s = trytospawn(x, y, sup);
			if(s!=null) {
	      s.setInvisible(invisactive);
				return s;
			}
		}
		return null;
	}
	public void draw(Graphics2D g, Point focus) {
		int drawx = cur.x-focus.x-width/2;
		int drawy = cur.y-focus.y-width/2;
		g.setColor(player);
		g.fillRect(drawx, drawy, width, width);
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		g.setColor(Color.white);
		g.drawImage(heart, drawx+1, cur.y-focus.y-2, 10, 10, null);
		g.drawImage(base, drawx, drawy, width, width, null);
    g.setColor(Color.black);
		g.drawString(health+"", drawx+8, cur.y-focus.y+7);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.drawString(totalworth+"", drawx, cur.y-focus.y+20);
		if( dead ) {
	    g.setColor(Color.black);
	    g.fillOval(drawx, drawy, width, width);
	    g.setColor(player);
	    g.fillOval(drawx+10, drawy+10, width-20, width-20);
		}
	}
	public double invisCooldownRatio() {
	  return 1.0 * invisCooldown/INVISCOOLDOWN;
	}
	public void activateInvis() {
	  if( invisCooldown <= 0 ) {
	    invisCooldown = INVISCOOLDOWN;
  	  invisactive = true;
  	  invistimer = INVISDURATION;
  	  world.activateInvis(player);
	  }
	}
	public boolean collides(Rectangle r) {
		if(r.intersects(new Rectangle(cur.x-width/2-1, cur.y-width/2-1, width+2, width+2))) {
			return true;
		}
		return false;
	}	
	public boolean equals(Base other) {
		return cur.equals(other.cur);
	}
	public boolean equals(int x, int y) {
		return (cur.x==x && cur.y==y);
	}
	public int regencost() {
		return REGEN;
	}
	public void upregen() {
		if(points>=regencost() && REGEN<20) {
			points-=regencost();
			REGEN++;
		}
	}
	public int healthcost() {
		return HEALTH/5-1;
	}
	public void uphealth() {
		if(points>=healthcost()) {
			points-=healthcost();
			HEALTH++;
		}
	}
	public int shipscost() {
		if(MAXSHIPS<20) {
			return MAXSHIPS-14;
		} else if(MAXSHIPS<30) {
			return MAXSHIPS/2-4;
		} else if(MAXSHIPS<40) {
			return MAXSHIPS/3+1;
		} else if(MAXSHIPS<50) {
			return MAXSHIPS/4+4;
		} else if(MAXSHIPS<100) {
			return MAXSHIPS/10+11;
		} else {
			return MAXSHIPS/50+19;
		}
	}
	public void upmaxships() {
		if(points>=shipscost()) {
			points-=shipscost();
			MAXSHIPS++;
		}
	}
	public int damagecost() {
		if(DAMAGE<9) {
			return DAMAGE-2;
		} else if(DAMAGE<21) {
			return DAMAGE/2+3;
		} else if(DAMAGE<40) {
			return DAMAGE/4+3;
		}
		else {
      return DAMAGE/4+3;
		}
	}
	public void updamage() {
		if(points>=damagecost()) {
			points-=damagecost();
			DAMAGE++;
		}
	}
	public int speedcost() {
		return MOVESPEED-9;
	}
	public void upspeed() {
		if(points>=speedcost()) {
			points-=speedcost();
			MOVESPEED+=2;
		}
	}
	public boolean damage(int dmg) {// not an upgrade
		if(dead)
			return false;
		health-=dmg;
		if(health<=0) {
			dead = true;
			return true;
		}
		return false;
	}
	public int attackcost() {
		return ATTACKCD*2;
	}
	public void upattackcd() {
		if(points>=attackcost() && ATTACKCD<8) {
			points-=attackcost();
			ATTACKCD+=1;
		}
	}
	private static final int RANGEINC = 5;
	public int rangecost() {
		return (RANGE-50+RANGEINC)/RANGEINC;
	}
	public void uprange() {
		if(points>=rangecost()) {
			points-=rangecost();
			RANGE+=RANGEINC;
		}
//		width++;
	}
	public int buildcdcost() {
		return (16-BUILDCD)*2;
	}
	public void upbuildcd() {
		if(points>=buildcdcost() && BUILDCD>=6) {
			points-=buildcdcost();
			BUILDCD--;
		}
	}
	public int supercost() {
		return (CHANCESUPER-3)/3;
	}
	public void upsuper() {
		if(world.superenabled && points>=supercost()) {
			points-=supercost();
			CHANCESUPER+=5;
//			width++;
		}
	}
	public ArrayList<Integer> convert() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(player.getRed());
		i.add(player.getGreen());
		i.add(player.getBlue());
		i.add(cur.x);
		i.add(cur.y);
		i.add(width);
		i.add(points);
		i.add(totalworth);
		i.add(health);
		i.add(id);
		return i;
	}
	public ArrayList<Integer> convertstats() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(player.getRed());
		i.add(player.getGreen());
		i.add(player.getBlue());
		i.add(DAMAGE);
		i.add(MOVESPEED);
		i.add(RANGE);
		i.add(BUILDCD);
		i.add(MAXSHIPS);
		i.add(HEALTH);
		i.add(ATTACKCD);
		i.add(REGEN);
		i.add(CHANCESUPER);
		i.add(points);
		i.add(damagecost());
		i.add(speedcost());
		i.add(rangecost());
		i.add(buildcdcost());
		i.add(shipscost());
		i.add(healthcost());
		i.add(attackcost());
		i.add(regencost());
		i.add(supercost());
		return i;
	}
	
//	public String getstatstring() {
//		return player.getRed()+" "+player.getGreen()+" "+player.getBlue()+" "+DAMAGE+" "+MOVESPEED+" "+RANGE+" "+BUILDCD+" "+MAXSHIPS+" "+HEALTH+" "+ATTACKCD+" "+points+" "+damagecost()+" "+speedcost()+" "+rangecost()+" "+buildcdcost()+" "+shipscost()+" "+healthcost()+" "+attackcost()+" ";
//	}
//	public String toString() {
//		return player.getRed()+" "+player.getGreen()+" "+player.getBlue()+" "+cur.x+" "+cur.y+" "+width+" "+points+" "+health+" ";
//	}
}
