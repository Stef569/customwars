package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.script.BeanShell;
import com.customwars.client.script.ScriptManager;
import com.customwars.client.tools.IOUtil;
import com.customwars.client.tools.Log4jLogSystem;
import com.customwars.client.tools.StringUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Load and apply configuration
 * global configuration: game, user, log4J, language properties and maps
 * plugin configuration: plugin properties, sound, images
 */
public class Config {
  private static final Logger logger = Logger.getLogger(Config.class);
  private static final String HOME_DIR = System.getProperty("user.home") + "/.cw2";
  private static final String MAPS_DIR = HOME_DIR + "/maps/";

  private static final String GAME_PROPERTIES_FILE = "game.properties";
  private static final String LOG_PROPERTIES_FILE = "log4j.properties";
  private static final String USER_PROPERTIES_FILE = "user.properties";
  private static final String USER_DEFAULTS_PROPERTIES_FILE = "userDefaults.properties";
  private static final String PLUGIN_PROPERTIES_FILE = "plugin.properties";

  private static final String CO_SCRIPT_FILE = "co.bsh";
  private static final String MAIN_SCRIPT_FILE = "main.bsh";

  private final ResourceManager resources;
  private static String resourcesPath;
  private final ScriptManager coScriptManager;

  public Config(ResourceManager resources) {
    this.resources = resources;
    this.coScriptManager = new ScriptManager();
    Log.setLogSystem(new Log4jLogSystem());
  }

  /**
   * Load Configuration files relative to the current dir
   */
  public void load() {
    load("");
  }

  /**
   * Load configuration files relative to the given resourcesPath
   *
   * @param resPath The path to the resources
   */
  public void load(String resPath) {
    if (StringUtil.hasContent(resPath)) {
      String resourcesPath = StringUtil.appendTrailingSuffix(resPath, "/");
      resourcesPath += "resources";
      App.put("resources.path", resourcesPath);
      Config.resourcesPath = resourcesPath + "/res";
    } else {
      Config.resourcesPath = "resources/res";
    }

    checkForResourcesInClassPath();
    storePaths();
    initHomeDir();
    loadConfigFiles();
    initBeanshellConsole();
  }

  private static void checkForResourcesInClassPath() {
    URL url = Thread.currentThread().getContextClassLoader().getResource("res/data/config");

    if (url == null) {
      throw new IllegalStateException("Please add the resources dir to the classpath");
    }
  }

  private static void storePaths() {
    App.put("home.maps.dir", MAPS_DIR);
    App.put("save.path", resourcesPath + "/save/");
    App.put("quick.save.game.file.name", "quick.save");
    App.put("gui.path", resourcesPath + "/data/gui/");
    App.put("userproperties.path", HOME_DIR + "/" + USER_PROPERTIES_FILE);
  }

  private static void initHomeDir() {
    createHomeDirs();
    createEmptyUserPropertyFileIfNonePresent();
  }

  private static void createHomeDirs() {
    IOUtil.mkDir(new File(HOME_DIR));
    IOUtil.mkDir(new File(MAPS_DIR));
  }

  private static void createEmptyUserPropertyFileIfNonePresent() {
    File userPropertiesFile = new File(HOME_DIR + "/" + USER_PROPERTIES_FILE);

    if (!userPropertiesFile.exists()) {
      IOUtil.createNewFile(userPropertiesFile, "Could not create empty user properties file");
    }
  }

  private void loadConfigFiles() {
    try {
      String configPath = resourcesPath + "/data/config";
      loadLog4JProperties(configPath);
      loadGameProperties(configPath);
      logger.info("CW2 version #" + App.get("game.version"));

      Properties userProperties = loadUserProperties(configPath);
      String language = userProperties.getProperty("user.lang");
      String pluginName = userProperties.getProperty("user.activeplugin");

      if (pluginName.equals("default")) {
        pluginName = "dor";
      }

      String pluginPath = resourcesPath + "/plugin/" + pluginName;

      loadLang(language);
      loadPluginProperties(pluginPath);
      initResourceManager(pluginPath);
      initScripts(pluginPath);
    } catch (IOException ex) {
      throw new RuntimeException("Error reading config file", ex);
    }
  }

  private static void loadLog4JProperties(String configPath) throws IOException {
    Properties log4JProperties = loadProperties(configPath + "/" + LOG_PROPERTIES_FILE);
    PropertyConfigurator.configure(log4JProperties);
  }

  private static void loadGameProperties(String configPath) throws IOException {
    Properties gameProperties = loadProperties(configPath + "/" + GAME_PROPERTIES_FILE);
    App.putAll(gameProperties);
  }

  private static Properties loadUserProperties(String configPath) throws IOException {
    Properties defaults = loadProperties(configPath + "/" + USER_DEFAULTS_PROPERTIES_FILE);
    Properties userProperties = loadProperties(HOME_DIR + "/" + USER_PROPERTIES_FILE, defaults);
    App.putAll(userProperties);
    return userProperties;
  }

  public void loadLang(String languageCode) throws IOException {
    if (!StringUtil.hasContent(languageCode)) {
      logger.warn("Language code not specified, default = english");
      languageCode = "EN";
    }

    Locale locale = new Locale(languageCode);
    loadLangProperties(locale, resourcesPath + "/data/lang/Languages");
  }

  private static void loadLangProperties(Locale locale, String languageBundlePath) {
    // Using our own ClassLoader that reads from a folder
    // default classloader just looks into the classpath
    ResourceBundle bundle = ResourceBundle.getBundle(languageBundlePath, locale, new IOUtil.URLClassLoader());
    App.setLocaleResourceBundle(bundle);
    logger.info("Lang=" + locale);
  }

  private static void loadPluginProperties(String pluginLocation) throws IOException {
    Properties pluginProperties = loadProperties(pluginLocation + "/data/" + PLUGIN_PROPERTIES_FILE);
    App.putAll(pluginProperties);
    logger.info("Plugin=" + pluginLocation);
  }

  private void initResourceManager(String pluginPath) {
    resources.putLoadPath("img.path", pluginPath + "/images/");
    resources.putLoadPath("cursorimg.path", pluginPath + "/images/cursors/");
    resources.putLoadPath("sound.path", pluginPath + "/sound/");
    resources.putLoadPath("data.path", pluginPath + "/data/");

    // Maps can be stored on 2 places
    // 1. The user can create maps and put them in his HOME_DIR
    // 2. Maps included within the release are in the RES_DIR
    resources.putLoadPath("map.path1", MAPS_DIR);
    resources.putLoadPath("map.path2", resourcesPath + "/maps/");
    resources.putLoadPath("font.path", pluginPath + "/fonts/");
    resources.setDarkPercentage(App.getInt("display.darkpercentage"));
  }

  private void initScripts(String pluginPath) {
    String coDir = pluginPath + "/data/co/";
    coScriptManager.init(coDir + MAIN_SCRIPT_FILE, coDir + CO_SCRIPT_FILE);
    COFactory.setScriptManager(coScriptManager);
  }

  private static Properties loadProperties(String location) throws IOException {
    InputStream in = ResourceLoader.getResourceAsStream(location);
    return IOUtil.loadProperties(in);
  }

  private static Properties loadProperties(String location, Properties defaults) throws IOException {
    InputStream in = ResourceLoader.getResourceAsStream(location);
    return IOUtil.loadProperties(in, defaults);
  }

  private void initBeanshellConsole() {
    BeanShell bsh = BeanShell.get();
    bsh.set("coScripts", coScriptManager);
    bsh.set("resources", resources);
    bsh.eval("setAccessibility(true)");
  }
}
