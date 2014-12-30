package com.customwars.client.model.map;

/**
 * The 8 Compass Directions
 * STILL means no direction
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

  public static boolean isNorthQuadrant(Direction quadrant) {
    return quadrant == Direction.NORTHEAST || quadrant == Direction.NORTHWEST;
  }

  public static boolean isEastQuadrant(Direction quadrant) {
    return quadrant == Direction.NORTHEAST || quadrant == Direction.SOUTHEAST;
  }

  public static boolean isSouthQuadrant(Direction quadrant) {
    return quadrant == Direction.SOUTHEAST || quadrant == Direction.SOUTHWEST;
  }

  public static boolean isWestQuadrant(Direction quadrant) {
    return quadrant == Direction.NORTHWEST || quadrant == Direction.SOUTHWEST;
  }

  /**
   * Check if 2 directions are the opposite of each other
   */
  public static boolean isOpposite(Direction direction1, Direction direction2) {
    return direction1 == getOpposite(direction2);
  }

  /**
   * Returns the opposite direction of the given direction.
   * <code>Direction North = getOpposite(Direction.SOUTH)</code>
   *
   * @param direction the direction to find the opposite for
   * @return the opposite direction, the opposite of STILL return STILL
   */
  public static Direction getOpposite(Direction direction) {
    switch (direction) {
      case NORTH:
        return Direction.SOUTH;
      case EAST:
        return Direction.WEST;
      case SOUTH:
        return Direction.NORTH;
      case WEST:
        return Direction.EAST;
      case NORTHEAST:
        return Direction.SOUTHWEST;
      case SOUTHEAST:
        return Direction.NORTHWEST;
      case NORTHWEST:
        return Direction.SOUTHEAST;
      case SOUTHWEST:
        return Direction.NORTHEAST;
      case STILL:
        return Direction.STILL;
    }
    throw new IllegalStateException("No opposite for " + direction);
  }
}
