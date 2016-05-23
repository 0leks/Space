package one;

import java.awt.Point;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTextField;

public class MyButton extends JButton {

  public int id;
  
  public char hotkey;
  public JTextField tooltip;

  public MyButton(String title, int sid, char hotk) {
    super(title);
    id = sid;
    hotkey=hotk;
    tooltip = new JTextField();
    tooltip.setText("Hello");
  }
//
//  public MyButton(Icon arg0) {
//    super(arg0);
//  }
//
//  public MyButton(String arg0) {
//    super(arg0);
//  }
//
//  public MyButton(Action arg0) {
//    super(arg0);
//  }
//
//  public MyButton(String arg0, Icon arg1) {
//    super(arg0, arg1);
//  }

  public boolean isMouseOver(Point mouse) {
  	return mouse.x>this.getX() && mouse.x<this.getX()+this.getWidth() && mouse.y>this.getY() && mouse.y<this.getY()+this.getHeight();
  }

}