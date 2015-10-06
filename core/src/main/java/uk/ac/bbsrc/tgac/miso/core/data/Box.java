package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Map;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.exception.InvalidBoxPositionException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * This interface describes a Box which is a n by m container which contains tubes which contain Samples/Libraries.
 *
 * A Box typically is labeled using a combination of letters and numbers. For example, the first position in a box would be "A01".
 *
 * A Box usually has dimensions 8 by 12. (A-H, 1-12, A01 through H12)
 */

public interface Box extends SecurableByProfile, Barcodable, Locatable, Deletable {

   public static final String PREFIX = "BOX";

   /**
    * Returns the Alias of this Box object.
    *
    * @return String alias.
    */
   public String getAlias();

   /**
    * Returns the Boxable at position given.
    *
    * @param String
    *           position
    * @return BoxItem at position
    * @throws InvalidBoxPositionException
    *            if invalid position given
    */
   public Boxable getBoxItem(String position) throws InvalidBoxPositionException;

   /**
    * Returns the Map representing the BoxItems of this Box object.
    *
    * @return Map<String, Boxable> items
    */
   public Map<String, Boxable> getBoxItems();

   /**
    * Returns the number of free positions left in the Box.
    *
    * @return int free positions
    */
   public int getFree();

   /**
    * Returns the number of columns of this Box object.
    *
    * @return int columns.
    */
   public int getNumColumns();

   /**
    * Returns the number of rows of this Box object.
    *
    * @return int rows.
    */
   public int getNumRows();

   public SecurityProfile getSecurityProfile();

   /**
    * Returns the number of positions in the Box.
    *
    * @return int number of positions
    */
   public int getSize();

   /**
    * Sets the alias of this Box object.
    *
    * @param String
    *           alias.
    */
   public void setAlias(String alias);

   /**
    * Adds a BoxItem to the Box object at the given position.
    *
    * @param BoxItem
    *           item, String position.
    * @throws InvalidBoxPositionException
    */
   public void setBoxItem(String position, Boxable item) throws InvalidBoxPositionException;

   /**
    * Sets the Map of BoxItems of this Box object.
    *
    * @param Map
    *           <String, Boxable> items
    * @throws InvalidBoxPositionException
    */
   public void setBoxItems(Map<String, Boxable> items) throws InvalidBoxPositionException;

   /**
    * Sets the Id of this Box object.
    *
    * @param long id.
    */
   public void setId(long id);

   /**
    * Sets the name of this Box object.
    *
    * @param String
    *           name.
    */
   public void setName(String name);

   /**
    * Sets the number of columns of this Box object.
    *
    * @param int columns.
    */
   public void setNumColumns(int columns);

   /**
    * Sets the number of rows of this Box object.
    *
    * @param int rows.
    */
   public void setNumRows(int rows);

   public void setSecurityProfile(SecurityProfile securityProfile);

   /**
    * Get the Boxable items in 2-D array form
    *
    * @return 2D Boxable array
    */
   public Boxable[][] toArray();

   public boolean userCanRead(User user);

   public boolean userCanWrite(User user);

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
  public void setBoxables(Map<String, Boxable> items) throws InvalidBoxPositionException;

  /**
   * Adds a BoxItem to the Box object at the given position.
   *
   * @param BoxItem item, String position.
   * @throws InvalidBoxPositionException
   */
  public void setBoxable(String position, Boxable item) throws InvalidBoxPositionException;

  /**
   * Returns the Boxable at position given.
   *
   * @param String position
   * @return BoxItem at position
   */
  public Boxable getBoxable(String position);

  /**
   * Removes a Boxable item from the given position
   *
   * @param String position
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
   */
  public boolean isFreePosition(String position);

  /**
   * Returns whether or not the given String is a valid position or not
   *
   * @return validity of the position string
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
   * @param String position
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

   BoxType getType();

   void setType(BoxType type);
}
