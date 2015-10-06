package uk.ac.bbsrc.tgac.miso.integration;

import java.util.Map;

public interface BoxScan {
  
  /**
   * Get the barcode from a position in the box
   * 
   * @param position the box position, specified in a String containing row letter and two-digit column number (e.g. "A01" for top-left)
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of 
   * the tube in the specified position
   */
  public String getBarcode(String position);
  
  /**
   * Get the barcode from a position in the box
   * 
   * @param row row letter of the position to examine, where 'A' is the first row
   * @param column column number of the position to examine, where 1 is the first row
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of 
   * the tube in the specified position
   */
  public String getBarcode(char row, int column);
  
  /**
   * Get the barcode from a position in the box
   * 
   * @param row row number of the position to examine, where 1 is the first row
   * @param column column number of the position to examine, where 1 is the first row
   * @return Null if there is no tube in the position, or an empty String if the barcode could not be read; otherwise, the barcode of 
   * the tube in the specified position
   */
  public String getBarcode(int row, int column);
  
  /**
   * @return a 2D array containing all of the scanned barcodes. The outer array contains the rows, and the inner array contains columns 
   * (e.g. position "B10" = result[1,9])
   */
  public String[][] getBarcodesArray();
  
  /**
   * @return a Map containing all of the scanned barcodes. The keys are position names containing the row letter and two-digit column 
   * number (e.g. "A01" for top-left)
   */
  public Map<String,String> getBarcodesMap();
  
  /**
   * @return true if every position in the box contains a tube; false otherwise. A return value of true does not indicate success in 
   * reading the barcode. The "barcode" for a failed read will be an empty String. See {@link #hasReadErrors()}
   */
  public boolean isFull();
  
  /**
   * @return true if there are no tubes in this box; false otherwise
   */
  public boolean isEmpty();
  
  /**
   * @return the maximum number of tubes that this box can accommodate
   */
  public int getPositionCount();
  
  /**
   * @return the number of tubes currently in the box. This count may include tubes with barcodes that were successfully read, as well 
   * as tubes with barcodes that the scanner failed to read. See {@link #hasReadErrors()}
   */
  public int getTubeCount();
  
  /**
   * @return true if the scanner failed to read the barcode on any tube(s) in the box; false otherwise. An empty position (no tube) does 
   * not count as a read error; there must actually be a tube in the position to have a read error
   */
  public boolean hasReadErrors();
  
}
