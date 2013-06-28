package one;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
/*
	false 0
	true 1
	
	tar1 1
	tar2 2
	end 3
	
	Base 10001
	Ship 10002
	Laser 10003
	Stat 10004
	Init 10005
	Meteor 10006
	removeship 10007
	wall 10008
	Pause 10009
	Ping 10010
	Game Unavailable 10012
*/
public class Client implements Runnable{
	Socket socket;
	Frame frame;
	DataInputStream hostin;
	DataOutputStream hostout;
	String ip;
	Thread mythread;
	boolean stop = false;
	public Client(Frame f, String ip) {
		frame = f;
		this.ip = ip;
	    this.mythread = new Thread(this);
	}
	public void start() {
		mythread.start();
	}
	public void stop() {
		ArrayList<Integer> i  = new ArrayList<Integer>();
		i.add(10003);
		i.add(frame.me.getRed());
		i.add(frame.me.getGreen());
		i.add(frame.me.getBlue());
		send(i);
		stop = true;
	}
	public void read() {
		while(!stop) {
			try {
				int type = hostin.readInt();
				if(type==10001)
					frame.bases = new ArrayList<Base>();
				if(type==10008)
					frame.walls = new ArrayList<Wall>();
				int num = hostin.readInt();
				for(int a=0; a<num; a++) {
					if(type==10001) { //reading base
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x = hostin.readInt();
						int y = hostin.readInt();
						int width = hostin.readInt();
						int points = hostin.readInt();
						int totalworth = hostin.readInt();
						int health = hostin.readInt();
						Base b = new Base(new Color(red, gre, blu), x, y, width, points, totalworth);
						b.health = health;
						frame.bases.add(b);
					} else if(type == 10002) {// ship
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
						if(de==1) {
							dead=true;
						}
						Ship ship = new Ship(new Color(red, gre, blu), x, y, dead, id, wid);
						frame.removeShip(id);
						frame.ships.add(ship);
					} else if(type == 10003) {// laser
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x =hostin.readInt();
						int y = hostin.readInt();
						int damage =hostin.readInt();
						int time = hostin.readInt();
						int hit =hostin.readInt();
						boolean hi = false;
						if(hit==1) {
							hi = true;
						} else if(hit==0) {
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
					} else if(type==10004) {// stats
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
							int troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
							troll = hostin.readInt();
						}
					} else if(type==10005) {// init
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						frame.me = new Color(red, gre, blu);
						frame.setTitle("Space Player "+frame.me);
						frame.message = "Waiting for server to start game";
					} else if(type==10006) {// meteor
						int x = hostin.readInt();
						int y = hostin.readInt();
						int width = hostin.readInt();
						frame.met = new Meteor(x, y, width);
					} else if(type==10007) {// removeship
						int red = hostin.readInt();
						int gre = hostin.readInt();
						int blu = hostin.readInt();
						int x = hostin.readInt();
						int y = hostin.readInt();
						int id = hostin.readInt();
						int wid = hostin.readInt();
						int de = hostin.readInt();
						frame.removeShip(id);
					} else if(type==10008) {// wall
						int x = hostin.readInt();
						int y = hostin.readInt();
						int w = hostin.readInt();
						int h = hostin.readInt();
						frame.walls.add(new Wall(x, y, w, h));
					} else if(type==10009) {// pause
						int p = hostin.readInt();
						if(p==1) {
							frame.pause(true);
						} else if(p==0)
							frame.pause(false);
					} else if(type==10010) {// ping
						frame.pinged();
					} else if(type==10012) {// game unavailable
						frame.message="Game unavailable (either started or full)";
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
			for(Integer in : i) {
				hostout.writeInt(in);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		read();
	}
}
