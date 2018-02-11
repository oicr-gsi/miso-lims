package uk.ac.bbsrc.tgac.miso.plugins;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.plugins.extensionpoint.ArrayMessageProvider;

public class ExtensionManager {

  @Autowired(required = false)
  private final List<ArrayMessageProvider> arrayMessageProviders = new ArrayList<>();

  public List<ArrayMessageProvider> getArrayMessageProviders() {
    return arrayMessageProviders;
  }

}
