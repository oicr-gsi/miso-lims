package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Map;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.data.BoxType;
/**
 * This interface describes a Box which is a n by m container which contains tubes
 * which contain Samples/Libraries.
 *
 * A Box typically is labeled using a combination of letters and numbers. For example,
 * the first position in a box would be "A01".
 *
 * A Box usually has dimensions 8 by 12. (A-H, 1-12, A01 through H12)
 */

public interface Box extends SecurableByProfile, Barcodable, Locatable, Deletable {

  public static final String PREFIX = "BOX";

  /**
   * Sets the Id of this Box object.
   *
   * @param long id.
   */
  public void setId(long id);

  /**
   * Sets the name of this Box object.
   *
   * @param String name.
   */
   public void setName(String name);


  /**
   * Returns the Alias of this Box object.
   *
   * @return String alias.
   */
  public String getAlias();

  /**
   * Sets the alias of this Box object.
   *
   * @param String alias.
   */
  public void setAlias(String alias);


  /**
   * Returns the number of rows of this Box object.
   *
   * @return int rows.
   */
  public int getNumRows();

  /**
   * Sets the number of rows of this Box object.
   *
   * @param int rows.
   */
  public void setNumRows(int rows);


  /**
   * Returns the number of columns of this Box object.
   *
   * @return int columns.
   */
  public int getNumColumns();

  /**
   * Sets the number of columns of this Box object.
   *
   * @param int columns.
   */
  public void setNumColumns(int columns);

  /**
   * Returns the Map representing the Boxables (Samples, Libraries) of this Box object.
   *
   * @return Map<String, Boxable> items
   */
  public Map<String, Boxable> getBoxables();

  /**
   * Sets the Map of BoxItems of this Box object.
   *
   * @param Map<String, Boxable> items
   * @throws InvalidBoxPositionException
   */
  public void setBoxables(Map<String, Boxable> items);

  /**
   * Adds a BoxItem to the Box object at the given position.
   *
   * Note: this method is not responsible for any Boxable stored at the given position, it will be replaced by the
   *       given item.
   *
   * @param BoxItem item, String position.
   * @throws IllegalArgumentException if the given position is not in the correct format
   *         IndexOutOfBoundsException if the given Row letter or column value is too big for the Box
   */
  public void setBoxable(String position, Boxable item);

  /**
   * Returns the Boxable at position given.
   *
   * @param String position
   * @return BoxItem at position
   * @throws IllegalArgumentException if the given position is not in the correct format
   *         IndexOutOfBoundsException if the given Row letter or column value is too big for the Box
   */
  public Boxable getBoxable(String position);

  /**
   * Removes a Boxable item from the given position
   *
   * @param String position
   * @throws IllegalArgumentException if the given position is not in the correct format
   *         IndexOutOfBoundsException if the given Row letter or column value is too big for the Box
   */
  public void removeBoxable(String position);

  /**
   * Removes a given Boxable item from the Box
   *
   * @param Boxable boxable
   */
  public void removeBoxable(Boxable boxable);

  /**
   * Removes ALL Boxable items from the Box
   *
   */
  public void removeAllBoxables();

  /**
   * Returns the number of free positions left in the Box.
   *
   * @return int free positions
   */
  public int getFree();

  /**
   * Returns true/false is the position is free or not
   *
   * @return true/false if position is taken by another Boxable item or not
   * @throws IllegalArgumentException if the given position is not in the correct format
   *         IndexOutOfBoundsException if the given Row letter or column value is too big for the Box
   */
  public boolean isFreePosition(String position);

  /**
   * Returns whether or not the given String is a valid position or not
   *
   * @return validity of the position string
   * @throws IllegalArgumentException if the given position is not in the correct format
   *         IndexOutOfBoundsException if the given Row letter or column value is too big for the Box
   */
  public boolean isValidPosition(String position);

  /**
   * Returns whether or not the given Boxable item exists in the Box
   *
   * @return existence of boxable in Box
   */
  public boolean boxableExists(Boxable boxable);
  /**
   * Returns the number of positions in the Box.
   *
   * @return int number of positions
   */
  public int getSize();

  /**
   * Set Boxable Item at position to empty and removes the location for the Boxable
   *
   *
   * @param String position
   * @throws IllegalArgumentException if the given position is not in the correct format
   *         IndexOutOfBoundsException if the given Row letter or column value is too big for the Box
   */
  public void setBoxableEmpty(String position);

  /**
   * Sets all Boxable items to empty and removes their locations
   *
   */
  public void setAllBoxablesEmpty();

  /**
   * Get the Boxable items in 2-D array form
   *
   * @return 2D Boxable array
   */
   public Boxable[][] to2DArray();

   public BoxType getType();

   public void setType(BoxType type);
}
