package one;

import java.io.Serializable;

public class WorldInfo implements Serializable {
  public int width, height;
  public boolean superenabled;
  public boolean paused;
  public WorldInfo(int width, int height, boolean superenabled) {
    this.width = width;
    this.height = height;
    this.superenabled = superenabled;
    this.paused = false;
  }
}
