package one;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class World implements ActionListener{
	private ArrayList<Ship> ships;
	private ArrayList<Base> bases;
	private ArrayList<Laser> lasers;
	private ArrayList<Wall> walls;
	private ArrayList<String> messages;
	ArrayList<Color> available;
	ArrayList<Color> disconnected;
	
	public Meteor met;
	Timer t;
	int con = 0;
	boolean gamestarted = false;
	boolean superenabled = true;
	boolean meteorenabled = true;
	int timeformeteor = 0;
	final int METEORCD = 200;
	Server server;
	ArrayList<Connection> connections;
	MFrame mframe;
	TFrame info;
	boolean withframe;
	boolean gamepaused;
	boolean randomspawn;
	final int startingpoints;
	public World(boolean withfram) {
		withframe=withfram;
		mframe = new MFrame();
		info = new TFrame();
		mframe.setTitle("Space Spectating Client");
		info.setTitle("Space Console  (Creating Server)");
		startingpoints = (int)(Math.random()*20+10);
		connections = new ArrayList<Connection>();
		ships = new ArrayList<Ship>();
		bases = new ArrayList<Base>();
		lasers = new ArrayList<Laser>();
		walls  = new ArrayList<Wall>();
		messages = new ArrayList<String>();
		available = new ArrayList<Color>();
		disconnected = new ArrayList<Color>();
		available.add(Color.blue);
		available.add(Color.red);
		available.add(Color.green);
		available.add(Color.MAGENTA);
		available.add(Color.orange);
		available.add(Color.pink);
		available.add(Color.GRAY);
		met = new Meteor(0, 0, 0);
		ImageIcon ii = new ImageIcon("images/meteor.png");
		Meteor.image = ii.getImage();
		Base.world = this;
		Ship.world = this;
		Laser.world = this;
		server = new Server(this);
		server.start();
		info.setTitle("Space Console  (Server Created)");
		t = new Timer(100, this);
		t.start();
//		Ship ship = new Ship(Color.blue, 300, 300, 999999, 0, 9999, 1, 1, 10, 0);
//		ships.add(ship);
//		ship = new Ship(Color.red, 400, 300, 999999, 0, 9999, 1, 4, 10, 0);
//		ships.add(ship);
//		ship = new Ship(Color.green, 400, 400, 999999, 0, 9999, 1, 8, 10, 0);
//		ships.add(ship);
	}
	public boolean gamestarted() {
		return gamestarted;
	}
	public boolean hasdisconnect() {
		return disconnected.size()>0;
	}
	public void removeConnection(one.Connection connection) {
		if(connections.remove(connection)) {
			playerDisconnected(connection.player);
			
		}
	}
	public boolean tooclose(int x, int y) {
		for(Base b : bases) {
			int dist = Math.abs(b.cur.x-x)+Math.abs(b.cur.y-y);
			if(dist<=200)
				return true;
		}
		return false;
	}
	public void ping(Color c) {
		for(int a=0; a<connections.size(); a++) {
			Connection con = connections.get(a);
			if(con.player.equals(c)) {
				ArrayList<Integer> i = new ArrayList<Integer>();
				i.add(10010);
				i.add(1);
				con.send(i);
			}
		}
	}
	public boolean colortooclose(Color c) {
		int MIN = 150;
		for(Base b : bases) {
			Color p = b.player;
			if(Math.abs(p.getRed()-c.getRed())+Math.abs(p.getGreen()-c.getGreen())+Math.abs(p.getBlue()-c.getBlue())<MIN) {
				return true;
			}
		}
		return false;
	}
	public void addConnection(Connection connection) {
		connections.add(connection);
		connection.start();
		System.out.println("Adding connection");
		Color co;
//		int rand = (int)(Math.random()*3);
		
		if(disconnected.size()>0)
			co = disconnected.remove(0);
		else {
			do {
				co = Color.getHSBColor((float)(Math.random()), (float)(Math.random()*.5+.5), (float)(Math.random()*.5+.5));
			} while(colortooclose(co));
		}
//			co = new Color((int)(Math.random()*150+50), (int)(Math.random()*150+50), (int)(Math.random()*150+50));
		Base b = this.getbase(co);
		if(b==null) {
//			for(int a=0; a<8; a++) {
			if(randomspawn) {
				int x=0;
				int y=0;
				int w=(int)(Math.random()*10+40);
				do {
					x = (int)(Math.random()*900+50);
					y = (int)(Math.random()*700+50);
				} while (tooclose(x, y));
				b = new Base(co, x, y, w, startingpoints);
			} else {
				if(con==0) {
					b = new Base(co, 200, 100, 40, startingpoints);
	//				walls.add(new Wall(350, 0, 10, 150));
				}
				if(con==1) {
					b = new Base(co, 800, 100, 40, startingpoints);
	//				walls.add(new Wall(750, 250, 250, 10));
				}
				if(con==2) {
					b = new Base(co, 800, 650, 40, startingpoints);
	//				walls.add(new Wall(650, 550, 10, 150));
				}
				if(con==3) {
					b = new Base(co, 200, 650, 40, startingpoints);
	//				walls.add(new Wall(0, 500, 150, 10));
				}
				if(con==4) {
					b = new Base(co, 500, 50, 40, startingpoints);
	//				walls.add(new Wall(600, 0, 10, 150));
	//				walls.add(new Wall(350, 0, 10, 150));
				}
				if(con==5) {
					b = new Base(co, 100, 350, 40, startingpoints);
	//				walls.add(new Wall(0, 200, 150, 10));
				}
				if(con==6) {
					b = new Base(co, 900, 360, 40, startingpoints);
				}
				if(con==7){
					b = new Base(co, 500, 700, 40, startingpoints);
				}
				if(con>7){
					return;
				}
			}
			bases.add(b);
			con++;
//			}
		}
		connection.setPlayer(b.player);
	}
	public boolean collides(Rectangle r, Ship ship) {
		for(Ship s : ships) {
			if(s!=ship && s.collides(r)) {
				return true;
			}
		}
		for(Base b : bases) {
			if(b.collides(r)) {
				return true;
			}
		}
		for(Wall w : walls) {
			if(w.collides(r)) {
				return true;
			}
		}
		if(r.x<0 || r.x+r.width>1200 || r.y<0 ||r.y+r.height>768) {
			return true;
		}
		return false;
	}
	public boolean collides(Rectangle r) {
		for(Ship s : ships) {
			if(s.collides(r)) {
				return true;
			}
		}
		for(Base b : bases) {
			if(b.collides(r)) {
				return true;
			}
		}
		if(r.x<0 || r.x+r.width>1200 || r.y<0 ||r.y+r.height>768) {
			return true;
		}
		return false;
	}
	public void playerDisconnected(Color c) {
		System.out.println("player "+c+" disconnected");
		for(int a=0; a<bases.size(); a++) {
			Base b = bases.get(a);
			if(b.player.equals(c)) {
				disconnected.add(0, c);
				return;
			}
		}
	}
	public void removeBase(Base b) {
		bases.remove(b);
	}
	public void removeShip(Ship s) {
		for(Base b : bases) {
			if(b.player.equals(s.player)) {
				b.numships--;
			}
		}
		ships.remove(s);
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10007);
		i.add(1);
		i.addAll(s.convert());
		for(int a=0; a<connections.size(); a++) {
			Connection c = connections.get(a);
			c.send(i);
		}
	}
	public void tic() {
		if(gamestarted && !gamepaused) {
			for(int a=lasers.size()-1; a>=0; a--) {
				Laser l = lasers.get(a);
				l.tic();
				if(l.hit) 
					lasers.remove(l);
			}
			for(int a=bases.size()-1; a>=0; a--) {
				Base b = bases.get(a);
				b.tic();
			}
			for(int a=ships.size()-1; a>=0; a--) {
				Ship s = ships.get(a);
				s.tic();
			}
			if(meteorenabled) {
				if(timeformeteor++>METEORCD) {
					if((met!=null && met.disabled()) || met==null) {
						met = new Meteor(new Point((int)(Math.random()*1200-100), (int)(Math.random()*1000-100)), 20, new Point((int)(Math.random()*1200-100), (int)(Math.random()*1000-100)));
						timeformeteor=0;
					}
				}
				met.tic();
			}
		}
		for(int a=0; a<connections.size(); a++) {
			Connection c = connections.get(a);
			c.send(convertships());
			c.send(convertbases());
			sendStats(c);
			c.send(convertMeteor());
			c.send(convertwalls());
		}
	}
	public void pause() {
		ArrayList<Integer> send = new ArrayList<Integer>();
		send.add(10009);
		send.add(1);
		if(gamepaused) {
			System.out.println("pausing");
			send.add(1);
		} else {
			System.out.println("unpausing");
			send.add(0);
		}
		for(int a=0; a<connections.size(); a++) {
			Connection c = connections.get(a);
			c.send(send);
		}
	}
	public void addLaser(Laser l) {
		lasers.add(l);
		sendlaser(l);
	}
	public void sendlaser(Laser l) {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10003);
		i.add(1);
		i.addAll(l.convert());
		for(int a=0; a<connections.size(); a++) {
			Connection c = connections.get(a);
			c.send(i);
		}
	}
	public Base getbaseinrange(Ship ship) {
		for(Base s : bases) {
			if(!ship.player.equals(s.player) && ship.distance(s)<ship.RANGE) {
				return s;
			}
		}
		return null;
	}
	public Ship getinrange(Ship ship) {
		for(Ship s : ships) {
			if(!ship.player.equals(s.player) && !s.dead && s.distance(ship)<ship.RANGE) {
				return s;
			}
		}
		return null;
	}
	public Ship getinrange(Base ship) {
		for(Ship s : ships) {
			if(!ship.player.equals(s.player) && s.distance(ship)<ship.RANGE*3/2) {
				return s;
			}
		}
		return null;
	}
	public int getNumShips(Color c) {
		int num=0;
		for(Ship s : ships) {
			if(s.player.equals(c)) {
				num++;
			}
		}
		return num;
	}
	public boolean addShip(Ship s) {
		ships.add(s);
		return true;
	}
	public boolean addBase(Base b) {
		bases.add(b);
		return true;
	}
	public void sendStats(Connection c) {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10004);
		i.add(1);
		Base b = this.getbase(c.player);
		if(b==null)
			return;
		i.addAll(b.convertstats());
		c.send(i);
	}
	public ArrayList<Integer> convertwalls() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10008);
		i.add(walls.size());
		for(Wall w : walls) {
			i.addAll(w.convert());
		}
		return i;
	}
	public ArrayList<Integer> convertbases() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10001);
		i.add(bases.size());
		for(Base b : bases) {
			i.addAll(b.convert());
		}
		return i;
	}
	public ArrayList<Integer> convertships() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10002);
		i.add(ships.size());
		for(Ship s : ships) {
			i.addAll(s.convert());
		}
		return i;
	}
	public ArrayList<Integer> convertMeteor() {
		ArrayList<Integer> i = new ArrayList<Integer>();
		i.add(10006);
		i.add(1);
		i.addAll(met.convert());
		return i;
	}
	public Base getbase(Color c) {
		for(Base b : bases) {
			if(b.player.equals(c))
				return b;
		}
		return null;
	}
	public void playerclicking(Color which, Point where) {
		for(Ship s : ships) {
			if(s.player.equals(which)) {
				s.setTarget(where);
			}
		}
	}
	public void gotbasekill(Color c) {
		for(Base b : bases) {
			if(b.player.equals(c)) {
				b.addpoints(30);
			}
		}
	}
	public void gotshipkill(Color c) {
		for(Base b : bases) {
			if(b.player.equals(c)) {
				b.addpoints(1);
			}
		}
	}
	public void gotsuperkill(Color c) {
		for(Base b : bases) {
			if(b.player.equals(c)) {
				b.addpoints(3);
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		tic();
		if(withframe)
			mframe.repaint();
	}
	public static void main(String[] a) {
		new World(true);
	}
	public class MFrame extends JFrame implements MouseListener{
		Point pressed;
		public MFrame() {
			int width = Toolkit.getDefaultToolkit().getScreenSize().width;
			if(width>1000)
				width=1000;
			int height = Toolkit.getDefaultToolkit().getScreenSize().height;
			if(height>800)
				height=800;
			this.setSize(width, height);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle("Space");
//	    	this.setLayout(null);
	        this.setLocationRelativeTo(null);
	        this.addMouseListener(this);
	        this.setResizable(true);
			this.setFocusable(true);
			Panel p = new Panel();
//			p.setBounds(0, 0, this.getWidth(), this.getHeight());
			this.add(p, BorderLayout.CENTER);
			this.validate();
			this.repaint();
	        this.setVisible(withframe);
			this.requestFocus();
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {	
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {	
		}
		@Override
		public void mouseExited(MouseEvent arg0) {	
		}
		@Override
		public void mousePressed(MouseEvent e) {
			pressed = e.getPoint();
		}
		@Override
		public void mouseReleased(MouseEvent e) {	
			if(met!=null && met.disabled())
				met = new Meteor(pressed, 10, e.getPoint());
		}
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
			met.draw(g2d);
			for(Ship s:ships) {
				s.draw(g2d);
			}
			for(Base b : bases) {
				b.draw(g2d);
			}
			for(Laser l : lasers) {
				l.draw(g2d);
			}
			g.setFont(new Font("Arial", 50, 70));
			if(!gamestarted) {
				g.drawString("Waiting for server to start game", 50, getHeight()/2);
			}
			if(gamepaused) {
				g.drawString("GAME PAUSED", 50, getHeight()/2+50);
			}
		}
	}
	public class TFrame extends JFrame implements KeyListener, ActionListener{
		JButton start;
		JButton meteor;
		JButton supers;
		JButton pause;
		JButton random;
		public TFrame() {
			start = new JButton("Start Game");
			start.addActionListener(this);
			start.setVisible(true);
			start.setSize(100, 50);
			start.setLocation(10, 10);
			this.add(start);
			pause = new JButton("Pause");
			pause.addActionListener(this);
			pause.setVisible(true);
			pause.setSize(100, 50);
			pause.setLocation(120, 10);
//			this.add(pause);
			meteor = new JButton("Meteor On");
			meteor.addActionListener(this);
			meteor.setVisible(true);
			meteor.setSize(100, 50);
			meteor.setLocation(120, 10);
			this.add(meteor);
			supers = new JButton("Super On");
			supers.addActionListener(this);
			supers.setVisible(true);
			supers.setSize(100, 50);
			supers.setLocation(230, 10);
			this.add(supers);
			random = new JButton("RandomOff");
			random.addActionListener(this);
			random.setVisible(true);
			random.setSize(100, 50);
			random.setLocation(340, 10);
			this.add(random);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle("Space Console");
	    	this.setLayout(null);
	    	this.setSize(780, 100);
	        this.setLocationRelativeTo(null);
	        this.setResizable(false);
			this.setFocusable(true);
			this.addKeyListener(this);
			this.validate();
			this.repaint();
	        this.setVisible(withframe);
			this.requestFocus();
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_SPACE) {
				gamestarted = true;
				info.setTitle("Space Console (Game Started)");
			}
			if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
				System.exit(0);
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==start) {
				this.remove(supers);
				this.remove(meteor);
				this.remove(start);
				this.remove(random);
				pause.setLocation(10, 10);
				this.add(pause);
				gamestarted = true;
				info.setTitle("Space Console  (Game Started)");
			}
			if(e.getSource()==meteor) {
				meteorenabled=!meteorenabled;
				if(meteorenabled) {
					meteor.setText("Meteor On");
				} else {
					if(met!=null)
						met.disable();
					meteor.setText("Meteor Off");
				}
			}
			if(e.getSource()==supers) {
				superenabled=!superenabled;
				if(superenabled) {
					supers.setText("Super On");
				} else {
					supers.setText("Super Off");
				}
			}
			if(e.getSource()==pause) {
				gamepaused=!gamepaused;
				if(gamepaused) {
					pause.setText("Resume");
					info.setTitle("Space Console  (Game Paused)");
				} else {
					pause.setText("Pause");
					info.setTitle("Space Console  (Game Started)");
				}
				pause();	
			}
			if(e.getSource()==random) {
				randomspawn=!randomspawn;
				if(randomspawn) {
					random.setText("Random On");
				} else {
					random.setText("RandomOff");
				}
			}
			this.repaint();
		}
	}
}
