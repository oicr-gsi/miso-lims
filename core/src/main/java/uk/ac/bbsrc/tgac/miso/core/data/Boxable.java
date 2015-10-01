package uk.ac.bbsrc.tgac.miso.core.data;


/**
 * This interface simply describes an object that can be placed into a
 * Matrix box. i.e. Sample, Library
 *
 */
public interface Boxable {

  /**
   * Set the Box of this Boxable item
   *
   * @param Box box to add
   */
  //public void setBox(Box box);

  /**
   * Return the current Box object of this Boxable item
   *
   * @return Box current box
   */
  //public Box getBox();

  /**
   * Returns whether or not the Implementor has been dumped from the Box
   *
   * @return dumped
   */
   public boolean isEmptied();
}
