package com.customwars.client.model.map;

/**
 * The 8 Compass Directions
 * STILL means no direction
 *
 * @author Stefan
 */
public enum Direction {
  NORTH, EAST, SOUTH, WEST,
  NORTHEAST, SOUTHEAST, NORTHWEST, SOUTHWEST,
  STILL;

  /**
   * Try to get an enum constant based on it's String value eg.:
   * Direction north = getDirection("north")
   *
   * @return the enum constant or null if the enum was not found
   */
  public static Direction getDirection(String enumName) {
    return Direction.valueOf(enumName.trim().toUpperCase());
  }
}
