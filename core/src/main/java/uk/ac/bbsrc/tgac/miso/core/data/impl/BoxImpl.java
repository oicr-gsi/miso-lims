package uk.ac.bbsrc.tgac.miso.core.data.impl;

//import com.eaglegenomics.simlims.core.SecurityProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.exception.InvalidBoxPositionException;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.util.Map;
import java.util.HashMap;


public class BoxImpl extends AbstractBox {
  protected static final Logger log = LoggerFactory.getLogger(BoxImpl.class);

  private static final int DEFAULT_ROWS = 8;     // A-H
  private static final int DEFAULT_COLUMNS = 12; // 1-12

  // The contents of the Box
  private Map<String, Boxable> boxableItems = new HashMap<String, Boxable>();

  public BoxImpl() {
    setNumRows(DEFAULT_ROWS);
    setNumColumns(DEFAULT_COLUMNS);
  }

  public BoxImpl(int rows, int cols) {
    setNumRows(rows);
    setNumColumns(cols);
  }

  // If parse fails, return -1, else return parsed integer
  private int tryParseInt(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  // Determines if there is a Boxable object at position or not
  private boolean isFreePosition(String position) {
    if (boxableItems.get(position) == null)
      return true;
    return false;
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
  public void setBoxItems(Map<String, Boxable> items) throws InvalidBoxPositionException {
    this.boxableItems = items;
  }

  @Override
  public Map<String, Boxable> getBoxItems() {
  	return boxableItems;
  }

  // Returns true/false depending if item is in boxableItems
  private boolean itemExists(Boxable item) {
    return boxableItems.values().contains(item);
  }

  @Override
  public void setBoxItem(String position, Boxable item) throws InvalidBoxPositionException {
    if (isFreePosition(position) && itemExists(item)) {  // We want to swap the location
      boxableItems.values().remove(item);
      boxableItems.put(position, item);
    } else if (isFreePosition(position) && !itemExists(item)) { // Safe to place item at location
      boxableItems.put(position, item);
    } else {
      // Position is taken, throw exception?
    }
      // TODO
      // - Is efficiency necessary here? Save booleans!
      // Alert the user that this position is taken
      // Need overwrite flag?
      // boxableItems.replace(position, item);
  }

  @Override
  public Boxable getBoxItem(String position) throws InvalidBoxPositionException {
    return boxableItems.get(position);
  }

  @Override
  public void removeBoxItem(String position) throws InvalidBoxPositionException {
    if (isFreePosition(position))
      throw new InvalidBoxPositionException("Cannot remove: No item at position.");
    else {
      boxableItems.values().remove(boxableItems.get(position));
    }
  }

  // Return the position of the item, given row and column
  private String toPosition(int row, int col) {
    char letter = LimsUtils.getCharForNumber(row);
    String position = String.format("%02d", col); // pad col with zeros
    position = letter + position;
    return position;
  }

  @Override
  public void setBoxItemEmpty(String position) throws InvalidBoxPositionException {
    if (isFreePosition(position))
      throw new InvalidBoxPositionException("Cannot set empty: There is no item at position.");
    else {
      boxableItems.get(position).setEmptied(true);
    }
  }

  @Override
  public void setAllBoxItemsEmpty() {
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
