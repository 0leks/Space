package one;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
	false 0
	true 1
	
	updamage 1
	uprange 2
	upspeed 3
	uphealth 4
	upmaxships 5
	upbuildcd 6
	upattackspeed 7
	upregen 8
*/
public class Connection implements Runnable{
	DataInputStream in;
	DataOutputStream out;
	ObjectOutputStream output;
//	PrintWriter out2;
	World world;
	Thread thread;
	int life = 10;
	boolean dc = false; 
	Color player;
	public boolean ready;
	public static final int MOVE = 10001;
	public static final int UPGRADE = 10002;
	public static final int DISCONNECT = 10003;
	public static final int MAKEWALL = 10004;
	ArrayList<Integer> sendping;
	public static int name = 0;
	public Connection(World w, DataInputStream i, DataOutputStream o, ObjectOutputStream output) {
		in = i;
		out = o;
		this.output = output;
		world = w;
		sendping = new ArrayList<Integer>();
		sendping.add(Client.PING);
		sendping.add(1);
		thread = new Thread(this);
		ready = false;
	}
	public void setPlayer(Color c) {
		player = c;
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10005);
		i.add(1);
		i.add(c.getRed());
		i.add(c.getGreen());
		i.add(c.getBlue());
		send(player);
		//send(i);
	}
  public void reset() {
    try {
      output.reset();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
	public void send( Object o ) {
	  try {
      output.writeUnshared(o);
    } catch (IOException e) {
      processErrorSending(e);
    }
	}
	private void processErrorSending(IOException e) {
	  e.printStackTrace();
    if(life--<0) {
      dc = true;
      world.removeConnection(this);
      try {
        in.close();
        out.close();
        in = null;
        out = null;
        return;
      } catch (IOException e1) {
        System.err.println( "Error Sending Data, Most Likely due to Client disconnect" );
      }
    }
	}
	public void send(ArrayList<Integer> i) {
	  int a = 0;
	  if( a == 0 ) {
	    return;
	  }
		if(out==null)
			return;
		try {
			for(Integer in : i) {
				out.writeInt(in);
			}
		}catch (IOException e) {
      processErrorSending(e);
		}
	}
	public void read() {
		while(true) {
			boolean wait = true;
			try {
				int type = 0;
				while(type<10000)
					type = in.readInt();
				//System.out.println("Read:" + type);
				wait = false;
				int red = in.readInt();
				int gre = in.readInt();
				int blu = in.readInt();
				Color color = new Color(red, gre, blu);
				if(type==Connection.MOVE) {// move
					int x = in.readInt();
					int y = in.readInt();
					world.playerclicking(color, new Point(x, y));
				} else if(type==Connection.UPGRADE) {// upgrade
					int id = in.readInt();
					Base base = world.getbase(color);
					if(base==null) {
						return;
					}
					if(id==UpgradeButton.DAMAGE)
						base.updamage();
					if(id==UpgradeButton.RANGE)
						base.uprange();
					if(id==UpgradeButton.SPEED)
						base.upspeed();
					if(id==UpgradeButton.HEALTH)
						base.uphealth();
					if(id==UpgradeButton.MAXSHIPS)
						base.upmaxships();
					if(id==UpgradeButton.BUILDSPEED)
						base.upbuildcd();
					if(id==UpgradeButton.ATTACKSPEED)
						base.upattackcd();
					if(id==UpgradeButton.BASEHEALTH)
						base.upregen();
					if(id==UpgradeButton.SUPERCHANCE)
						base.upsuper();
					if(id == SpellButton.INVIS ) {
					  System.out.println("PRESSED the INVIS SPELL");
					  base.activateInvis();
					}
				} else if(type == Connection.DISCONNECT) {// disconnect
					world.removeConnection(this);
				} else if(type == Connection.MAKEWALL) {
					int x = in.readInt();
					int y = in.readInt();
					int w = in.readInt();
					int h = in.readInt();
					world.buildWall(x, y, w, h);
				} else if(type == Client.PING) {
					this.send(sendping);
				}
			} catch (IOException e) {
				if(!wait) {
					if(life--<0 && !dc) {
						world.removeConnection(this);
						dc = true;
						try {
							in.close();
							out.close();
							in = null;
							out = null;
							return;
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}
	@Override
	public void run() {
		read();
	}
	public void start() {
		thread.start();
	}

}
