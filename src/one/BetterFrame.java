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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class BetterFrame {
	boolean tutorialon;
	public static void main(String[] arg) {
		BetterFrame fr = new BetterFrame();
	}
	MenuFrame menu;
	GameFrame game;
	public BetterFrame() {
		tutorialon = true;
		menu = new MenuFrame();
		menu.setVisible(true);
//		playSound("menu.wav");
	}

	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			// The wrapper thread is unnecessary, unless it blocks on the
			// Clip finishing; see comments.
			public void run() {
				try {
					System.out.println("thread started");
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("resources/sounds/" + url).getAbsoluteFile());
					clip.open(inputStream);
					clip.start();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public class MenuFrame extends JFrame {
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
					g2d.drawImage(star, p.x, p.y, 4, 4, null);
					
				}
				for(int a=0; a<ships.size(); a++) {
					ships.get(a).draw(g2d, focus);
				}
			}
		}
		public ArrayList<Ship> ships;
		Point focus;
		Panel mypanel;
		ArrayList<Point> stars;
		public Image star;
		JTextField input;
		JButton join;
		JButton exit;
		Timer tim;
		public MenuFrame() {
			ships = new ArrayList<Ship>();
			
			ImageIcon ii = new ImageIcon("resources/images/star.gif");
			star = ii.getImage();
			ii = new ImageIcon("resources/images/icon.png");
			Image icon = ii.getImage();
			this.setIconImage(icon);
			mypanel = new Panel();
			stars = new ArrayList<Point>();
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			this.setUndecorated(true);
			this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width-200, Toolkit.getDefaultToolkit().getScreenSize().height-200);
			for(int a=0; a<40; a++) {
				Color col = Color.getHSBColor((float)(Math.random()), (float)(Math.random()*.5+.5), (float)(Math.random()*.5+.5));
				ships.add(new Ship(col, (int)(Math.random()*(getWidth()+600)-300), (int)(Math.random()*1000)+getHeight(), false, 0, 10   )    );
			}

			for(Ship s : ships) {
				s.setTarget(new Point(getWidth()/2, -400));
			}
			//			focus = new Point(getWidth()/2, getHeight()/2);
			focus = new Point(0, 0);
			this.setTitle("Space Menu");
	    	this.setLayout(null);
	    	mypanel.setLayout(null);
	        this.setLocationRelativeTo(null);
	        this.setResizable(false);
			this.setFocusable(true);
			input = new JTextField();
			input.setSize(200, 30);
			input.setLocation(this.getWidth()/2-100, this.getHeight()/2-100);
			input.setText("");
			input.setFocusable(true);
			input.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent arg0) {
				}
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER) {
						joingame(input.getText());
					}
				}
				@Override
				public void keyTyped(KeyEvent arg0) {
				}
				
			});
			join = new JButton("Join Server");
			join.setSize(200, 30);
			join.setLocation(this.getWidth()/2-100, this.getHeight()/2);
			join.setFocusable(false);
			join.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					joingame(input.getText());
				}
			});
			exit = new JButton("Exit");
			exit.setSize(200, 30);
			exit.setLocation(this.getWidth()/2-100, this.getHeight()/2+100);
			exit.setFocusable(false);
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			mypanel.setSize(this.getWidth(), this.getHeight());
			mypanel.setLocation(0, 0);
			this.add(mypanel);
			mypanel.add(input);
			input.requestFocus();
			mypanel.add(join);
			mypanel.add(exit);
			
			this.validate();
			for(int a=0; a<200; a++) {
				stars.add(new Point((int) (Math.random()*(getWidth())), (int)(Math.random()*(getHeight()))));
			}
			this.repaint();
			this.requestFocus();
			tim = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for(int a=ships.size()-1; a>=0; a--) {
						Ship s = ships.get(a);
						if(s.moveToTarget()) {
//							Ship news = new Ship(s.player, (int)(Math.random()*getWidth()  ), (int)(Math.random()*1000)+getHeight(), false, 0, 10);
//							news.setTarget(new Point(getWidth()/2, -400));
//							ships.remove(a);
//							ships.add(news);
							s.cur = new Point((int)(Math.random()*(getWidth()+600)-300), (int)(Math.random()*1000)+getHeight());
							s.setTarget(new Point(getWidth()/2, -400));
						}
					}
					repaint();
				}
			});
			tim.start();
		}
		@Override
		public void setVisible(boolean b) {
			super.setVisible(b);
			if(tim !=null) {
				if(b) {
					tim.start();
				} else {
					tim.stop();
				}
			}
		}
		public void joingame(String ip) {
			this.setVisible(false);
			if(ip.equals(""))
				ip="localhost";
//			input.setText(ip);
			game = new GameFrame(ip);
			tutorialon = false;
//			game.setVisible(true);
		}
	}

	public class GameFrame extends JFrame implements ActionListener{
		Panel mypanel;
		ArrayList<Point> stars;
		public Image star;
		public Color me;
		public Color inverse;
		public Base mybase;
		boolean gamestarted = false;
		String message = "";
		String pingtime = "";
		public long timepinged;
		
		public UpgradeButton updamage;
		public UpgradeButton upspeed;
		public UpgradeButton uprange;
		public UpgradeButton uphealth;  
		public UpgradeButton upattackcd;
		
		public UpgradeButton upbuildcd;
		public UpgradeButton upmaxships;
		public UpgradeButton upregen;
		public UpgradeButton upsuper;
		
		public SpellButton invisSpell;
		public int points;
		Point mouse;
		
		public Meteor met;
		public ArrayList<Ship> ships;
		public ArrayList<Base> bases;
		public ArrayList<Laser> lasers;
		public ArrayList<Wall> walls;
		public ArrayList<MyButton> buttons;
		public ArrayList<ExplosionGraphic> explosions;
		Client client;
		Timer t;
		boolean pause;
		boolean tutorial = true;
		Image tutor;
		long ping = 0;
		int world;
		boolean buildwallmode = false;
		boolean mousedown = false;
		final static int OCCUPIED = 100;
		final static int AVAILABLE = 101;
		public static final int MINWALLWIDTH = 15;
		public Point cornerone;
		public Point cornertwo;
		ArrayList<Integer> keyspressed;
		public int WORLDWIDTH, WORLDHEIGHT;
		public boolean superenabled;
		
		Point lookingat;
		boolean tictime;
		public final int CAMERAMOVESPEED = 25;
		
		int drawwarning;
		int baseattacked;
			
		public GameFrame(String ip) {
			tutorial = tutorialon;
			keyspressed = new ArrayList<Integer>();
			mouse = new Point(0, 0);
			lookingat = new Point(0, 0);
			ImageIcon ii = new ImageIcon("resources/images/star.gif");
			star = ii.getImage();
			ii = new ImageIcon("resources/images/meteor.png");
			Meteor.image = ii.getImage();
			ii = new ImageIcon("resources/images/tutorial.png");
			tutor = ii.getImage();
			ii = new ImageIcon("resources/images/icon.png");
			Image icon = ii.getImage();
			this.setIconImage(icon);
			stars = new ArrayList<Point>();
		
			ships = new ArrayList<Ship>();
			bases = new ArrayList<Base>();
			lasers = new ArrayList<Laser>();
			walls = new ArrayList<Wall>();
			buttons = new ArrayList<MyButton>();
      explosions = new ArrayList<ExplosionGraphic>();
			mypanel = new Panel();

      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setUndecorated(true);
      this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
      
			this.add(mypanel);
			mypanel.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			updamage = new UpgradeButton("DAMAGE", UpgradeButton.DAMAGE, '1');
			updamage.setToolTipText("Damage dealt per laser.");
			initbutton(updamage, mypanel.getWidth()-110, 70, 100, 50);
			uprange = new UpgradeButton("RANGE", UpgradeButton.RANGE, '2');
			uprange.setToolTipText("Maximum length of a laser.");
			initbutton(uprange, mypanel.getWidth()-110, 130, 100, 50);
			upspeed = new UpgradeButton("SPEED", UpgradeButton.SPEED, '3');
			upspeed.setToolTipText("Movement speed of each ship.");
			initbutton(upspeed, mypanel.getWidth()-110, 190, 100, 50);
			uphealth = new UpgradeButton("HEALTH", UpgradeButton.HEALTH, '4');
			uphealth.setToolTipText("Maximum health of each ship");
			initbutton(uphealth, mypanel.getWidth()-110, 250, 100, 50);
			upattackcd = new UpgradeButton("ATTACKSP", UpgradeButton.ATTACKSPEED, '5');
			upattackcd.setToolTipText("How frequently your base and ships shoot lasers. (Max 8)");
			initbutton(upattackcd, mypanel.getWidth()-110, 310, 100, 50);
			upmaxships = new UpgradeButton("MAXSHIPS", UpgradeButton.MAXSHIPS, '6');
			upmaxships.setToolTipText("The total number of ships your base can support.");
			initbutton(upmaxships, mypanel.getWidth()-110, 440, 100, 50);
			upbuildcd = new UpgradeButton("BUILDSP", UpgradeButton.BUILDSPEED, '7');
			upbuildcd.setToolTipText("The time it takes to build a ship. (Min 5)");
			initbutton(upbuildcd, mypanel.getWidth()-110, 500, 100, 50);
			upregen = new UpgradeButton("BASEHP", UpgradeButton.BASEHEALTH, '8');
			upregen.setToolTipText("TotalBaseHealth= 1000 + BASEHP*100   BASEHP also increases base health regeneration.");
			initbutton(upregen, mypanel.getWidth()-110, 560, 100, 50);
			upsuper = new UpgradeButton("SUPER", UpgradeButton.SUPERCHANCE, '9');
			upsuper.setToolTipText("% chance of building a SuperShip.");
			initbutton(upsuper, mypanel.getWidth()-110, 620, 100, 50);
      invisSpell = new SpellButton("INVISIBLE", SpellButton.INVIS, 'i');
      initbutton(invisSpell, 30, mypanel.getHeight() - 80, 100, 50);
			
			for(Component c : mypanel.getComponents()) {
				if(c instanceof SpellButton || c instanceof UpgradeButton) {
					buttons.add((MyButton)c);
					((MyButton)c).setFocusable(false);
				}
			}
	
			me = Color.gray;
			//this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
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
			for(int a=0; a<getWidth()/2; a++) {
				stars.add(new Point((int) (Math.random()*(getWidth()*3)-getWidth()), (int)(Math.random()*(getHeight()*3)-getHeight())));
			}
			this.repaint();
	        this.setVisible(true);
			this.requestFocus();
	
			message="Connecting to "+ip+" ...";
			client = new Client(this, ip);
			message="Failed to connect to: "+ip;
			client.start();
			t = new Timer(30, this);
			t.start();
		}
		public void setsuperenabled(boolean bl) {
			superenabled = bl;
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
			if(r.x<0 || r.x+r.width>1100 || r.y<0 ||r.y+r.height>768) {
				return true;
			}
			return false;
		}
		public void setWorldSize(int w, int h) {
			WORLDWIDTH = w;
			WORLDHEIGHT = h;
		}
		public void makeWall() {
			Rectangle dim = createRectangle(cornerone, cornertwo);
			int cost = getWallCost(dim);
			if(points>=cost && !wallcollides(new Rectangle(dim.x, dim.y, dim.width, dim.height))) {
				Wall wally = new Wall(dim);
				walls.add(wally);
				points-=getWallCost(dim);
				send(wally);
			}
			cornerone = null;
		}
		public void baseattacked() {
			baseattacked = 5;
		}
		public boolean checkAndHandleBaseAttacked(int newHealth) {
		  if( mybase != null) {
  		  if( newHealth < mybase.health ) {
  		    baseattacked();
  		    return true;
  		  }
		  }
		  return false;
		}
		public void send(Wall w) {
			ArrayList<Integer> i = new ArrayList<Integer>();
			i.add(Connection.MAKEWALL);
			i.add(me.getRed());
			i.add(me.getGreen());
			i.add(me.getBlue());
			i.add(w.x);
			i.add(w.y);
			i.add(w.w);
			i.add(w.h);
			client.send(i);
		}
		public boolean wallcollides(Rectangle r) {
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
			return false;
		}
		public int getWallCost(Rectangle r) {
			return r.width*r.height/60;
		}
		public Rectangle createRectangle(Point one, Point two) {
		  int topleftx = Math.min(one.x, two.x);
      int toplefty = Math.min(one.y, two.y);
      int width = Math.abs(one.x - two.x);
      int height = Math.abs(one.y - two.y);
      return new Rectangle(topleftx, toplefty, width, height);
      /*
		  other = new Point( other.x + lookingat.x, other.y + lookingat.y);
			Point topleft = new Point(0, 0);
			Point botright = new Point(0, 0);
			if(cornerone.x<other.x) {
				topleft.x = cornerone.x;
				botright.x = other.x;
				if(topleft.x-botright.x>-MINWALLWIDTH) {
					botright.x = topleft.x+MINWALLWIDTH;
				}
			} else {
				topleft.x = other.x;
				botright.x = cornerone.x;
				if(topleft.x-botright.x>-MINWALLWIDTH) {
					topleft.x = botright.x-MINWALLWIDTH;
				}
			}
			if(cornerone.y<other.y){
				topleft.y = cornerone.y;
				botright.y = other.y;
				if(topleft.y-botright.y>-MINWALLWIDTH) {
					botright.y = topleft.y+MINWALLWIDTH;
				}
			} else {
				botright.y = cornerone.y;
				topleft.y = other.y;
				if(topleft.y-botright.y>-MINWALLWIDTH) {
					topleft.y = botright.y-MINWALLWIDTH;
				}
			}
			
			int width = botright.x-topleft.x;
			if(width<MINWALLWIDTH)
				width=MINWALLWIDTH;
			int height = botright.y-topleft.y;
			if(height<MINWALLWIDTH)
				height = MINWALLWIDTH;
					
			return new Rectangle(topleft.x-lookingat.x, topleft.y-lookingat.y, width, height);
			*/
		}
		public boolean isMyBase( Base b ) {
		  if( b.player.equals(me) ) {
		    return true;
		  }
		  return false;
		}
		public void readBase(Base b) {
			for(int a=0; a<bases.size(); a++) {
				Base base = bases.get(a);
				if(base.id==b.id){
					bases.remove(a);
				}
			}
      if( isMyBase(b) ) {
        checkAndHandleBaseAttacked(b.health);
        mybase = b;
      }
			bases.add(b);
		}
		public void removeBase(int id) {
			for(int a=0; a<bases.size(); a++) {
				Base base = bases.get(a);
				if(base.id==id){
					Base removed = bases.remove(a);
          explosions.add(new ExplosionGraphic(removed.cur, removed.width));
					if( isMyBase(removed) ) {
					  //mybase = null;
					}
				}
			}
		}
		public void removeShip(int id) {
			for(int a = 0; a<ships.size(); a++) {
				Ship s = ships.get(a);
				if(s.id==id) {
					ships.remove(s);
					explosions.add(new ExplosionGraphic(s.cur, s.width));
				}
			}
		}
		public void readShip(Ship ship) {
			for(int a = 0; a<ships.size(); a++) {
				Ship s = ships.get(a);
				if(s.id==ship.id) {
					ships.remove(s);
				}
			}
			ships.add(ship);
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
	        	if(drawwarning>=5)
	        		g2d.setColor(Color.red);
	        	else
	        		g2d.setColor(Color.black);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.setColor(Color.black);
				g2d.fillRect(0, 30, getWidth(), getHeight()-60);
				
				for(Point p : stars) {
					int drawx = p.x-lookingat.x;
					int drawy = p.y-lookingat.y;
					if(drawx>-10 && drawy>-10 && drawx<getWidth() && drawy<getHeight());
						g2d.drawImage(star, p.x-lookingat.x, p.y-lookingat.y, 8, 8, null);
				}
				g.setColor(Color.white);
				g.drawRect(0, 0, 10, 10);
				if(met!=null)
					met.draw(g2d, lookingat);
				for(int a=0; a<ships.size(); a++) {
					Ship s=ships.get(a);
					if( s.player.equals(me)) {
					  s.drawMyShip(g2d, lookingat);
					}
					else {
					  s.draw(g2d, lookingat);
					}
				}
				for(int a=0; a<bases.size(); a++) {
					Base b = bases.get(a);
					b.draw(g2d, lookingat);
				}
        for( int a = 0; a < explosions.size(); a++ ) {
          ExplosionGraphic e = explosions.get(a);
          g.setColor(Color.orange);
          g.fillOval(e.x-e.radius/2-lookingat.x, e.y-e.radius/2-lookingat.y, e.radius, e.radius);
        }
				for(int a=0; a<walls.size(); a++) {
					Wall w = walls.get(a);
					w.draw(g2d, lookingat);
				}
				for(int a=0; a<lasers.size(); a++) {
					Laser l = lasers.get(a);
					l.draw(g2d, lookingat);
				}
				if(buildwallmode) {
					g2d.setColor(Color.gray);
					for(int a=0; a<getWidth()-200; a+=MINWALLWIDTH) {
						g2d.drawLine(a, 0, a, getHeight());
					}
					for(int b=0; b<getHeight(); b+=MINWALLWIDTH) {
						g2d.drawRect(0, b, getWidth()-200, b);
					}
					if(cornerone!=null) {
						g2d.setColor(Color.white);
						Point mouseToWorld = new Point(mouse);
						mouseToWorld.x += lookingat.x;
            mouseToWorld.y += lookingat.y;
						Rectangle dimensionsInWorld = createRectangle(cornerone, mouseToWorld);
						Rectangle dimensionsOnScreen = new Rectangle(dimensionsInWorld.x-lookingat.x, dimensionsInWorld.y - lookingat.y, dimensionsInWorld.width, dimensionsInWorld.height);//TODO Stuff
						//Rectangle dim = getWallDim(mouse);
						g2d.draw(dimensionsOnScreen);
						g2d.setColor(me);
						g2d.setFont(new Font("Arial", Font.PLAIN, 20));
						FontMetrics fm = g2d.getFontMetrics();
						int cost = getWallCost(dimensionsInWorld);
						g2d.drawString(cost+"", dimensionsOnScreen.x+dimensionsOnScreen.width/2-fm.stringWidth(cost+"")/2, dimensionsOnScreen.y+dimensionsOnScreen.height/2+10);
						
					}
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
				for( MyButton button : buttons) {
				  draw(g2d, button);
				}
//				for(Component c : this.getComponents()) {
//					if(c instanceof UpgradeButton) {
//						draw(g2d, (UpgradeButton)c);
//					}
//				}
				g.setColor(Color.white);
				g.fillRect(getWidth()-20, 0, 21, 21);
				g.setColor(Color.black);
				g.drawRect(getWidth()-20, 0, 20, 20);
				g.drawLine(getWidth()-18, 2, getWidth()-2, 18);
				g.drawLine(getWidth()-18, 1, getWidth()-2, 17);
				g.drawLine(getWidth()-18, 18, getWidth()-2, 2);
				g.drawLine(getWidth()-18, 17, getWidth()-2, 1);
				g.setFont(new Font("Arial", Font.PLAIN, 50));
				g.setColor(Color.white);
				g.drawString(message, 20, getHeight()/2);
				g.drawRect(-lookingat.x, -lookingat.y, WORLDWIDTH, WORLDHEIGHT);
				if(tutorial) {
					g.drawImage(tutor, 0, 0, this.getWidth(), this.getHeight(), null);
				}
				g.setFont(new Font("Arial", Font.PLAIN, 20));
				g.setColor(Color.white);
				g.drawString(pingtime, 2, 22);
				g.setFont(new Font("Arial", Font.PLAIN, 50));
				if(tutorial)
					g.drawString("Press Space", getWidth()/2-200, getHeight()-50);
			}
		}
		public ArrayList<Integer> convertmovetostring(MouseEvent e) {
			ArrayList<Integer> i = new ArrayList<Integer>();
			i.add(10001);
			i.add(me.getRed());
			i.add(me.getGreen());
			i.add(me.getBlue());
			i.add(e.getX()+lookingat.x);
			i.add(e.getY()+lookingat.y);
			return i;
		}
		public void draw(Graphics2D g, MyButton b) {/// draws a MyButton
		  
			g.setColor(me);
			g.fillRect(b.getLocation().x, b.getLocation().y,b.getSize().width, b.getSize().height);
			g.setColor(Color.white);
			g.fillRect(b.getLocation().x-2, b.getLocation().y+20, 30, 10);

			g.setColor(Color.white);
			if(!superenabled && b==upsuper)
				g.setColor(Color.GRAY);
			g.fillRect(b.getLocation().x+10, b.getLocation().y+10,b.getSize().width-20, b.getSize().height-20);
	//		g.setFont(new Font("Helvetica", Font.BOLD, 20));//new Font("Arial", Font.BOLD, 12));
	//		g.setColor(me);
	//		g.drawString(b.getActionCommand(), b.getLocation().x+12, b.getLocation().y+30);
			if( b instanceof UpgradeButton ) {
			  UpgradeButton button = (UpgradeButton)b;
  			g.setColor(me);
  			g.setFont(new Font("Arial", Font.BOLD, 40));
  	    FontMetrics metr = this.getFontMetrics(g.getFont());
  			g.drawString(""+button.stat, b.getLocation().x-metr.stringWidth(""+button.stat), b.getLocation().y+40);
  			g.setColor(Color.white);
  			g.setFont(new Font("Arial", Font.BOLD, 20));
  			g.drawString(""+button.cost, b.getLocation().x-65, b.getLocation().y+35);
  			g.setColor(Color.black);
  			g.setFont(new Font("Arial", Font.PLAIN, 14));
  			g.drawString(""+b.hotkey, b.getLocation().x+1, b.getLocation().y+45);
			}
			else if( b instanceof SpellButton && mybase != null ) {
        g.setColor(Color.black);
        //TODO temp jankness for now, later fix to srp dry
        
        double ratio = mybase.invisCooldownRatio();
        if( ratio < .22 && ratio > 0 ) 
          ratio = .22;
//        System.out.println(ratio);
        int xoffset = (int) ((1-ratio) * b.getSize().width/2);
        int yoffset = (int) ((1-ratio) * b.getSize().height/2);
        
        g.fillRect(b.getLocation().x+xoffset, b.getLocation().y+yoffset,(int)(b.getSize().width*ratio)+1, (int)(b.getSize().height*ratio)+1);
        
        
//        String name = b.getText();
//        g.drawString(name, b.getLocation().x-metr.stringWidth(name), b.getLocation().y+40);
			}
		}
		public void pinged(int time) {
			pingtime = "ping: "+time;
		}
		public void leavegame() {
			setVisible(false);
			menu.setVisible(true);
		}
		public class Listener implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
	
			@Override
			public void mouseExited(MouseEvent e) {
			}
	
			@Override
			public void mousePressed(MouseEvent e) {
//			  System.err.println("Pressed Mouse");
				mousedown=true;
				if(mousedown && buildwallmode) {
					//cornerone = e.getPoint();
          cornerone = new Point( e.getX() + lookingat.x, e.getY() + lookingat.y);
				}
			}
	
			@Override
			public void mouseReleased(MouseEvent e) {
				mousedown=false;
				if(buildwallmode) {
					makeWall();
				} else {
					if(e.getX()>getWidth()-20 && e.getY()<20) {
						client.stop();
						leavegame();
//						System.exit(0);
						return;
					} else if(e.getX()<10 && e.getY()<10) {
						timepinged = System.currentTimeMillis();
						ArrayList<Integer> tosend = new ArrayList<Integer>();
						tosend.add(Client.PING);
						tosend.add(me.getRed());
						tosend.add(me.getGreen());
						tosend.add(me.getBlue());
						client.send(tosend);
					} else {
						client.send(convertmovetostring(e));
					}
				}
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				mouse = e.getPoint();
				if(mousedown && buildwallmode) {

					cornertwo = new Point( e.getX() + lookingat.x, e.getY() + lookingat.y);
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				mouse = e.getPoint();
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() instanceof MyButton) {
					buttonpressed((MyButton)e.getSource());
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_W) {
					if(gamestarted && World.BUILD_WALL_MODE_ENABLED )   //uncomment this to reenable walls building
						buildwallmode = true;
				}
				if(!keyspressed.contains(e.getKeyCode())) {
					keyspressed.add(e.getKeyCode());
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				for(MyButton b : buttons) {
					if(e.getKeyChar()==b.hotkey) {
						buttonpressed(b);
//						if( b == invisSpell) {
//						  System.out.println("INVIS SPELL PRESSED at client");
//						}
					}
				}
				if(e.getKeyCode()==KeyEvent.VK_SPACE) {
					tutorial=false;
				}
				if(e.getKeyCode()==KeyEvent.VK_R) {
	//				sendready();
				}
				if(e.getKeyCode()==KeyEvent.VK_W) {
					buildwallmode = false;
					mousedown=false;
					cornerone = null;
				}
				if(keyspressed.contains(e.getKeyCode())) {
					keyspressed.remove(new Integer(e.getKeyCode()));
				}
				
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				
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
		@Override
		public void actionPerformed(ActionEvent arg0) {
      try {
  			for(int a=0; a<ships.size(); a++) {
			    Ship s=ships.get(a);
  				if(s.dead)
  					ships.remove(a--);
  			}
      } catch (IndexOutOfBoundsException i) {
        
      }
//			if(tictime) {
//				tictime=!tictime;
				for(int a=lasers.size()-1; a>=0 && !pause; a--) {
					Laser l = lasers.get(a);
					l.tic();
					if(l.hit) 
						lasers.remove(l);
				}
				for( int a = explosions.size() - 1; a >= 0; a-- ) {
				  if( explosions.get(a).widen() ) {
				    explosions.remove(a);
				  }
				}
				if(baseattacked>0) {
					drawwarning--;
					if(drawwarning<0) {
						drawwarning = 10;
						baseattacked--;
					}
				} else {
					drawwarning = -1;
				}
//			}
			
			if(keyspressed.contains(KeyEvent.VK_DOWN) || mouse.y>=getHeight()-10) {
			  if( lookingat.y < WORLDHEIGHT-500)
			    lookingat.y+=CAMERAMOVESPEED;
			}
			if(keyspressed.contains(KeyEvent.VK_UP) || mouse.y<=10) {
        if( lookingat.y > -500)
          lookingat.y-=CAMERAMOVESPEED ;
			}
			if(keyspressed.contains(KeyEvent.VK_RIGHT) || mouse.x>=getWidth()-10) {
        if( lookingat.x < WORLDWIDTH-500)
          lookingat.x+=CAMERAMOVESPEED ;
			}
			if(keyspressed.contains(KeyEvent.VK_LEFT) || mouse.x<=10) {
        if( lookingat.x > -500)
          lookingat.x-=CAMERAMOVESPEED ;
			}
			repaint();
		}
	}
}
