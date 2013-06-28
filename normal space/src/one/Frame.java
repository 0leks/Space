package one;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JFrame implements ActionListener{
//	static World m+363yworld;
	
	public boolean mousedown;
	Panel mypanel;
	ArrayList<Point> stars;
	public Image star;
	public Color me;
	boolean gamestarted = false;
	String message = "";
	String pingtime = "";
	
	public MyButton updamage;
	public MyButton upspeed;
	public MyButton uprange;
	public MyButton uphealth;  
	public MyButton upattackcd;
	
	public MyButton upbuildcd;
	public MyButton upmaxships;
	public MyButton upregen;
	public MyButton upsuper;
	public int points;
	
	public Meteor met;
	public ArrayList<Ship> ships;
	public ArrayList<Base> bases;
	public ArrayList<Laser> lasers;
	public ArrayList<Wall> walls;
	public ArrayList<MyButton> buttons;
	Client client;
	Timer t;
	boolean pause;
	boolean tutorial = true;
	Image tutor;
	long ping = 0;
	int world;
	public Frame(String ip) {
		ImageIcon ii = new ImageIcon("images/star.gif");
		star = ii.getImage();
		ii = new ImageIcon("images/meteor.png");
		Meteor.image = ii.getImage();
		ii = new ImageIcon("images/tutorial.png");
		tutor = ii.getImage();
		stars = new ArrayList<Point>();
	
		ships = new ArrayList<Ship>();
		bases = new ArrayList<Base>();
		lasers = new ArrayList<Laser>();
		walls = new ArrayList<Wall>();
		buttons = new ArrayList<MyButton>();
		mypanel = new Panel();

		this.add(mypanel);
		mypanel.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		updamage = new MyButton("DAMAGE", 1, '1');
		updamage.setToolTipText("Damage dealt per laser.");
		initbutton(updamage, mypanel.getWidth()-110, 70, 100, 50);
		uprange = new MyButton("RANGE", 2, '2');
		uprange.setToolTipText("Maximum length of a laser.");
		initbutton(uprange, mypanel.getWidth()-110, 130, 100, 50);
		upspeed = new MyButton("SPEED", 3, '3');
		upspeed.setToolTipText("Movement speed of each ship.");
		initbutton(upspeed, mypanel.getWidth()-110, 190, 100, 50);
		uphealth = new MyButton("HEALTH", 4, '4');
		uphealth.setToolTipText("Maximum health of each ship");
		initbutton(uphealth, mypanel.getWidth()-110, 250, 100, 50);
		upattackcd = new MyButton("ATTACKSP", 7, '5');
		upattackcd.setToolTipText("How frequently your base and ships shoot lasers.");
		initbutton(upattackcd, mypanel.getWidth()-110, 310, 100, 50);
		

		upmaxships = new MyButton("MAXSHIPS", 5, '6');
		upmaxships.setToolTipText("The total number of ships your base can support.");
		initbutton(upmaxships, mypanel.getWidth()-110, 440, 100, 50);
		upbuildcd = new MyButton("BUILDSP", 6, '7');
		upbuildcd.setToolTipText("The time it takes to build a ship.");
		initbutton(upbuildcd, mypanel.getWidth()-110, 500, 100, 50);
		upregen = new MyButton("BASEHP", 8, '8');
		upregen.setToolTipText("TotalBaseHealth= 1000 + BASEHP*100   BASEHP also increases base health regeneration.");
		initbutton(upregen, mypanel.getWidth()-110, 560, 100, 50);
		upsuper = new MyButton("SUPER", 9, '9');
		upsuper.setToolTipText("% chance of building a Super.");
		initbutton(upsuper, mypanel.getWidth()-110, 620, 100, 50);
		
		for(Component c : mypanel.getComponents()) {
			if(c instanceof MyButton) {
				buttons.add((MyButton)c);
				((MyButton)c).setFocusable(false);
			}
		}

		me = Color.white;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//		this.setTitle("Space");
    	this.setLayout(null);
    	mypanel.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
		this.setFocusable(true);
		this.addMouseListener(new Listener());
		this.addMouseMotionListener(new Listener());
		this.addKeyListener(new Listener());
		this.validate();
		for(int a=0; a<100; a++) {
			stars.add(new Point((int) (Math.random()*getWidth()), (int)(Math.random()*getHeight())));
		}
		this.repaint();
        this.setVisible(true);
		this.requestFocus();
		

		message="Connecting to "+ip+" ...";
		client = new Client(this, ip);
		message="Failed to connect to "+ip+" ...";
		client.start();
		t = new Timer(100, this);
		t.start();
	}
	public void removeShip(int id) {
		for(int a = 0; a<ships.size(); a++) {
			Ship s = ships.get(a);
			if(s.id==id) {
				ships.remove(s);
			}
		}
	}
	public void pause(boolean yes) {
		if(yes) {
			message="GAME PAUSED";
			pause = true;
		} else {
			message = "GAME RESUMED";
			pause = false;
		}
	}
	public void startgame() {
		if(!gamestarted) {
			gamestarted = true;
			message="";
		}
	}
	public void initbutton(MyButton b, int x, int y, int w, int h) {
		b.setIgnoreRepaint(true);
		b.setVisible(true);
		b.setSize(new Dimension(w, h));
		b.addActionListener(new Listener());
		b.setLocation(x, y);
		b.setLayout(null);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setContentAreaFilled(false);
		b.setFont(new Font("Arial", Font.PLAIN, 12));
		b.setForeground(me);
		mypanel.add(b);
	}
	public class Panel extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        	rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        	g2d.setRenderingHints(rh);
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			for(Point p : stars) {
				g2d.drawImage(star, p.x, p.y, 8, 8, null);
			}
			g.setColor(Color.white);
			g.drawRect(0, 0, 10, 10);
			if(met!=null)
				met.draw(g2d);
			for(int a=0; a<ships.size(); a++) {
				Ship s=ships.get(a);
				s.draw(g2d);
			}
			for(int a=0; a<bases.size(); a++) {
				Base b = bases.get(a);
				b.draw(g2d);
			}
			for(int a=0; a<walls.size(); a++) {
				Wall w = walls.get(a);
				w.draw(g2d);
			}
			for(int a=0; a<lasers.size(); a++) {
				Laser l = lasers.get(a);
				l.draw(g2d);
			}
//			g.setColor(Color.white);
//			g.fillRect(1260, 0, 100, getHeight());
			g.setColor(me);
			g.fillRect(this.getWidth()-200, 0, 200, 370);
			g.setColor(Color.black);
			g.fillRect(this.getWidth()-195, 5, 190, 360);
			g.setColor(me);
			g.setFont(new Font("Arial", 50, 70));
			g.drawString("Ships", getWidth()-190, 60);
			
			
			g.setColor(me);
			g.fillRect(this.getWidth()-200, 375, 200, 305);
			g.setColor(Color.black);
			g.fillRect(this.getWidth()-195, 380, 190, 295);
			g.setColor(me);
			g.drawString("Base", getWidth()-190, 435);
			

			g.setFont(new Font("Arial", 50, 110));
			g.setColor(Color.white);
			g.drawString(points+"", this.getWidth()-185, 740);
			for(Component c : this.getComponents()) {
				if(c instanceof MyButton) {
					draw(g2d, (MyButton)c);
				}
			}
			g.setColor(Color.white);
			g.fillRect(getWidth()-20, getHeight()-20, 20, 20);
			g.setFont(new Font("Arial", Font.PLAIN, 50));
			g.drawString(message, 20, getHeight()/2);
			if(tutorial) {
				g.drawImage(tutor, 0, 0, this.getWidth(), this.getHeight(), null);
			}
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.drawString(pingtime, 2, 22);
		}
	}
	public ArrayList<Integer> convertmovetostring(MouseEvent e) {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10001);
		i.add(me.getRed());
		i.add(me.getGreen());
		i.add(me.getBlue());
		i.add(e.getX());
		i.add(e.getY());
		return i;
	}
	public void draw(Graphics2D g, MyButton b) {
		g.setColor(me);
		g.fillRect(b.getLocation().x, b.getLocation().y,b.getSize().width, b.getSize().height);
		g.setColor(Color.white);
		g.fillRect(b.getLocation().x-2, b.getLocation().y+20, 30, 10);
		g.fillRect(b.getLocation().x+10, b.getLocation().y+10,b.getSize().width-20, b.getSize().height-20);
//		g.setFont(new Font("Helvetica", Font.BOLD, 20));//new Font("Arial", Font.BOLD, 12));
//		g.setColor(me);
//		g.drawString(b.getActionCommand(), b.getLocation().x+12, b.getLocation().y+30);
		g.setColor(me);
		g.setFont(new Font("Arial", Font.BOLD, 40));
    	FontMetrics metr = this.getFontMetrics(g.getFont());
		g.drawString(""+b.stat, b.getLocation().x-metr.stringWidth(""+b.stat), b.getLocation().y+40);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString(""+b.cost, b.getLocation().x-65, b.getLocation().y+35);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		g.drawString(""+b.hotkey, b.getLocation().x+1, b.getLocation().y+45);
	}
	public void pinged() {
		long dif = ping-System.currentTimeMillis();
		pingtime = "ping: "+dif;
	}
	public class Listener implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			if(mousedown) {
				//myworld.playerclicking(me, e.getPoint());
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(mousedown) {
				//myworld.playerclicking(me, e.getPoint());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mousedown = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mousedown = false;
//			myworld.frameinteracted(convertmovetostring(e));
			client.send(convertmovetostring(e));
			if(e.getX()>getWidth()-20 && e.getY()>getHeight()-20) {
				client.stop();
				System.exit(0);
			}
			if(e.getX()<20 && e.getY()<20) {
				ArrayList<Integer> i = new ArrayList<Integer>();
				i.add(10004);
				i.add(me.getRed());
				i.add(me.getGreen());
				i.add(me.getBlue());
				client.send(i);
				ping = System.currentTimeMillis();
				System.out.println("pinging");
			}
		}
		@Override
		public void mouseDragged(MouseEvent e) {
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			for(int a=0; a<ships.size(); a++) {
				Ship s=ships.get(a);
				if(s.dead)
					ships.remove(a--);
			}
			if(e.getSource() instanceof MyButton) {
				buttonpressed((MyButton)e.getSource());
			}
		}
		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyReleased(KeyEvent e) {
			for(MyButton b : buttons) {
				System.out.println("checking button");
				if(e.getKeyChar()==b.hotkey) {
					buttonpressed(b);
				}
			}
			if(e.getKeyCode()==KeyEvent.VK_SPACE) {
				tutorial=false;
			}
			if(e.getKeyCode()==KeyEvent.VK_R) {
//				sendready();
			}
			
		}
		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	public void buttonpressed(MyButton b) {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10002);
		i.add(me.getRed());
		i.add(me.getGreen());
		i.add(me.getBlue());
		i.add(b.id);
		client.send(i);
	}
	public static void main(String[] arg) {
		String s = JOptionPane.showInputDialog("IP?");
		if(s==null)
			return;
		if(s.equals(""))
			s="localhost";
		Frame fr = new Frame(s);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		for(int a=lasers.size()-1; a>=0 && !pause; a--) {
			Laser l = lasers.get(a);
			l.tic();
			if(l.hit) 
				lasers.remove(l);
		}
		repaint();
	}
}
