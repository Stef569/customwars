package com.customwars.client.model.map;

/**
 * The 4 Compass Directions
 * STILL means no direction
 *
 * @author Stefan
 * @since 1.0
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
}
