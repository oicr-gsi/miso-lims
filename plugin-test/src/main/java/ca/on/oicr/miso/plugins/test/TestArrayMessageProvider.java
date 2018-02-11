package ca.on.oicr.miso.plugins.test;

import org.pf4j.Extension;

import uk.ac.bbsrc.tgac.miso.plugins.extensionpoint.ArrayMessageProvider;

@Extension
public class TestArrayMessageProvider implements ArrayMessageProvider {

  public String getMessage() {
    return "Hello from PluginTest!";
  }

}
