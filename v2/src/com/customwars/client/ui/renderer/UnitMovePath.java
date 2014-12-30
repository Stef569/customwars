package com.customwars.client.ui.renderer;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

import java.util.LinkedList;
import java.util.List;

/**
 * Stores the path a unit can move over to reach a destination.
 * The path is created by moving the cursor away from the unit.
 */
public class UnitMovePath {
  private final Map map;
  private final int maxPathLength;
  private List<Direction> path;

  public UnitMovePath(Map map, int maxPathLength) {
    this.map = map;
    this.maxPathLength = maxPathLength;
    this.path = new LinkedList<Direction>();
  }

  public boolean canAddDirection(Unit activeUnit, Direction moveDirection, Location newLocation) {
    if (activeUnit.getLocation().equals(newLocation)) return true;
    boolean noMove = moveDirection == Direction.STILL;

    return !noMove && isInMoveZone(activeUnit, newLocation);
  }

  private boolean isInMoveZone(Unit activeUnit, Location newLocation) {
    if (activeUnit != null && activeUnit.getMoveZone() != null) {
      if (activeUnit.getMoveZone().contains(newLocation)) {
        return true;
      }
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
  }

  /**
   * Check if the move path needs be shrinked
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
  }

  /**
   * @return The direction path that a unit can move over to reach his destination.
   */
  public List<Direction> getDirectionsPath() {
    return path;
  }
}
