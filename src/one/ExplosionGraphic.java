package one;

import java.awt.Point;

public class ExplosionGraphic {
  public int x, y, radius, maxsize, d;
  public ExplosionGraphic(int sx, int sy, int ms) {
    x = sx;
    y = sy;
    radius = 0;
    maxsize = ms;
    d = maxsize/15;
  }
  public ExplosionGraphic(Point position, int ms) {
    x = position.x;
    y = position.y;
    radius = 0;
    maxsize = ms;
    d = maxsize/15;
    if( d <= 0 ) d = 1;
  }
  public boolean widen() {
    radius+=d;
    if(radius>maxsize) {
      radius = maxsize;
      d = -d;
    }
    if(radius<0) {
      return true;
    }
    return false;
  }
}
