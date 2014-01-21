package one;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
			hostin = new DataInputStream(socket.getInputStream());
			hostout = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			frame.message = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			frame.message = e.toString();
			e.printStackTrace();
		}
		read();
	}
}
