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
  private static final String configPath = "res/data/config/";
  private static final String GAME_PROPERTIES_FILE = "game.properties";
  private static final String LOG_PROPERTIES_FILE = "log4j.properties";
  private ResourceManager resources;

  public Config(ResourceManager resources) {
    this.resources = resources;
  }

  public void configure() {
    try {
      loadLog4JProperties();
      loadGameProperties();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    resources.setImgPath("res/image/");
    resources.setDataPath("res/data/");
  }

  private static void loadLog4JProperties() throws IOException {
    Properties props = new Properties();
    InputStream in = ResourceLoader.getResourceAsStream(configPath + LOG_PROPERTIES_FILE);
    props.load(in);
    PropertyConfigurator.configure(props);
  }

  private void loadGameProperties() throws IOException {
    Properties prop = new Properties(System.getProperties());
    InputStream in = ResourceLoader.getResourceAsStream(configPath + GAME_PROPERTIES_FILE);
    prop.load(in);
    System.setProperties(prop);
  }
}