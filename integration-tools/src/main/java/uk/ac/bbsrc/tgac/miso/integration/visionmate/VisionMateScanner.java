package uk.ac.bbsrc.tgac.miso.integration.visionmate;

import java.io.IOException;
import java.net.UnknownHostException;

import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import ca.on.oicr.gsi.visionmate.RackType;
import ca.on.oicr.gsi.visionmate.RackType.Manufacturer;
import ca.on.oicr.gsi.visionmate.Scan;
import ca.on.oicr.gsi.visionmate.ScannerException;
import ca.on.oicr.gsi.visionmate.ServerConfig;
import ca.on.oicr.gsi.visionmate.VisionMateClient;

public class VisionMateScanner implements BoxScanner {
  
  private static final int timeout = 15;
  
  private VisionMateClient client;
  
  public VisionMateScanner(String host, int port) throws IntegrationException {
    ServerConfig config = new ServerConfig();
    try {
      client = new VisionMateClient(host, port, config, timeout);
    } catch (UnknownHostException e) {
      throw new IntegrationException("Scanner host could not be resolved", e);
    }
  }

  @Override
<<<<<<< HEAD
  public synchronized void prepareScan(int expectedRows, int expectedColumns) throws IntegrationException {
    try {
      client.connect();
      // Note: VisionMate documentation recommends setting Matrix as the manufacturer if the rack manufacturer is unknown. 
      // The row and column configuration is the important part of this.
      RackType rack = new RackType(Manufacturer.MATRIX, expectedRows, expectedColumns);
      
      if (!client.setCurrentProduct(rack) || !client.resetStatus()) {
        throw new IntegrationException("Scanner failed to execute preparation instructions");
      }
    } catch (IOException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    } finally {
      client.close();
    }
  }

  @Override
  public synchronized BoxScan getScan() throws IntegrationException {
    try {
      client.connect();
      Scan scan = client.waitForScan(timeout);
      return scan == null ? null : new VisionMateScan(scan);
    } catch (IOException e) {
      throw new IntegrationException("Error communicating with the scanner", e);
    } catch (ScannerException e) {
      throw new IntegrationException("Error reported by the scanner", e);
    } finally {
      client.close();
    }
  }

}
