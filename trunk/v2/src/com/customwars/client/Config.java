package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.loading.UserConfigParser;
import com.customwars.client.ui.state.CWInput;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.newdawn.slick.util.ResourceLoader;
import tools.IOUtil;

import java.io.File;
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
 * There is global configuration: game, user, log4J, language properties
 * and plugin configuration plugin properties, sound, maps, images
 *
 * Persistance properties(user config) are located in the user home dir
 *
 * @author Stefan
 */
public class Config {
  private static final Logger logger = Logger.getLogger(Config.class);
  private static final String HOME_DIR = System.getProperty("user.home") + "/.cw2/";
  public static final String MAPS_DIR = HOME_DIR + "maps/";
  private static final String CONFIG_PATH = "res/data/config/";
  private static final String GAME_PROPERTIES_FILE = "game.properties";
  private static final String LOG_PROPERTIES_FILE = "log4j.properties";
  private static final String USER_PROPERTIES_FILE = "user.properties";
  private static final String USER_DEFAULTS_PROPERTIES_FILE = "userDefaults.properties";
  private static final String PLUGIN_PROPERTIES_FILE = "plugin.properties";
  private static final String LANG_BUNDLE_PATH = "res.data.lang.Languages";
  private String pluginLocation;

  private ResourceManager resources;
  private Properties userProperties;
  private Map<String, Properties> persistenceProperties; // Location -> Properties, Properties that can be stored
  private UserConfigParser userConfigParser;

  public Config(ResourceManager resources) {
    this.resources = resources;
    persistenceProperties = new HashMap<String, Properties>();
  }

  public void configure() {
    createHomeDir();
    createEmptyUserPropertyFileIfNonePresent();
    loadProperties();

    resources.setImgPath(pluginLocation + "/images/");
    resources.setSoundPath(pluginLocation + "/sound/");
    resources.setDataPath(pluginLocation + "/data/");
    resources.addMapPath(MAPS_DIR);
    resources.addMapPath("res/maps/");
    resources.setFontPath("res/data/fonts/");
    resources.setDarkPercentage(App.getInt("display.darkpercentage"));
  }

  private void createHomeDir() {
    File homeDir = new File(HOME_DIR);
    File mapsDir = new File(MAPS_DIR);

    IOUtil.mkDir(homeDir, "Could not create home dir " + HOME_DIR);
    IOUtil.mkDir(mapsDir);
  }

  private void createEmptyUserPropertyFileIfNonePresent() {
    File userPropertiesFile = new File(HOME_DIR + USER_PROPERTIES_FILE);

    if (!userPropertiesFile.exists()) {
      IOUtil.createNewFile(userPropertiesFile, "Could not create empty user properties file");
    }
  }

  private void loadProperties() {
    try {
      loadLog4JProperties();
      loadGameProperties();
      loadUserProperties();

      String language = userProperties.getProperty("user.lang");
      String pluginName = userProperties.getProperty("user.activeplugin", "default");
      pluginLocation = "res/plugin/" + pluginName;

      loadLang(language);
      loadPluginProperties();
    } catch (IOException e) {
      throw new RuntimeException("Error reading file", e);
    }
  }

  /**
   * Load the command -> key-mouse control bindings from the userProperties file
   * Add the bindings to cwInput
   *
   * @param cwInput the inputprovider that stores each control->command mapping
   */
  public void loadInputBindings(CWInput cwInput) {
    userConfigParser = new UserConfigParser(cwInput);
    userConfigParser.readInputConfig(userProperties);
  }

  private void loadLog4JProperties() throws IOException {
    Properties log4JProperties = loadProperties(CONFIG_PATH + LOG_PROPERTIES_FILE);
    PropertyConfigurator.configure(log4JProperties);
  }

  private void loadGameProperties() throws IOException {
    Properties gameProperties = loadProperties(CONFIG_PATH + GAME_PROPERTIES_FILE, App.getProperties());
    App.setProperties(gameProperties);
  }

  private void loadUserProperties() throws IOException {
    Properties defaults = loadProperties(CONFIG_PATH + USER_DEFAULTS_PROPERTIES_FILE);
    userProperties = loadProperties(HOME_DIR + USER_PROPERTIES_FILE, defaults);
    persistenceProperties.put(HOME_DIR + USER_PROPERTIES_FILE, userProperties);
  }

  public void loadLang(String languageCode) throws IOException {
    if (languageCode == null) languageCode = "EN";
    Locale locale = new Locale(languageCode);
    loadLangProperties(locale);
  }

  private void loadLangProperties(Locale locale) throws IOException {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    ResourceBundle bundle = PropertyResourceBundle.getBundle(LANG_BUNDLE_PATH, locale, loader);
    App.setLocaleResourceBundle(bundle);
    logger.info("Lang=" + locale);
  }

  private void loadPluginProperties() throws IOException {
    Properties pluginProperties = loadProperties(pluginLocation + "/data/" + PLUGIN_PROPERTIES_FILE, App.getProperties());
    App.setProperties(pluginProperties);
    logger.info("Plugin=" + pluginLocation);
  }

  private Properties loadProperties(String location) throws IOException {
    InputStream in = ResourceLoader.getResourceAsStream(location);
    return IOUtil.loadProperties(in);
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

  /**
   * Store each Property file in persistenceProperties
   */
  public void storePersistenceProperties() {
    for (String propertyFilePath : persistenceProperties.keySet()) {
      storePropertyFile(persistenceProperties.get(propertyFilePath), propertyFilePath);
    }
  }

  private void storePropertyFile(Properties properties, String location) {
    try {
      FileOutputStream out = new FileOutputStream(location);
      properties.store(out, null);
      IOUtil.closeStream(out);
    } catch (IOException e) {
      logger.warn("Could not save property file to " + location, e);
    }
  }
}