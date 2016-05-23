package one;

import javax.swing.JTextField;

public class UpgradeButton extends MyButton{
  public static final int DAMAGE = 1;
  public static final int RANGE = 2;
  public static final int SPEED = 3;
  public static final int HEALTH = 4;
  public static final int MAXSHIPS = 5;
  public static final int BUILDSPEED = 6;
  public static final int ATTACKSPEED = 7;
  public static final int BASEHEALTH = 8;
  public static final int SUPERCHANCE = 9;
  
	public int stat;
	public int cost;
	public UpgradeButton(String title, int sid, char hotk) {
		super(title, sid, hotk);
	}
}
