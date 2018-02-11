package uk.ac.bbsrc.tgac.miso.plugins.extensionpoint;

import org.pf4j.ExtensionPoint;

public interface ArrayMessageProvider extends ExtensionPoint {

  public String getMessage();

}
