package com.customwars.client.io.loading;

import com.thoughtworks.xstream.core.util.Fields;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.spi.ThingleColor;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Load a color theme from a properties file
 * the keys are the var names from within the Theme class
 * the value is a RGB value in this format: r,g,b
 */
public class ThemeLoader {
  private Properties properties;
  private Theme theme;

  public Theme load(String propertyFilePath) {
    InputStream in = ResourceLoader.getResourceAsStream(propertyFilePath);
    this.properties = new Properties();
    this.theme = new Theme();
    try {
      properties.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    loadThemeProperties();
    return theme;
  }

  private void loadThemeProperties() {
    Enumeration propertyNames = properties.propertyNames();
    while (propertyNames.hasMoreElements()) {
      String themeProperty = (String) propertyNames.nextElement();
      addThemeColor(themeProperty);
    }
  }

  private void addThemeColor(String themeProperty) {
    String color = properties.getProperty(themeProperty);
    ThingleColor thingleColor = convertToThingleColor(color);

    try {
      Field field = Theme.class.getDeclaredField(themeProperty);
      Fields.write(field, theme, thingleColor);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private static ThingleColor convertToThingleColor(String colorValue) {
    String[] rgb = colorValue.split(",");
    int red = Integer.valueOf(rgb[0]);
    int green = Integer.valueOf(rgb[1]);
    int blue = Integer.valueOf(rgb[2]);
    return Thingle.createColor(red, green, blue);
  }
}
