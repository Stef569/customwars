package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load and apply configuration
 *
 * @author Stefan
 */
public class Config {
  private static final Logger logger = Logger.getLogger(Config.class);
  private ResourceManager resources;

  public Config(ResourceManager resources) {
    this.resources = resources;
  }

  public void configure() {
    try {
      loadAndApplyLog4JProperties();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    resources.setImgPath("res/image/");
    resources.setDataPath("res/data/");
  }

  private static void loadAndApplyLog4JProperties() throws IOException {
    Properties props = new Properties();
    InputStream in = ResourceLoader.getResourceAsStream("res/data/config/log4j.properties");
    props.load(in);
    PropertyConfigurator.configure(props);
  }
}