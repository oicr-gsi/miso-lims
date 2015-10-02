package uk.ac.bbsrc.tgac.miso.core.data;

/**
 * This interface simply describes an object that can be placed into a
 * box. i.e. Sample, Library
 *
 */
public interface Boxable {
  /**
   * Set the Box of this Boxable item
   *
   * @param Box box to add
   */
  public void setBox(Box box);

  /**
   * Return the current Box object of this Boxable item
   *
   * @return Box current box
   */
  public Box getBox();

  /**
   * Sets the 'emptied' attribute for the Implementor
   *
   * @param boolean emptied
   */
   public void setEmptied(boolean emptied);

  /**
   * Returns whether or not the Implementor has been emptied
   *
   * @return emptied
   */
   public boolean getEmptied();

   /**
   * Returns the volume of the Implementor
   *
   * @return volume
   */
   public double getVolume();

   /**
   * Sets the volume of the Implementor
   *
   * @param double volume
   */
   public void setVolume(double volume);
}
