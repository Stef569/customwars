package com.customwars.client;

import com.customwars.client.tools.Args;
import com.customwars.client.tools.ColorUtil;

import java.awt.Color;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Handles Application wide settings by Wrapping System properties functions like
 * <code>System.get(key, default_value)</code> into functions that return primitives.
 *
 * This reduces the line <code>Integer.parse(System.get("user.age", "10"))</code>
 * to <code>App.getInt("user.age",10)</code>
 *
 * The default value is optional, when it is not provided a sensible default is returned
 * getString  -> ""
 * getInt     -> 0
 * getDouble  -> 0.0
 * getBoolean -> false
 * getColor   -> null
 *
 * @author stefan
 */
public class App {
  private static final Properties properties = new Properties();
  private static ResourceBundle localeResourceBundle;
  private static GAME_MODE gameMode = GAME_MODE.SINGLE_PLAYER;

  public enum GAME_MODE {
    SINGLE_PLAYER, NETWORK_SNAIL_GAME, LOAD_SAVED_GAME
  }

  /**
   * Set the locale resource bundle, this is used to translate text
   * See the translate method
   *
   * @param bundle resource bundle that contains key-value pairs
   */
  public static void setLocaleResourceBundle(ResourceBundle bundle) {
    App.localeResourceBundle = bundle;
  }

  public static void put(String key, String value) {
    properties.setProperty(key, value);
  }

  public static String get(String key) {
    return get(key, "");
  }

  public static String get(String key, String def) {
    Args.checkForContent(key, "Invalid key");
    String result = properties.getProperty(key, def);
    return (result == null ? def : result);
  }

  public static double getDouble(String key) {
    return getInt(key, 0);
  }

  public static double getDouble(String key, double def) {
    double result = def;
    try {
      String value = get(key, null);
      if (value != null) {
        result = Double.parseDouble(value);
      }
    } catch (NumberFormatException ignore) {
      // Ignoring exception causes specified default to be returned
    }
    return result;
  }

  public static int getInt(String key) {
    return getInt(key, 0);
  }

  public static int getInt(String key, int def) {
    int result = def;
    try {
      String value = get(key, null);
      if (value != null) {
        result = Integer.parseInt(value);
      }
    } catch (NumberFormatException ignore) {
      // Ignoring exception causes specified default to be returned
    }
    return result;
  }

  public static Color getColor(String key) {
    return getColor(key, null);
  }

  public static Color getColor(String key, Color def) {
    Color result = def;
    String value = get(key, null);
    if (value != null) {
      result = ColorUtil.toColor(value);
    }
    return result;
  }

  public static boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  public static boolean getBoolean(String key, boolean def) {
    boolean result = def;
    String value = get(key, null);
    if (value != null) {
      result = Boolean.parseBoolean(value);
    }

    return result;
  }

  /**
   * Get a translated String for msg
   *
   * @param msg lower case key defined in the language properties file
   * @return msg translated to the current language
   */
  public static String translate(String msg) {
    return localeResourceBundle.getString(msg);
  }

  /**
   * Put all the key-value pairs from the given properties into this properties
   * including default values. Duplicate keys are overwritten.
   *
   * @param properties The properties to add to this properties
   */
  public static void putAll(Properties properties) {
    for (String key : properties.stringPropertyNames()) {
      String val = properties.getProperty(key);
      App.properties.setProperty(key, val);
    }
  }

  /**
   * @return All the properties that can be changed by the user
   */
  public static Properties getUserProperties() {
    Properties userProperties = new Properties();

    for (String key : properties.stringPropertyNames()) {
      if (key.startsWith("user.")) {
        String val = properties.getProperty(key);
        userProperties.setProperty(key, val);
      }
    }
    return userProperties;
  }

  public static boolean isSinglePlayerGame() {
    return gameMode == GAME_MODE.SINGLE_PLAYER;
  }

  public static boolean isMultiplayerSnailGame() {
    return gameMode == GAME_MODE.NETWORK_SNAIL_GAME;
  }

  public static void changeGameMode(GAME_MODE newGameMode) {
    gameMode = newGameMode;
  }

  public static GAME_MODE getGameMode() {
    return gameMode;
  }
}
