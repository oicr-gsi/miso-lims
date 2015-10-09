package uk.ac.bbsrc.tgac.miso.integration.test.visionmate;

import org.junit.BeforeClass;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.test.BoxScannerTests;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScanner;
import ca.on.oicr.gsi.visionmate.RackType;
import ca.on.oicr.gsi.visionmate.RackType.Manufacturer;
import ca.on.oicr.gsi.visionmate.Scan;
import ca.on.oicr.gsi.visionmate.ServerConfig;
import ca.on.oicr.gsi.visionmate.mockServer.MockScannerServer;

public class VisionMateScannerTests extends BoxScannerTests<VisionMateScanner> {
  
  private static MockScannerServer server;
  private static VisionMateScanner client;
  
  @BeforeClass
  public static void setup() throws IntegrationException {
    server = new MockScannerServer();
    client = new VisionMateScanner("127.0.0.1", 8000);
  }
  
  @Override
  protected VisionMateScanner getScanner() throws IntegrationException {
    return client;
  }

  @Override
  protected void simulateScan(BoxScan scan) {
    int rows = scan.getRowCount();
    int cols = scan.getColumnCount();
    server.setCurrentProduct(new RackType(Manufacturer.MATRIX, rows, cols));
    String[] barcodes = new String[rows*cols];
    int i = 0;
    for (int col = 1; col <= cols; col++) {
      for (int row = 1; row <= rows; row++) {
        barcodes[i] = scan.getBarcode(row, col);
        i++;
      }
    }
    server.emulateScan(barcodes);
  }

  @Override
  protected BoxScan getSampleScan(String barcode) {
    RackType rack = new RackType(Manufacturer.MATRIX, 2, 2);
    ServerConfig config = new ServerConfig();
    String data = barcode + ",22222,33333,44444,";
    Scan wrappedScan = new Scan(rack, data, config);
    return new VisionMateScan(wrappedScan);
  }

  @Override
  protected void prePrepare() {
    new Thread(server).start();
  }

  @Override
  protected void preGet() {
    new Thread(server).start();
  }

}
