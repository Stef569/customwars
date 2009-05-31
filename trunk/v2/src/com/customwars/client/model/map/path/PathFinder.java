package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import tools.Args;

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
  TileMap map;
  Dijkstra dijkstra;          // data
  Mover currentMover = null;  // remembering this saves an unnessary recalculation of Dijk0
  Location currentLocation = null;

  public PathFinder(TileMap map) {
    this.map = map;
    dijkstra = new Dijkstra(map.getCols(), map.getRows());
    dijkstra.setMovementCosts(this);
  }

  /**
   * Gets all the possible movement locations.
   *
   * @param mover that wants to see its movement zone.
   * @return a collection on tiles the mover can move too.
   */
  public List<Location> getMovementZone(Mover mover) {
    calculatePaths(mover);

    List<Point> points = dijkstra.getMoveZone();
    List<Location> tiles = new ArrayList<Location>(points.size());
    for (Point p : points) {
      tiles.add(map.getTile(p.x, p.y));
    }
    return tiles;
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
    if (mover == null) {
      logger.warn("Mover is null");
      return;

      // Only call the expensive calculate() method if required
      // IF selecting a new mover OR mover has moved THEN
      //    recalculate dijkstra
    }

    if (currentMover == null || mover != currentMover || currentLocation == null || mover.getLocation() != currentLocation) {
      currentMover = mover;
      currentLocation = mover.getLocation();

      int xLoc = currentLocation.getCol();
      int yLoc = currentLocation.getRow();
      int movement = currentMover.getMovePoints();

      dijkstra.calculate(xLoc, yLoc, movement);
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