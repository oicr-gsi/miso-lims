package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Map;
import uk.ac.bbsrc.tgac.miso.core.exception.InvalidBoxPositionException;

/**
 * This interface describes a Box which is a n by m container which contains tubes
 * which contain Samples/Libraries.
 *
 * A Box typically is labeled using a combination of letters and numbers. For example,
 * the first position in a box would be "A01".
 *
 * A Box usually has dimensions 8 by 12. (A-H, 1-12, A01 through H12)
 */

public interface Box extends Barcodable, Locatable, Deletable {

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
   * Returns the Map representing the BoxItems of this Box object.
   *
   * @return Map<String, Boxable> items
   */
  public Map<String, Boxable> getBoxItems();

  /**
   * Sets the Map of BoxItems of this Box object.
   *
   * @param Map<String, Boxable> items
   * @throws InvalidBoxPositionException
   */
  public void setBoxItems(Map<String, Boxable> items) throws InvalidBoxPositionException;

  /**
   * Adds a BoxItem to the Box object at the given position.
   *
   * @param BoxItem item, String position.
   * @throws InvalidBoxPositionException
   */
  public void setBoxItem(String position, Boxable item) throws InvalidBoxPositionException;

  /**
   * Returns the Boxable at position given.
   *
   * @param String position
   * @return BoxItem at position
   * @throws InvalidBoxPositionException if invalid position given
   */
  public Boxable getBoxItem(String position) throws InvalidBoxPositionException;

  /**
   * Returns the number of free positions left in the Box.
   *
   * @return int free positions
   */
  public int getFree();

  /**
   * Returns the number of positions in the Box.
   *
   * @return int number of positions
   */
  public int getSize();

  /**
   * Get the Boxable items in 2-D array form
   *
   * @return 2D Boxable array
   */
   public Boxable[][] toArray();
}
