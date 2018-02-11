package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.pf4j.Extension;

import uk.ac.bbsrc.tgac.miso.plugins.extensionpoint.ArrayMessageProvider;

@Extension
public class DefaultArrayMessageProvider implements ArrayMessageProvider {

  @Override
  public String getMessage() {
    return "Hello from MISO";
  }

}
