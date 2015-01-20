package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.Args;
import org.apache.log4j.Logger;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pathfinder class that allows to build paths for Mover objects
 * it can build a movementZone around the mover
 * it can build a path from Location a to Location b
 * it uses the Dijkstra implementation.
 *
 * @author Benjamin Islip
 */
public class PathFinder implements MovementCost {
  private static final Logger logger = Logger.getLogger(PathFinder.class);
  Map map;
  Dijkstra dijkstra;          // data
  Mover currentMover;         // remembering this saves an unnecessary recalculation of Dijk0
  Location currentLocation;

  public PathFinder(Map map) {
    this.map = map;
    dijkstra = new Dijkstra(map.getCols(), map.getRows());
    dijkstra.setMovementCosts(this);
  }

  /**
   * Gets all the possible movement locations.
   *
   * @param mover that wants to see its movement zone.
   * @return a collection of tiles the mover can move too.
   */
  public List<Location> getMovementZone(Mover mover) {
    calculatePaths(mover, mover.getMovePoints());

    List<Point> points = dijkstra.getMoveZone();
    return pointsToTiles(points);
  }

  /**
   * Gets the locations a unit must traverse to reach a desired destination.
   *
   * @param mover       mover that wants to see a way to a tile.
   * @param destination tile the unit must reach.
   * @return a collection of tiles the unit must traverse to reach its goal.
   *         returns an empty list if destination is out of reach.
   */
  public List<Location> getMovementPath(Mover mover, Location destination) {
    calculatePaths(mover);

    // test does not include the destination tile
    // And We just came from here.
    if (!dijkstra.canMoveTo(destination.getCol(), destination.getRow())) {
      return Collections.emptyList();
    }
    List<Point> points = dijkstra.getRoute(destination.getCol(), destination.getRow());
    return pointsToTiles(points);
  }

  /**
   * Finds the shortest path towards a destination in the map.
   * The destination can be anywhere in the map but the mover must be able to traverse the destination.
   *
   * It prevents units from walking towards the shore to get to a
   * destination across the sea when a bridge 3 tiles away is available.
   *
   * @param mover       The mover to find the shortest path to the destination for
   * @param destination The destination to build a path to
   * @return The shortest path towards the destination
   */
  public List<Location> getShortestPath(Mover mover, Location destination) {
    int moveCost = mover.getMoveStrategy().getMoveCost(map.getTile(destination.getCol(), destination.getRow()));

    if (moveCost == Terrain.IMPASSIBLE) {
      logger.debug(String.format(
        "No route possible to destination %s for %s " + destination.getLocationString(), mover
      ));
      return Collections.emptyList();
    }

    // Using MAX VALUE ensures that we can reach the destination in the map
    calculatePaths(mover, Integer.MAX_VALUE);

    // Create path to the destination across the whole map
    List<Point> route = dijkstra.getRoute(destination.getCol(), destination.getRow());

    // Now trim the route, we actually can't move further then max movement of the mover
    List<Point> trimmedRoute = trimRoute(mover, route);

    // Remove the first Point
    // As this is the mover location
    trimmedRoute.remove(0);

    List<Location> path = pointsToTiles(trimmedRoute);

    if (!path.isEmpty()) {
      // Remove end locations with a unit on it
      trimLocationsWithUnitsOnThem(path);
    }

    return path;
  }

  private void trimLocationsWithUnitsOnThem(List<Location> path) {
    int locationsToRemove = 0;
    for (int i = path.size() - 1; i != 0; i--) {
      Location location = path.get(i);

      if (map.hasUnitOn(location)) {
        locationsToRemove++;
      } else {
        break;
      }
    }

    while (locationsToRemove > 0) {
      int lastIndex = path.size() - 1;
      path.remove(lastIndex);
      locationsToRemove--;
    }
  }

  private List<Point> trimRoute(Mover mover, List<Point> points) {
    int maxMovePoints = mover.getMovePoints();
    int usedMoveCosts = 0;
    List<Point> path = new ArrayList<Point>();

    for (Point p : points) {
      if (usedMoveCosts < maxMovePoints) {
        int moveCost = getMovementCost(p.x, p.y);
        usedMoveCosts += moveCost;
        path.add(p);
      }
    }
    return path;
  }

  /**
   * Convert the points collection to Locations within the map
   */
  private List<Location> pointsToTiles(List<Point> points) {
    List<Location> tiles = new ArrayList<Location>(points.size());

    for (Point p : points) {
      tiles.add(map.getTile(p.x, p.y));
    }
    return tiles;
  }

  /**
   * Gets the directions a unit must take to reach a desired destination.
   *
   * @param mover       that wants to see a way to a tile.
   * @param destination tile the mover must reach.
   * @return a collection of directions the mover must move to reach its goal.
   *         returns an empty list if destination is out of reach.
   */
  public List<Direction> getDirections(Mover mover, Location destination) {
    Args.checkForNull(mover, "mover is null");
    Args.checkForNull(destination, "destination is null");

    calculatePaths(mover);

    if (!dijkstra.canMoveTo(destination.getCol(), destination.getRow())) {
      return Collections.emptyList();
    }

    return dijkstra.getPath(destination.getCol(), destination.getRow());
  }

  /**
   * Find if a destination is reachable.
   *
   * @param mover       the movable object.
   * @param destination the desired location to move to.
   * @return true if destination is reachable.
   */
  public boolean canMoveTo(Mover mover, Location destination) {
    boolean canMove = false;
    if (mover != null && destination != null) {
      calculatePaths(mover);

      if (dijkstra.canMoveTo(destination.getCol(), destination.getRow())) {
        canMove = true;
      }
    }
    return canMove;
  }

  private void calculatePaths(Mover mover) {
    calculatePaths(mover, mover.getMovePoints());
  }

  private void calculatePaths(Mover mover, int maxMovePoints) {
    if (mover == null) {
      logger.warn("Mover is null");
      return;
    }

    // Only call the expensive calculate() method if required
    // IF selecting a new mover OR mover has moved THEN
    // recalculate dijkstra
    if (currentMover == null || mover != currentMover || currentLocation == null || mover.getLocation() != currentLocation) {
      currentMover = mover;
      currentLocation = mover.getLocation();

      int xLoc = currentLocation.getCol();
      int yLoc = currentLocation.getRow();

      dijkstra.calculate(xLoc, yLoc, maxMovePoints);
    }
  }

  /**
   * Delegate the move cost calculation to the current mover
   */
  public int getMovementCost(int x, int y) {
    if (currentMover != null)
      return currentMover.getMoveStrategy().getMoveCost(map.getTile(x, y));
    else
      throw new IllegalStateException("Mover needed to get the movecost");
  }
}
