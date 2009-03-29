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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

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
  private static final String PLUGIN_PROPERTIES_FILE = "plugin.properties";
  private static final String LANG_BUNDLE_PATH = "data.lang.Languages";
  private String activePluginLocation;                   // The active plugin, read from user.plugin

  private ResourceManager resources;
  private Properties userProperties;
  private Map<String, Properties> persistenceProperties; // Location -> Properties, Properties that can be stored
  private UserConfigParser userConfigParser;             // Parses and applies user configuration
  private static ResourceBundle messages;                // Contains A Bundle of Translated strings in 1 language

  public Config(ResourceManager resources) {
    this.resources = resources;
    persistenceProperties = new HashMap<String, Properties>();
  }

  public void configure() {
    loadProperties();

    resources.setImgPath(activePluginLocation + "/images/");
    resources.setDataPath(activePluginLocation + "/data/");
  }

  private void loadProperties() {
    try {
      loadLog4JProperties();
      loadGameProperties();
      loadUserProperties();

      String activeLang = userProperties.getProperty("user.lang");
      String activePlugin = userProperties.getProperty("user.activeplugin", "default");
      activePluginLocation = "res/plugin/" + activePlugin;

      loadLang(activeLang);
      loadPluginProperties();
    } catch (IOException e) {
      throw new RuntimeException("Error reading file", e);
    }
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
    persistenceProperties.put(configPath + USER_PROPERTIES_FILE, userProperties);
  }

  public void loadLang(String languageCode) {
    if (languageCode == null) languageCode = "EN";
    Locale locale = new Locale(languageCode);
    loadLangProperties(locale);
  }

  private void loadLangProperties(Locale locale) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    messages = PropertyResourceBundle.getBundle(LANG_BUNDLE_PATH, locale, loader);
    logger.info("Lang=" + locale);
  }

  private void loadPluginProperties() throws IOException {
    Properties pluginProperties = loadProperties(activePluginLocation + "/data/" + PLUGIN_PROPERTIES_FILE, System.getProperties());
    System.setProperties(pluginProperties);
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
    for (String props : persistenceProperties.keySet()) {
      storePropertyFile(persistenceProperties.get(props), props);
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

  /**
   * Get a translated String for msg
   *
   * @param msg lower case key definded in the language properties file
   * @return msg translated to the current language
   */
  public static String getMsg(String msg) {
    return (String) messages.getObject(msg);
  }
}