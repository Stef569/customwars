package com.customwars.client.model.map;

import com.customwars.client.tools.Args;

import java.io.Serializable;

/**
 * Defines an inclusive range between a Min and Max value.
 * There is one pre condition min should be smaller then max
 */
public class Range implements Serializable {
  public static final Range ZERO_RANGE = new Range(0, 0);
  private static final long serialVersionUID = 1L;
  private final int minRange, maxRange;

  public Range(int minRange, int maxRange) {
    Args.validate(minRange > maxRange, "min range " + minRange + " cannot be smaller then the max range " + maxRange);
    this.minRange = minRange;
    this.maxRange = maxRange;
  }

  public int getMinRange() {
    return minRange;
  }

  public int getMaxRange() {
    return maxRange;
  }

  /**
   * @return True if x is within the min(inclusive) and max(inclusive) range
   */
  public boolean isInRange(int x) {
    return x >= minRange && x <= maxRange;
  }

  @Override
  public String toString() {
    return minRange + "/" + maxRange;
  }
}
