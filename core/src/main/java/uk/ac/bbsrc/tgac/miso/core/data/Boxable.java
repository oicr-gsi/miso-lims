package uk.ac.bbsrc.tgac.miso.core.data;

/**
 * This interface simply describes an object that can be placed into a
 * box. i.e. Sample, Library
 *
 */
public interface Boxable extends Nameable, Locatable {
  /**
   * Set the BoxId of this Boxable item
   *
   * @param Box box to add
   */
  public void setBoxId(Long boxId);

  /**
   * Return the current BoxId of this Boxable item
   *
   * @return Box current box
   */
  public Long getBoxId();

  /**
   * Sets the 'emptied' attribute for the Implementor
   *
   * @param boolean emptied
   */
   public void setEmpty(boolean emptied);

  /**
   * Returns whether or not the Implementor has been emptied
   *
   * @return emptied
   */
   public boolean isEmpty();

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
