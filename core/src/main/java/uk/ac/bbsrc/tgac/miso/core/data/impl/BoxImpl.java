package uk.ac.bbsrc.tgac.miso.core.data.impl;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.exception.InvalidBoxPositionException;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

import java.util.Map;
import java.util.HashMap;

public class BoxImpl extends AbstractBox {
  protected static final Logger log = LoggerFactory.getLogger(BoxImpl.class);

  private static final int DEFAULT_ROWS = 8;     // A-H
  private static final int DEFAULT_COLUMNS = 12; // 1-12

  // The contents of the Box
  private Map<String, Boxable> boxableItems = new HashMap<String, Boxable>();

  /*
   * Construct new Box with defaults, and an empty SecurityProfile
   *
   */
  public BoxImpl() {
    setNumRows(DEFAULT_ROWS);
    setNumColumns(DEFAULT_COLUMNS);
    setSecurityProfile(new SecurityProfile());
  }

  /*
   * Construct new Box using Security Profile owned by a given User
   * @param User user
   */
  public BoxImpl(User user) {
    setNumRows(DEFAULT_ROWS);
    setNumColumns(DEFAULT_COLUMNS);
    setSecurityProfile(new SecurityProfile(user));
  }

  /*
   * Construct new Box of given dimensions and empty SecurityProfile
   *
   * @param int rows, int columns
   */
  public BoxImpl(int rows, int cols) {
    setNumRows(rows);
    setNumColumns(cols);
    setSecurityProfile(new SecurityProfile());
  }

  /*
   * Construct new Box of given dimensions and existing SecurityProfile
   *
   * @param int rows, int columns, User user
   */
  public BoxImpl(int rows, int cols, User user) {
    setNumRows(rows);
    setNumColumns(cols);
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public boolean isFreePosition(String position) {
    if (boxableItems.get(position) == null)
      return true;
    return false;
  }

  @Override
  public boolean isValidPosition(String position) {
    if (!position.matches("[A-Z][0-9][0-9]")) return false;
    if (BoxUtils.getNumberForChar(position.charAt(0)) > getNumRows()) return false;
    int col = BoxUtils.tryParseInt(position.substring(1,3));
    if (col <= 0 || col > getNumColumns()) return false;
    return true;
  }

  @Override
  public int getFree() {
    return getSize() - boxableItems.values().size();
  }

  @Override
  public String getLabelText() {
  	// TODO Auto-generated method stub
  	return null;
  }

  @Override
  public void setBoxables(Map<String, Boxable> items) throws InvalidBoxPositionException {
    this.boxableItems = items;
  }

  @Override
  public Map<String, Boxable> getBoxables() {
  	return boxableItems;
  }

  @Override
  public boolean boxableExists(Boxable boxable) {
    return boxableItems.values().contains(boxable);
  }

  @Override
  public void setBoxable(String position, Boxable item) throws InvalidBoxPositionException {
    if (!isFreePosition(position)) {
      throw new InvalidBoxPositionException(position + " is already occupied!");
    }
    else {
      boxableItems.put(position, item);
    }
  }

  @Override
  public Boxable getBoxable(String position) {
    return boxableItems.get(position);
  }

  @Override
  public void removeBoxable(String position) {
    removeBoxable(getBoxable(position));
  }

  @Override
  public void removeBoxable(Boxable boxable) {
    boxable.setLocationBarcode(""); //TODO: GLT-219
    boxableItems.values().remove(boxable);
  }

  @Override
  public void removeAllBoxables() {
    for (Boxable boxable : boxableItems.values()) {
      removeBoxable(boxable);
    }
  }

  // Return the position of the item, given row and column
  private String toPosition(int row, int col) {
    char letter = BoxUtils.getCharForNumber(row);
    String position = String.format("%02d", col); // pad col with zeros
    position = letter + position;
    return position;
  }

  @Override
  public void setBoxableEmpty(String position) {
    boxableItems.get(position).setEmptied(true);
  }

  @Override
  public void setAllBoxablesEmpty() {
    for (Boxable item : boxableItems.values()) {
      item.setEmptied(true);
    }
  }

  @Override
  public Boxable[][] to2DArray() {
    Boxable[][] arr = new Boxable[getNumRows()][getNumColumns()];
    for (int i = 0; i < getNumRows(); i++) {
      for (int j = 0; j < getNumColumns(); j++) {
        arr[i][j] = boxableItems.get(toPosition(i+1, j+1));
      }
    }
    return arr;
  }

  @Override
  public boolean isDeletable() {
    return true;
  }
}
