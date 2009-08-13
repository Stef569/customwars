package tools;

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
   * Append char c to String in
   * when it is not already the last char
   */
  public static String appendTrailingSuffix(String in, char c) {
    if (in.charAt(in.length() - 1) != c) in += c;
    return in;
  }

  public static boolean hasContent(String in) {
    return in != null && in.trim().length() > 0;
  }
}
