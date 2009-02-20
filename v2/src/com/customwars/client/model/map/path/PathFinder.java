package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.TileMap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Pathfinder class that allows to build paths for Mover objects
 * it can build a movementZone around the mover
 * it can build a path from Location a to Location b
 * it uses the Dijkstra implementation.
 *
 * @author Benjamin Islip
 */
public class PathFinder {
  TileMap map;
  Dijkstra dijkstra;          // data
  Mover currentMover = null;  // remembering this saves an unnessary recalculation of Dijk0
  Location currentLocation = null;

  public PathFinder(TileMap map) {
    this.map = map;
    dijkstra = new Dijkstra(map.getCols(), map.getRows());
  }

  public PathFinder(TileMap map, MovementCost cost) {
    this(map);
    setMoveCost(cost);
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
   *         returns null if destination is out of reach.
   */
  public List<Location> getMovementPath(Mover mover, Location destination) {
    calculatePaths(mover);
    // test does not include the destination tile
    // And We just came from here.

    if (!dijkstra.canMoveTo(destination.getCol(), destination.getRow())) {
      return null;
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
   *         returns null if destination is out of reach.
   */
  public List<Direction> getDirections(Mover mover, Location destination) {
    if (mover == null) {
      throw new IllegalArgumentException("mover is null");
    }
    if (destination == null) {
      throw new IllegalArgumentException("destination is null");
    }
    calculatePaths(mover);

    if (!dijkstra.canMoveTo(destination.getCol(), destination.getRow())) {
      return null;
    }
    List<Direction> dirs = dijkstra.getPath(destination.getCol(), destination.getRow());
    List<Direction> directions = new ArrayList<Direction>(dirs.size());
    for (Direction d : dirs) {
      directions.add(d);
    }
    return directions;
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
      return;
      // only call the expensive calculate() method if required

      // IF selecting a new mover OR mover has moved THEN
      //    recalculate dijkstra
    }
    if (currentMover == null || mover != currentMover || currentLocation == null || mover.getLocation() != currentLocation) {
      currentMover = mover;
      currentLocation = mover.getLocation();

      int xLoc = currentMover.getLocation().getCol();
      int yLoc = currentMover.getLocation().getRow();
      int movement = currentMover.getMovement();

      dijkstra.calculate(xLoc, yLoc, movement);
    }
  }

  public void setMoveCost(MovementCost cost) {
    if (cost == null) throw new IllegalArgumentException("Movement cost is null");
    dijkstra.setMovementCosts(cost);
  }
}
