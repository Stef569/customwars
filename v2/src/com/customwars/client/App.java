package com.customwars.client;

import tools.Args;

import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Handles Application wide settings by Wrapping Properties functions like
 * get(String key, String def) into functions that return primitives. This reduces the line
 * Integer.parse(App.get("user.age", "10")) to App.getInt("user.age",10)
 *
 * @author stefan
 */
public class App {
  private static Properties properties = new Properties();
  private static ResourceBundle localeResourceBundle;

  public static void setProperties(Properties properties) {
    App.properties = properties;
  }

  public static void setLocaleResourceBundle(ResourceBundle bundle) {
    App.localeResourceBundle = bundle;
  }

  public static void put(String key, String value) {
    properties.put(key, value);
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
    } catch (NumberFormatException e) {
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
    } catch (NumberFormatException e) {
      // Ignoring exception causes specified default to be returned
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
      result = value.equalsIgnoreCase("true");
    }

    return result;
  }

  /**
   * Get a translated String for msg
   *
   * @param msg lower case key definded in the language properties file
   * @return msg translated to the current language
   */
  public static String getMsg(String msg) {
    return localeResourceBundle.getString(msg);
  }

  public static Properties getProperties() {
    return properties;
  }
}
