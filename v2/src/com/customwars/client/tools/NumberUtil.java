package com.customwars.client.tools;

public final class NumberUtil {
  /**
   * This is a static utility class. It cannot be constructed.
   */
  private NumberUtil() {
  }

  public static int findHighest(int... values) {
    int highest = 0;

    for (int val : values) {
      if (val > highest) highest = val;
    }
    return highest;
  }

  public static int calcPercentage(int val, int max) {
    int percentage;
    if (max <= 0) {
      percentage = 0;
    } else {
      double divide = (double) val / max;
      percentage = (int) Math.round(divide * 100);
    }
    return percentage;
  }
}
