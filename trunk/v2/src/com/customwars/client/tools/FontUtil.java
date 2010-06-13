package com.customwars.client.tools;

import org.newdawn.slick.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for Slick fonts
 */
public final class FontUtil {
  /**
   * This is a static utility class. It cannot be constructed.
   */
  private FontUtil() {
  }

  /**
   * Convenient method that wraps the text and return the results as an array of strings
   *
   * @see #wrap
   */
  public static String[] wrapToArray(String text, int maxWidth, Font font) {
    String adjustedTxt = wrap(text, maxWidth, font);
    return adjustedTxt.split("\n");
  }

  /**
   * Wrap text into multiple lines where all line widths are smaller then the maxWidth.
   * If the text contains spaces then only split on a space instead of in the middle of a word.
   * lines are separated by the '\n' char.
   */
  public static String wrap(String text, int maxWidth, Font font) {
    StringBuilder sb = new StringBuilder(text.length() + text.length() * 2);
    int totalWidth = font.getWidth(text);

    if (totalWidth < maxWidth) {
      return text;
    }

    int lineStartIndex = 0;
    int charIndex = 0;

    while (charIndex < text.length()) {
      String nextLine = text.substring(lineStartIndex, charIndex + 1);

      if (font.getWidth(nextLine) > maxWidth) {
        if (text.contains(" ")) {
          charIndex = getNearestSpace(text, charIndex, maxWidth / 2);
        }

        String line = text.substring(lineStartIndex, charIndex);
        sb.append(line);
        sb.append("\n");
        lineStartIndex = charIndex;
      }
      charIndex++;
    }

    if (lineStartIndex < text.length()) {
      sb.append(text.substring(lineStartIndex));
    }

    return sb.toString();
  }

  private static int getNearestSpace(String text, int nearCharIndex, int amountOfCharsToSearchBackwards) {
    // Search backwards for a ' ' char
    for (int charIndex = nearCharIndex; charIndex > nearCharIndex - amountOfCharsToSearchBackwards; charIndex--) {
      if (text.charAt(charIndex) == ' ') {
        return charIndex + 1;
      }
    }

    // No space has been found in the amountOfCharsToSearchBackwards range.
    // return the char index in the middle of the word.
    return nearCharIndex;
  }

  public static int[] getStringLenghts(List<String> lines, Font font) {
    List<Integer> lengths = new ArrayList<Integer>();
    for (String line : lines) {
      int width = font.getWidth(line);
      lengths.add(width);
    }

    return convertToIntArray(lengths);
  }

  private static int[] convertToIntArray(List<Integer> ints) {
    int[] primitiveIntArray = new int[ints.size()];
    for (int i = 0; i < ints.size(); i++) {
      primitiveIntArray[i] = ints.get(i);
    }
    return primitiveIntArray;
  }
}

