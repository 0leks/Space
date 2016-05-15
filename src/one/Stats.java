package one;

public class Stats {
  public int REGEN;
  public int MAXSHIPS;
  public int BUILDCD;
  public int RANGE;
  public int HEALTH;
  public int DAMAGE;
  public int MOVESPEED;
  public int ATTACKCD;
  public int CHANCESUPER;
  

  public int supercost() {
    return (CHANCESUPER-3)/3;
  }
  public int buildcdcost() {
    return (16-BUILDCD)*2;
  }
  private static final int RANGEINC = 5;
  public int rangecost() {
    return (RANGE-50+RANGEINC)/RANGEINC;
  }
  public int attackcost() {
    return ATTACKCD*2;
  }
  public int speedcost() {
    return MOVESPEED-9;
  }
  public int damagecost() {
    if(DAMAGE<9) {
      return DAMAGE-2;
    } else if(DAMAGE<21) {
      return DAMAGE/3+4;
    } else if(DAMAGE<40) {
      return DAMAGE/5+7;
    } else if(DAMAGE<80) {
      return DAMAGE/10+11;
    } else {
      return DAMAGE/20+16;
    }
  }
  public int shipscost() {
    if(MAXSHIPS<20) {
      return MAXSHIPS-14;
    } else if(MAXSHIPS<30) {
      return MAXSHIPS/2-4;
    } else if(MAXSHIPS<40) {
      return MAXSHIPS/3+1;
    } else if(MAXSHIPS<50) {
      return MAXSHIPS/4+4;
    } else if(MAXSHIPS<100) {
      return MAXSHIPS/10+11;
    } else {
      return MAXSHIPS/50+19;
    }
  }
  public int healthcost() {
    return HEALTH/5-1;
  }
  public int regencost() {
    return REGEN;
  }
}
