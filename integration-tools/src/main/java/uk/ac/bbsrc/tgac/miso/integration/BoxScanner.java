package uk.ac.bbsrc.tgac.miso.integration;

import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

public interface BoxScanner {
  
  /**
   * Performs any setup actions required before the user scans a box
   * 
   * @throws IntegrationException if any hardware-specific error occurs
   */
  public void prepareScan() throws IntegrationException;
  
  /**
   * Retrieves scanned barcode data from the scanner
   * 
   * @return the scan data
   * @throws IntegrationException if any hardware-specific error occurs
   */
  public BoxScan getScan() throws IntegrationException;
  
}
