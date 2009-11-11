package com.customwars.client.tools;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Util functions to work with Colors
 * it stored a colorMap that maps color names to a Color object. ie
 * "Blue" -> Color.Blue
 * When the class is loaded we fill colorMap with the colors from the Color object.
 *
 * @author Stefan
 */
public final class ColorUtil {
  private static final Map<String, Color> colorMap = buildNameToColorMap();

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private ColorUtil() {
  }

  private static Map<String, Color> buildNameToColorMap() {
    Map<String, Color> colorMap = new HashMap<String, Color>();
    Field[] fields = Color.class.getDeclaredFields();
    for (Field field : fields) {
      if (isConstantColorField(field)) {
        try {
          Color color = (Color) field.get(null);
          colorMap.put(field.getName().toUpperCase(), color);
        }
        catch (IllegalAccessException ex) {
          throw new AssertionError(ex.getMessage() + " Should not occur because the field is public and static");
        }
      }
    }
    return colorMap;
  }

  private static boolean isConstantColorField(Field field) {
    return Modifier.isPublic(field.getModifiers())
      && Modifier.isStatic(field.getModifiers())
      && Color.class == field.getType();
  }

  public static List<Color> getColorListFromHex(List<Integer> intList) {
    List<Color> colorList = new ArrayList<Color>();
    for (Integer intVal : intList) {
      colorList.add(new Color(intVal));
    }
    return colorList;
  }

  public static Color toColor(String name) {
    String key = name.toUpperCase();
    if (!colorMap.containsKey(key)) {
      throw new IllegalArgumentException("ColorMap does not contain " + key);
    }
    return colorMap.get(key);
  }

  public static String toString(Color color) {
    String colorName = null;

    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
      if (entry.getValue().getRGB() == color.getRGB()) {
        colorName = entry.getKey();
        break;
      }
    }
    return colorName;
  }

  public static List<Color> toColorList(String colors, String delimiter) {
    List<Color> colorList = new ArrayList<Color>();
    Scanner scanner = new Scanner(colors);
    scanner.useDelimiter(delimiter);
    while (scanner.hasNext()) {
      Color c = toColor(scanner.next());
      colorList.add(c);
    }
    return colorList;
  }

  /**
   * Get A Color object from a String
   * if it is hex create a color from it
   * else looks for a defined Name in the colorMap
   * else return null
   *
   * @param color a numeric hex value or a color name
   */
  public static Color getColorFromText(String color) {
    if (colorMap.containsKey(color)) {
      return toColor(color);
    }

    try {
      int hex = Integer.parseInt(color, 16);
      return new Color(hex);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  /**
   * Allows to add 1 color to be mapped to a color name to the colorMap
   * Precondition:    colorName or color has not yet been defined in colorMap
   * Postcondition:   toString(color) returns colorName
   * toColor(colorName) returns color
   *
   * @param colorName The unique name of the color
   * @param color     The color to map to the colorName
   */
  public static void addColor(String colorName, Color color) {
    if (colorMap.containsKey(colorName) && colorMap.containsValue(color)) {
      throw new IllegalArgumentException("The colorMap already contains this name/color pair");
    }
    colorMap.put(colorName, color);
  }

  /**
   * Convert a java.awt.Color to an org.newdawn.slick.Color
   *
   * @param color awt Color
   * @return Slick Color
   */
  public static org.newdawn.slick.Color convertToSlickColor(Color color) {
    int rgb = color.getRGB();
    return new org.newdawn.slick.Color(rgb);
  }
}
