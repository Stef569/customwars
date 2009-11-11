package com.customwars.client.tools;

/**
 * Common used String funtions
 *
 * @author stefan
 */
public class StringUtil {
  /**
   * This is a static utility class. It cannot be constructed.
   */
  private StringUtil() {
  }

  /**
   * Append the suffix suf to String in
   * when in does not end with the suffix already
   */
  public static String appendTrailingSuffix(String in, String suf) {
    if (!in.endsWith(suf)) {
      in += suf;
    }
    return in;
  }

  public static boolean hasContent(String in) {
    return in != null && in.trim().length() > 0;
  }

  public static String removeCharsFromEnd(String in, int charCount) {
    String txt = "";
    if (in.length() > 0) {
      txt = in.substring(0, in.length() - charCount);
    }
    return txt;
  }
}
