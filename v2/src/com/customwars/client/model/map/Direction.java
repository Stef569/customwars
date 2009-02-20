package com.customwars.client.model.map;

import java.util.Scanner;

/**
 * The 4 Compass Directions
 * STILL means no direction
 *
 * @author Stefan
 */
public enum Direction {
  NORTH, EAST, SOUTH, WEST, STILL;

  public static Direction getReverseDirection(Direction dir) {
    Direction reverseDir = null;
    switch (dir) {
      case EAST:
        reverseDir = Direction.WEST;
        break;
      case NORTH:
        reverseDir = Direction.SOUTH;
        break;
      case SOUTH:
        reverseDir = Direction.NORTH;
        break;
      case STILL:
        reverseDir = Direction.STILL;
        break;
      case WEST:
        reverseDir = Direction.EAST;
        break;
    }
    return reverseDir;
  }

  /**
   * Try to get an enum constant based on it's String value
   * both name and position values are supported.
   *
   * @param id the Identifier value in the enum array
   * @return the enum constant or null if the enum was not found
   */
  public static Direction getDirection(String id) {
    Direction type = null;
    if (id == null) {
      return null;
    }
    if (id.trim().length() == 0) {
      return null;
    }

    Scanner scanner = new Scanner(id);
    if (scanner.hasNext()) {
      if (scanner.hasNextInt()) { // By Numeric String
        type = Direction.values()[scanner.nextInt()];
      } else {                    // By String
        type = Direction.valueOf(id.trim().toUpperCase());
      }
    }
    return type;
  }
}
