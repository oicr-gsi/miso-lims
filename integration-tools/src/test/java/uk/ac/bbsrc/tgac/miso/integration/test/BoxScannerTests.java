package uk.ac.bbsrc.tgac.miso.integration.test;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

public abstract class BoxScannerTests<T extends BoxScanner> {
  
  /**
   * @return a BoxScanner to test with. This may be a static object
   */
  protected abstract T getScanner() throws IntegrationException;
  
  /**
   * Simulates a scan as if an end user has used the box scanner to scan a box of tubes
   * 
   * @param scan a reference BoxScan to simulate
   */
  protected abstract void simulateScan(BoxScan scan);
  
  /**
   * Returns a new BoxScan representing a scan of a 2x2 box with the specified barcode in position A01
   * 
   * @param barcode the barcode to include in position A01
   * @return the new BoxScan
   */
  protected abstract BoxScan getSampleScan(String barcode);
  
  /**
   * This method is called before {@link BoxScanner#prepareScan(int, int)} in tests incase any mock setup needs to be done
   */
  protected abstract void prePrepare();
  
  /**
   * This method is called before {@link BoxScanner#getScan()} in tests incase any mock setup needs to be done
   */
  protected abstract void preGet();
  
  @Test
  public void testPrepare() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
  }
  
  @Test
  public void testScanBeforeGet() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
    simulateScan(getSampleScan("11111"));
    preGet();
    scanner.getScan();
  }
  
  @Test
  public void testGetBeforeScan() throws IntegrationException {
    BoxScanner scanner = getScanner();
    prePrepare();
    scanner.prepareScan(2, 2);
    
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          simulateScan(getSampleScan("22222"));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
    }).run();
    
    preGet();
    scanner.getScan();
  }

}
