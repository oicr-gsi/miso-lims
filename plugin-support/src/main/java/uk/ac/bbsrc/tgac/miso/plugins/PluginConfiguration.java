package uk.ac.bbsrc.tgac.miso.plugins;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class PluginConfiguration {

  @Bean
  public SpringPluginManager pluginManager() {
    return new SpringPluginManager();
  }

  @Bean
  @DependsOn("pluginManager")
  public ExtensionManager getExtensionManager() {
    return new ExtensionManager();
  }

}
