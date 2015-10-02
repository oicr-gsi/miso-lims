package uk.ac.bbsrc.tgac.miso.core.data;

/*
 * Skeleton implementation of a Boxable item
 */
public abstract class AbstractBoxable implements Boxable {
  private boolean emptied;
  private double volume;
  private Box box;

  @Override
  public void setBox(Box box) {
    this.box = box;
  }

  @Override
  public Box getBox() {
    return box;
  }

  @Override
  public void setVolume(double volume) {
    this.volume = volume;
  }

  @Override
  public double getVolume() {
    return volume;
  }

  @Override
  public boolean getEmptied() {
    return emptied;
  }

  @Override
  public void setEmptied(boolean emptied) {
    if (emptied) volume = 0;
    this.emptied = emptied;
  }
}
