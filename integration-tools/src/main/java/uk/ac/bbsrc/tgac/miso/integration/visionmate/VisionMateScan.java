package uk.ac.bbsrc.tgac.miso.integration.visionmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import ca.on.oicr.gsi.visionmate.Scan;

/**
 * This class is an implementation of BoxScan that wraps (already immutable) scan data retrieved using a {@link VisionMateScanner}
 */
public class VisionMateScan implements BoxScan {
  
  private final Scan scan;
  
  /**
   * Constructs a new VisionMateScan to wrap scan data from a VisionMateScanner
   * 
   * @param scan scan data retrieved using a {@link VisionMateScanner}
   */
  public VisionMateScan(Scan scan) {
    this.scan = scan;
  }

  @Override
  public String getBarcode(String position) {
    // TODO use BoxUtils
    return null;
  }

  @Override
  public String getBarcode(char row, int column) {
    return scan.getBarcode(row, column);
  }

  @Override
  public String getBarcode(int row, int column) {
    return scan.getBarcode(row, column);
  }

  @Override
  public String[][] getBarcodesArray() {
    // scan is immutable and already returns a defensive copy, so returning this does not break immutability
    return scan.getBarcodes();
  }

  @Override
  public Map<String, String> getBarcodesMap() {
    String[][] array = scan.getBarcodes();
    Map<String, String> map = new HashMap<>();
    for (int row = 0; row < array.length; row++) {
      for (int col = 0; col < array[row].length; col++) {
        map.put(getPosString(row+1, col+1), array[row][col]);
      }
    }
    return map;
  }
  
  private String getPosString(int row, int col) {
    // TODO: use BoxUtils
    return null;
  }

  @Override
  public boolean isFull() {
    return scan.isFull();
  }

  @Override
  public boolean isEmpty() {
    return scan.isEmpty();
  }

  @Override
  public int getMaximumTubeCount() {
    return scan.getRowCount() * scan.getColumnCount();
  }

  @Override
  public int getTubeCount() {
    return getMaximumTubeCount() - scan.getNoTubeCount();
  }

  @Override
  public boolean hasReadErrors() {
    return scan.getNoReadCount() != 0;
  }

  @Override
  public List<String> getReadErrorPositions() {
    final String[][] barcodes = scan.getBarcodes();
    final String noRead = scan.getNoReadLabel();
    List<String> positions = new ArrayList<>();
    
    for (int row = 0; row < barcodes.length; row++) {
      for (int col = 0; col < barcodes[row].length; col++) {
        if (noRead.equals(barcodes[row][col])) positions.add(getPosString(row+1, col+1));
      }
    }
    
    return positions;
  }

  @Override
  public int getRowCount() {
    return scan.getRowCount();
  }

  @Override
  public int getColumnCount() {
    return scan.getColumnCount();
  }

}
