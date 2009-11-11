package com.customwars.client.model.map;

import com.customwars.client.tools.Args;

/**
 * Defines an inclusive range between a Min and Max value.
 * There is one pre condition min should be smaller then max
 */
public class Range {
  int minRange, maxRange;

  public Range(int minRange, int maxRange) {
    setRange(minRange, maxRange);
  }

  private void setRange(int min, int max) {
    Args.validate(min > max, "min range " + minRange + " cannot be smaller then the max range " + maxRange);
    this.minRange = min;
    this.maxRange = max;
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
    return minRange + " /" + maxRange;
  }
}
