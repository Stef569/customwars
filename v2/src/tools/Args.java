package tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on http://www.javapractices.com
 * validate methods throw an <tt>IllegalArgumentException<tt> when the input was not valid
 * get methods return a value within bounds
 *
 * @author stefan
 */
public final class Args {

  /**
   * This is a static utility class. It cannot be constructed.
   */
  private Args() {
  }

  public static void checkForNull(Object obj) {
    checkForNull(obj, "Null");
  }

  public static void checkForContent(String text) {
    checkForContent(text, "Empty String");
  }

  public static void checkForNull(Object obj, String errMsg) {
    if (obj == null) {
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void checkForContent(String text, String errMsg) {
    checkForNull(text, errMsg);
    if (text.trim().length() < 0) {
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void validate(boolean condition, String err) {
    if (condition) throw new IllegalArgumentException(err);
  }

  public static void validateBetweenZeroMax(int amount, int maxAmount, String errField) {
    validateBetweenMinMax(amount, 0, maxAmount, errField);
  }

  public static void validateBetweenMinMax(int amount, int min, int max, String errField) {
    if (!isWithinRange(amount, min, max)) {
      throw new IllegalArgumentException(errField + " " + amount + " is not in range >0 <" + max);
    }
  }

  private static boolean isWithinRange(int amount, int minAmount, int maxAmount) {
    if (minAmount > maxAmount) {
      throw new IllegalArgumentException("minAmount bigger then maxAmount");
    }

    return amount >= minAmount && amount < maxAmount;
  }

  /**
   * PostCondition:
   * if amount >= maxamount, return maxAmount
   * if amount <= 0, return 0
   */
  public static int getBetweenZeroMax(int amount, int maxAmount) {
    return getBetweenZeroMax(amount, maxAmount, maxAmount);
  }

  /**
   * PostCondition:
   * if amount >= maxamount, return retAmountWhenMax
   * if amount <= 0, return 0
   */
  public static int getBetweenZeroMax(int amount, int maxAmount, int retAmountWhenMax) {
    return getBetweenMinMax(amount, 0, maxAmount, retAmountWhenMax);
  }

  /**
   * Precondition: minAmount < maxAmount
   * PostCondition:
   * if amount >= maxamount, return retAmountWhenMax
   * if amount <= minamount, return 0
   */
  public static int getBetweenMinMax(int amount, int minAmount, int maxAmount, int retAmountWhenMax) {
    int val = getBetweenMinMax(amount, minAmount, maxAmount);
    if (val == maxAmount) {
      return retAmountWhenMax;
    } else {
      return val;
    }
  }

  /**
   * Precondition: minAmount < maxAmount
   * PostCondition:
   * if amount >= maxamount, return maxAmount
   * if amount <= minamount, return minAmount
   *
   * @return an amount between minAmount and maxAmount
   */
  public static int getBetweenMinMax(int amount, int minAmount, int maxAmount) {
    if (minAmount > maxAmount) {
      throw new IllegalArgumentException("minAmount bigger then maxAmount");
    }

    int result;
    if (amount >= maxAmount) {
      result = maxAmount;
    } else if (amount < minAmount) {
      result = minAmount;
    } else {
      result = amount;
    }
    return result;
  }

  public static <T> List<T> createEmptyListIfNull(List<T> lst) {
    return lst == null ? new ArrayList<T>() : lst;
  }
}
