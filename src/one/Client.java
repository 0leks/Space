package one;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import one.BetterFrame.GameFrame;
/*
	false 0
	true 1
	
	tar1 1
	tar2 2
	end 3
	
*/
public class Client implements Runnable{
	public static final int FALSE=0;
	public static final int TRUE=1;
	public static final int BASE=10001;
	public static final int SHIP=10002;
	public static final int LASER=10003;
	public static final int STATS=10004;
	public static final int INIT=10005;
	public static final int METEOR=10006;
	public static final int REMOVESHIP=10007;
	public static final int WALL=10008;
	public static final int PAUSE=10009;
	public static final int PING=10010;
	public static final int REMOVEBASE=10011;
	public static final int GAMEUNAVAILABLE=10012;
	public static final int WORLDSIZE=10013;
	public static final int BASEATTACKED=10014;
	
	Socket socket;
	GameFrame frame;
	DataInputStream hostin;
	DataOutputStream hostout;
	ObjectInputStream input;
	String ip;
	Thread mythread;
	boolean stop = false;
	public Client(GameFrame f, String ip) {
		frame = f;
		this.ip = ip;
	    this.mythread = new Thread(this);
	}
	public void start() {
		mythread.start();
	}
	public void stop() {
		stop = true;
		ArrayList<Integer> i  = new ArrayList<Integer>();
		i.add(10003);
		i.add(frame.me.getRed());
		i.add(frame.me.getGreen());
		i.add(frame.me.getBlue());
		send(i);
	}
	public void readObjects() {
	  while(!stop) {
	    try {
        Object object = input.readUnshared();
//        System.out.print("\nRead Object!");
        if( object != null ) {
//          System.out.print(" Not Null!");
          if( object instanceof Object[] ) {
//            System.out.print(" is Object[]!");
            Object[] objects = (Object[]) object;
//            System.out.print(" length=" + objects.length + "!");
            for( int index = 0; index < objects.length; index++ ) {
              Object obj = objects[index];
              if( obj instanceof Ship) {
                Ship ship = (Ship) obj;
                if(ship.removeThis) {
                  frame.removeShip(ship.id);
                }
                else {
                  frame.startgame();
                  Ship newship = new Ship(ship);
                  frame.readShip(newship);
                }
//                if( ship.id == 10 ) {
//                  System.out.println("Read ship id:" + ship.id + " x,y: " + ship.cur.x + "," + ship.cur.y);
//                }
              }
              else if( obj instanceof Base ) {
                Base base = (Base) obj;
                Base b = new Base(base);
                b.health = base.health;
                frame.readBase(b);
                int red = base.player.getRed();
                int gre = base.player.getGreen();
                int blu = base.player.getBlue();
                Color c = new Color( red, gre, blu);
                if(c.equals(frame.me)) {
                  frame.updamage.stat = base.DAMAGE;
                  frame.upspeed.stat = base.MOVESPEED;
                  frame.uprange.stat = base.RANGE;
                  frame.upbuildcd.stat = base.BUILDCD;
                  frame.upmaxships.stat = base.MAXSHIPS;
                  frame.uphealth.stat = base.HEALTH;
                  frame.upattackcd.stat = base.ATTACKCD;
                  frame.upregen.stat = base.REGEN;
                  frame.upsuper.stat = base.CHANCESUPER;
                  frame.points = base.points;
                  frame.updamage.cost = base.damagecost();
                  frame.upspeed.cost = base.speedcost();
                  frame.uprange.cost = base.rangecost();
                  frame.upbuildcd.cost = base.buildcdcost();
                  frame.upmaxships.cost = base.shipscost();
                  frame.uphealth.cost = base.healthcost();
                  frame.upattackcd.cost = base.attackcost();
                  frame.upregen.cost = base.regencost();
                  frame.upsuper.cost = base.supercost();
                }
              }
              else if( obj instanceof Laser ) {
                Laser laserFromStream = (Laser) obj;
                int red = laserFromStream.player.getRed();
                int gre = laserFromStream.player.getGreen();
                int blu = laserFromStream.player.getBlue();
                int x = laserFromStream.cur.x;
                int y = laserFromStream.cur.y;
                int damage = laserFromStream.damage;
                int time = laserFromStream.timetohit;
                boolean hit = laserFromStream.hit;
                int tar = laserFromStream.getTypeOfTarget();
                Laser laser = null;
                if(tar==1) {
                  int id = laserFromStream.getIDShipTarget();
                  for(Ship sh : frame.ships) {
                    if(sh.equals(id)) {
                      laser = new Laser(x, y, sh, damage, new Color(red, gre, blu), time, true);
                      laser.hit = hit;
                    }
                  }
                } else if(tar==2) {
                  int ax = laserFromStream.tar2.cur.x;
                  int ay = laserFromStream.tar2.cur.y;
                  for(Base b : frame.bases) {
                    if(b.equals(ax, ay)) {
                      laser = new Laser(x, y, b, damage, new Color(red, gre, blu), time, true);
                      laser.hit = hit;
                    }
                  }
                }
                if(laser!=null) {
                  frame.lasers.add(laser);
                }
              }
              else if( obj instanceof Meteor ) {
                Meteor meteor = (Meteor) obj;
                frame.met = new Meteor(meteor);
              }
              else if( obj instanceof Wall ) {
                Wall wall = (Wall) obj;
                frame.walls.add(new Wall(wall));
              }
            }
          }
          else if( object instanceof Color ) {
            Color color = (Color) object;
            int red = color.getRed();
            int gre = color.getGreen();
            int blu = color.getBlue();
            frame.me = new Color(red, gre, blu);
            frame.inverse = new Color(255-red, 255-gre, 255-blu);
            frame.setTitle("Space Player "+frame.me);
            frame.message = "Waiting for server to start game";
          }
          else if( object instanceof WorldInfo ) {
            WorldInfo wi = (WorldInfo) object;
            int width = wi.width;
            int height = wi.height;
            boolean superenabled = wi.superenabled;
            frame.setWorldSize(width, height);
            frame.setsuperenabled(superenabled);
            frame.pause(wi.paused);
          }
        }
      } catch (ClassNotFoundException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
	    
	  }
	}
	public void read() {
		while(!stop) {
			try {
				int type = 0;
				while(type<10000) {
					type = hostin.readInt();
				}
				if(type==Client.WALL)
					frame.walls = new ArrayList<Wall>();
				int num = hostin.readInt();
//				if(num!=1) {
//					System.out.println(num);
//					for(int a=0; a<10; a++) {
//						System.out.println(hostin.readInt());
//					}
//				}
				for(int a=0; a<num; a++) {
					if(type==Client.BASE) { //reading base
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x = hostin.readInt();
						int y = hostin.readInt();
						int width = hostin.readInt();
						int points = hostin.readInt();
						int totalworth = hostin.readInt();
						int health = hostin.readInt();
						int id = hostin.readInt();
						Base b = new Base(new Color(red, gre, blu), x, y, width, points, totalworth, id);
						b.health = health;
						frame.readBase(b);
					} else if(type == Client.SHIP) {// ship
						frame.startgame();
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x = hostin.readInt();
						int y = hostin.readInt();
						int id = hostin.readInt();
						int wid = hostin.readInt();
						int de = hostin.readInt();
						boolean dead = false;
						if(de==Client.TRUE) {
							dead=true;
						}
						Ship ship = new Ship(new Color(red, gre, blu), x, y, dead, id, wid);
						frame.readShip(ship);
					} else if(type == Client.LASER) {// laser
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x =hostin.readInt();
						int y = hostin.readInt();
						int damage =hostin.readInt();
						int time = hostin.readInt();
						int hit =hostin.readInt();
						boolean hi = false;
						if(hit==Client.TRUE) {
							hi = true;
						} else if(hit==Client.FALSE) {
							hi = false;
						}
						int tar = hostin.readInt();
						Laser laser = null;
						if(tar==3) {
							
						} else if(tar==1) {
							int id = hostin.readInt();
							for(Ship sh : frame.ships) {
								if(sh.equals(id)) {
									laser = new Laser(x, y, sh, damage, new Color(red, gre, blu), time, true);
									laser.hit = hi;
								}
							}
						} else if(tar==2) {
							int ax = hostin.readInt();
							int ay = hostin.readInt();
							for(Base b : frame.bases) {
								if(b.equals(ax, ay)) {
									laser = new Laser(x, y, b, damage, new Color(red, gre, blu), time, true);
									laser.hit = hi;
								}
							}
						}
						if(laser!=null) {
							frame.lasers.add(laser);
						}
					} else if(type==Client.STATS) {// stats
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						Color c = new Color(red, gre, blu);
						if(c.equals(frame.me)) {
							frame.updamage.stat = hostin.readInt();
							frame.upspeed.stat = hostin.readInt();
							frame.uprange.stat = hostin.readInt();
							frame.upbuildcd.stat = hostin.readInt();
							frame.upmaxships.stat = hostin.readInt();
							frame.uphealth.stat = hostin.readInt();
							frame.upattackcd.stat = hostin.readInt();
							frame.upregen.stat = hostin.readInt();
							frame.upsuper.stat = hostin.readInt();
							frame.points = hostin.readInt();
							frame.updamage.cost = hostin.readInt();
							frame.upspeed.cost = hostin.readInt();
							frame.uprange.cost = hostin.readInt();
							frame.upbuildcd.cost = hostin.readInt();
							frame.upmaxships.cost = hostin.readInt();
							frame.uphealth.cost = hostin.readInt();
							frame.upattackcd.cost = hostin.readInt();
							frame.upregen.cost = hostin.readInt();
							frame.upsuper.cost = hostin.readInt();
						} else {
//							int troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
//							troll = hostin.readInt();
						}
					} else if(type==Client.INIT) {// init
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						frame.me = new Color(red, gre, blu);
						frame.inverse = new Color(255-red, 255-gre, 255-blu);
						frame.setTitle("Space Player "+frame.me);
						frame.message = "Waiting for server to start game";
					} else if(type==Client.METEOR) {// meteor
						int x = hostin.readInt();
						int y = hostin.readInt();
						int width = hostin.readInt();
						frame.met = new Meteor(x, y, width);
					} else if(type==Client.REMOVESHIP) {// removeship
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x = hostin.readInt();
						int y = hostin.readInt();
						int id = hostin.readInt();
						int wid = hostin.readInt();
						int de = hostin.readInt();
						frame.removeShip(id);
					} else if(type==Client.WALL) {// wall
						int x = hostin.readInt();
						int y = hostin.readInt();
						int w = hostin.readInt();
						int h = hostin.readInt();
						frame.walls.add(new Wall(x, y, w, h));
					} else if(type==Client.PAUSE) {// pause
						int p = hostin.readInt();
						if(p==1) {
							frame.pause(true);
						} else if(p==0)
							frame.pause(false);
					} else if(type==Client.REMOVEBASE) {// removebase
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x = hostin.readInt();
						int y = hostin.readInt();
						int width = hostin.readInt();
						int points = hostin.readInt();
						int totalworth = hostin.readInt();
						int health = hostin.readInt();
						int id = hostin.readInt();
						frame.removeBase(id);
					} else if(type==Client.GAMEUNAVAILABLE) {// game unavailable
						frame.message="Game unavailable (either started or full)";
					} else if(type==Client.WORLDSIZE) {
						int width = hostin.readInt();
						int height = hostin.readInt();
						int sup = hostin.readInt();
						boolean superenabled = true;
						if(sup==Client.FALSE) {
							superenabled = false;
						}
						frame.setWorldSize(width, height);
						frame.setsuperenabled(superenabled);
					} else if(type == Client.PING) {
						int ping = (int) (System.currentTimeMillis()-this.frame.timepinged);
						frame.pinged(ping);
					} else if(type == Client.BASEATTACKED) {
						frame.baseattacked();
					}
				}
			} catch (IOException e) {
				frame.setTitle("ERROR reading");
				e.printStackTrace();
			}
		}
		try {
			hostout.close();
			hostin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void send(int in) {
		try {
			hostout.writeInt(in);
			System.out.println("Sending");
		} catch (IOException e) {
			frame.message="ERROR SENDING";
			e.printStackTrace();
		}
		frame.repaint();
	}
	public void send(ArrayList<Integer> i) {
		try {
			if(hostout!=null) {
				for(Integer in : i) {
					hostout.writeInt(in);
				}
			}
		} catch (IOException e) {
			frame.message="ERROR SENDING";
			e.printStackTrace();
		}
		frame.repaint();
	}
	@Override
	public void run() {
		InetAddress hostIP = null;
		try {
			hostIP = InetAddress.getByName(ip);
			if (hostIP == null) {
				return;
			}
			socket = new Socket(hostIP, 34555);
			//hostin = new DataInputStream(socket.getInputStream());
      input = new ObjectInputStream(socket.getInputStream());
			hostout = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			frame.message = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			frame.message = e.toString();
			e.printStackTrace();
		}
		Thread thread = new Thread() {
		  @Override
		  public void run() {
		    readObjects();
		  }
		};
		//thread.start();

    int red = 240;
    int gre = 120;
    int blu = 0;
    frame.me = new Color(red, gre, blu);
    frame.inverse = new Color(255-red, 255-gre, 255-blu);
    frame.setTitle("Space Player "+frame.me);
    frame.message = "Waiting for server to start game";
		readObjects();
		//read();
	}
}
