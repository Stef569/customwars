package com.customwars.client.ui.renderer;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores the path a unit can move over to reach a destination.
 * The path is created by moving the cursor away from the unit.
 */
public class UnitMovePath {
  private static final Logger logger = Logger.getLogger(UnitMovePath.class);
  private final Map map;
  private final int maxPathLength;
  private List<Direction> path;
  private Location arrowHeadLocation;

  public UnitMovePath(Map map, int maxPathLength) {
    this.map = map;
    this.maxPathLength = maxPathLength;
    this.path = new LinkedList<Direction>();
  }

  public boolean canAddDirection(Unit activeUnit, Location oldLocation, Direction moveDirection, Location newLocation) {
    if (activeUnit.getLocation().equals(newLocation)) return true;
    boolean adjacent = TileMap.isAdjacent(oldLocation, newLocation);
    boolean adjacentOfArrow = isAdjacentOfArrow(newLocation);
    boolean inMoveZone = isInZone(activeUnit, newLocation);
    boolean noMoveDirection = moveDirection == Direction.STILL;

    return adjacent && adjacentOfArrow && inMoveZone && !noMoveDirection;
  }

  private boolean isInZone(Unit activeUnit, Location newLocation) {
    if (activeUnit != null) {
      List<Location> moveZone = activeUnit.getMoveZone();
      return moveZone != null && moveZone.contains(newLocation);
    }

    return false;
  }

  public void addDirection(Unit unit, Direction direction, Location moveLocation) {
    boolean shrinked = checkForShrink(direction);

    if (!shrinked) {
      if (path.size() == maxPathLength) {
        createShortestPath(unit, moveLocation);
      } else {
        path.add(direction);
      }
    }

    arrowHeadLocation = moveLocation;
  }

  /**
   * Check if the move path needs be shortened
   */
  private boolean checkForShrink(Direction direction) {
    if (!path.isEmpty()) {
      Direction lastDirection = getLastDirection();

      if (Direction.getOpposite(direction) == lastDirection) {
        removeLastDirection();
        return true;
      }
    }
    return false;
  }

  private Direction getLastDirection() {
    return path.get(path.size() - 1);
  }

  private void removeLastDirection() {
    path.remove(path.size() - 1);
  }

  public void createShortestPath(Unit activeUnit, Location moveLocation) {
    path = map.getDirectionsPath(activeUnit, moveLocation);

    if (path.isEmpty()) {
      // When no path can be created an empty immutable path is returned
      // If this is the case make sure we can modify the path in the future
      path = new ArrayList<Direction>();
    }

    arrowHeadLocation = moveLocation;
  }

  /**
   * Check if the location is adjacent of the arrow head.
   * If there is no path false is returned.
   */
  public boolean isAdjacentOfArrow(Location location) {
    return !path.isEmpty() && arrowHeadLocation != null && TileMap.isAdjacent(arrowHeadLocation, location);
  }

  /**
   * @return The direction path that a unit can move over to reach his destination.
   */
  public List<Direction> getDirectionsPath() {
    return path;
  }
}
