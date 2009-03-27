package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.loading.UserConfigParser;
import com.customwars.client.ui.state.CWInput;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.newdawn.slick.util.ResourceLoader;
import tools.IOUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
  private static final String USER_PROPERTIES_FILE = "user.properties";
  private static final String USER_DEFAULTS_PROPERTIES_FILE = "userDefaults.properties";

  private ResourceManager resources;
  private Properties userProperties;
  private Map<String, Properties> properties;
  private UserConfigParser userConfigParser;

  public Config(ResourceManager resources) {
    this.resources = resources;
    properties = new HashMap<String, Properties>();
  }

  public void configure() {
    try {
      loadLog4JProperties();
      loadGameProperties();
      loadUserProperties();
    } catch (IOException e) {
      throw new RuntimeException("Error reading file", e);
    }

    resources.setImgPath("res/image/");
    resources.setDataPath("res/data/");
  }

  public void configureAfterStartup(CWInput cwInput) {
    userConfigParser = new UserConfigParser(cwInput);
    userConfigParser.readInputConfig(userProperties);
  }

  private void loadLog4JProperties() throws IOException {
    Properties log4JProperties = loadProperties(configPath + LOG_PROPERTIES_FILE, null);
    PropertyConfigurator.configure(log4JProperties);
  }

  private void loadGameProperties() throws IOException {
    Properties gameProperties = loadProperties(configPath + GAME_PROPERTIES_FILE, System.getProperties());
    System.setProperties(gameProperties);
  }

  private void loadUserProperties() throws IOException {
    Properties defaults = loadProperties(configPath + USER_DEFAULTS_PROPERTIES_FILE, null);
    userProperties = loadProperties(configPath + USER_PROPERTIES_FILE, defaults);
  }

  private Properties loadProperties(String location, Properties defaults) throws IOException {
    InputStream in = ResourceLoader.getResourceAsStream(location);
    return IOUtil.loadProperties(in, defaults);
  }

  /**
   * Store the keys defined in CWInput into newUserProperties
   * Overwrite the old user properties(the one loaded on startup)
   */
  public void storeInputConfig() {
    Properties newUserProperties = userConfigParser.writeInputConfig();

    for (Object obj : newUserProperties.keySet()) {
      String key = (String) obj;
      userProperties.put(key, newUserProperties.getProperty(key));
    }
  }

  public void storeProperties() {
    for (String props : properties.keySet()) {
      storePropertyFile(properties.get(props), props);
    }
  }

  private void storePropertyFile(Properties properties, String location) {
    try {
      FileOutputStream out = new FileOutputStream(location);
      properties.store(out, null);
      IOUtil.closeStream(out);
    } catch (IOException e) {
      logger.warn("Could not save property file to " + location + " ", e);
    }
  }
}