package com.customwars.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Contains reusable functions to handle common GUI tasks
 *
 * @author stefan
 */
public final class GuiUtil {

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private GuiUtil() {
  }

  /**
   * Convenient method that wraps the convertToMultiLine method to return an array of strings
   *
   * @see #convertToMultiLine
   */
  public static String[] convertToMultiLineArray(String text, int maxPixelWidth, Graphics g) {
    String adjustedTxt = convertToMultiLine(text, maxPixelWidth, g);
    String[] strings = adjustedTxt.split("\n");
    return strings;
  }

  /**
   * Split text into mutliple lines where 1 line always fits the maxPixelWidth
   * lines are separated by the \n char
   */
  public static String convertToMultiLine(String text, int maxPixelWidth, Graphics g) {
    StringBuffer sb = new StringBuffer();
    int charPos = 0;        // Current position within the string
    int startPos = 0;       // Start of the last line found
    int lineCharWidth = 0; // Total width of the chars read in the for loop, reset when a line is found
    int totalWidth = getStringWidth(text, g);

    // The text is smaller then the maxPixelWidth, return it!
    if (totalWidth < maxPixelWidth) {
      return text;
    }

    // For each char in the string
    // Get the width of the char
    // Always read 1 ahead, Example: When charPos=0, lineCharWidth = 10
    // Because we always read 1 char ahead we have to move 1 char position back when a line has been found
    for (; charPos < text.length(); charPos++) {
      String oneChar = text.substring(charPos, charPos + 1);
      lineCharWidth += getStringWidth(oneChar, g);

      // If the total chars width has gone over the max width
      // extract one line from the text
      if (lineCharWidth > maxPixelWidth) {
        String oneLine = text.substring(startPos, charPos);
        startPos = charPos;
        lineCharWidth = 0;
        charPos--;

        sb.append(oneLine);
        sb.append("\n");
      }
    }

    // At this point every line that fits the maxPixelWidth has been extracted and put into the string buffer
    // Check if there is still a line smaller then the maxPixelWidth
    if (startPos < charPos) {
      sb.append(text.substring(startPos, charPos));
      startPos = charPos;
    }

    return sb.toString();
  }

  /**
   * @return a string that fits the max Pixel Width
   *         text that is larger then maxPixelWidth is discarted
   */
  public static String fitLine(String text, int maxPixelWidth, Graphics g) {
    int stringPixelWidth = getStringWidth(text, g);

    if (text == null) {
      throw new IllegalArgumentException("text is null");
    }
    if (g == null) {
      throw new IllegalArgumentException("graphics is null");
    }

    if (stringPixelWidth > maxPixelWidth) {
      stringPixelWidth = maxPixelWidth;
    }

    return getSubStringByPixelWidth(text, stringPixelWidth, 0, g);
  }

  /**
   * @param text       The source text
   * @param maxPxWidth The width the text should fit in (in pixels)
   * @param offSet     If the text fits the maxPxWidth, where should we start to grab the substring
   * @param g          graphical context containing the current Font
   * @return the substring of text with a width of maxPxWidth pixels starting at offSet
   *         if text is smaller then maxPxWidth then text is returned
   */
  private static String getSubStringByPixelWidth(String text, int maxPxWidth, int offSet, Graphics g) {
    // Loop through each char
    for (int charPos = 0; charPos < text.length(); charPos++) {
      // Get char size in pixels
      int charsPxWidth = getStringWidth(text, 0, charPos + 1, g);

      // See if it goes over the maxPxWidth
      if (charsPxWidth >= maxPxWidth) {
        return text.substring(offSet, offSet + charPos);
      }
    }
    // The text is not bigger then maxPxWidth
    return text;
  }

  private static int getStringWidth(String text, int beginIndex, int limit, Graphics g) {
    FontMetrics metrics = g.getFontMetrics();

    // Get char size in pixels
    Rectangle2D txtRect = metrics.getStringBounds(text, beginIndex, limit, g);
    return (int) txtRect.getWidth();
  }

  public static int getStringWidth(String text, Graphics g) {
    FontMetrics metrics = g.getFontMetrics();
    Rectangle2D txtRect = metrics.getStringBounds(text, g);
    return (int) txtRect.getWidth();
  }
}
