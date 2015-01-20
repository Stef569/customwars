package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.Args;
import org.apache.log4j.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Allows Movers to traverse within a TileMap
 *
 * Usage:
 * moveTraverse.prepareMove(mover, destination)
 * Then repeatedly call moveTraverse.update() to do 1 step in the path
 *
 * There can only be one mover moving through the map at any time.
 * The pathFinder creates the shortest path from the mover location to the destination.
 * On each move in the path the move cost is subtracted from the mover.
 *
 * When the move is completed caused by: path blocked by a trapper, destination reached or mover can no longer move
 * {@link #isPathMoveComplete()} will return true
 *
 * if a trapper was found within the path then
 * {@link #foundTrapper()} and {@link #isPathMoveComplete()} return true.
 */
public class MoveTraverse {
  private static final Logger logger = Logger.getLogger(MoveTraverse.class);
  private final PropertyChangeSupport changeSupport;
  private final Map map;                    // The map the mover can move in
  private final PathFinder pathFinder;      // Path generator
  private List<Location> movePath;    // The path we are going to move over
  private Mover mover;                // Mover that is moving or is ready to move through the movePath

  private int pathIndex;              // The current position in the move path
  private boolean pathMoveComplete;   // Indicates that the move has been completed (trapped or successful)
  private boolean foundTrapper;       // Indicates that a trapper was found on the movepath

  public MoveTraverse(Map map) {
    this.map = map;
    pathFinder = new PathFinder(map);
    changeSupport = new PropertyChangeSupport(this);
  }

  /**
   * Prepares a move, the movePath defines the locations the unit must follow to reach it's destination.
   *
   * @param mover    The mover that wants to move
   * @param movePath The path that the mover must traverse
   */
  public void prepareMove(Mover mover, List<Location> movePath) {
    Args.checkForNull(mover);
    Args.checkForNull(movePath);

    prepareForNextMove();
    this.mover = mover;
    this.movePath = movePath;
  }

  /**
   * Prepares a move from the mover location to the destination.
   *
   * The pathfinder will find the shortest route towards the destination.
   *
   * If the destination is out of reach, a warning is logged and the unit is 'trapped'
   *
   * @param mover       The mover that wants to move
   * @param destination the destination to reach
   */
  public void prepareMove(Mover mover, Location destination) {
    Args.checkForNull(mover);
    Args.checkForNull(destination);

    prepareForNextMove();
    this.mover = mover;

    if (pathFinder.canMoveTo(mover, destination)) {
      this.movePath = pathFinder.getMovementPath(mover, destination);
    } else {
      logger.warn("Could not prepare move" +
        " from " + mover.getLocation().getLocationString() +
        " to " + destination.getLocationString() +
        " for " + mover);

      setPathMoveComplete();
      setTrapped();
    }
  }

  /**
   * Cleans up the values from a previous move
   */
  private void prepareForNextMove() {
    mover = null;
    movePath = null;
    pathIndex = 0;
    pathMoveComplete = false;
    foundTrapper = false;
  }

  /**
   * Updates the map position and orientation of the mover By moving one step further in the movePath
   * movePath and mover are set by {@link #prepareMove(Mover, Location)}
   * if the path is already completed or the movePath has not been generated then nothing happens.
   */
  public void update() {
    if (pathMoveComplete || movePath == null) return;

    if (canMoveFurther()) {
      moveToNextLocation();
    } else {
      pathMoveComplete();
    }
  }

  private boolean canMoveFurther() {
    return withinPath(pathIndex) && withinPath(pathIndex + 1) &&
      mover.canMove() && !trapperOnNextLocation();
  }

  private boolean trapperOnNextLocation() {
    if (withinPath(pathIndex + 1)) {
      Location nextLocation = movePath.get(pathIndex + 1);
      return mover.hasTrapperOn(nextLocation);
    } else {
      return false;
    }
  }

  private void moveToNextLocation() {
    Tile currentLocation = (Tile) movePath.get(pathIndex);
    Tile nextLocation = (Tile) movePath.get(pathIndex + 1);
    move(currentLocation, nextLocation);
    addPathMoveCost(nextLocation);
    pathIndex++;
  }

  private void move(Tile from, Tile to) {
    mover.setOrientation(map.getDirectionTo(from, to));
    map.teleport(from, to, mover);
  }

  private void addPathMoveCost(Tile nextLocation) {
    int moveCost = getMoveCost(nextLocation);
    mover.addPathMoveCost(moveCost);
  }

  private int getMoveCost(Tile location) {
    int movementType = mover.getMovementType();
    return location.getTerrain().getMoveCost(movementType);
  }

  private void pathMoveComplete() {
    if (trapperOnNextLocation()) {
      setTrapped();
    }
    setPathMoveComplete();
    revealAdjacentTilesAlongMovepath();
  }

  /**
   * @return if index is within the movePath bounds, 0 based
   */
  private boolean withinPath(int index) {
    return index >= 0 && index <= movePath.size() - 1;
  }

  private void setTrapped() {
    this.foundTrapper = true;
    changeSupport.firePropertyChange("trapped", false, true);
  }

  private void setPathMoveComplete() {
    pathMoveComplete = true;
    changeSupport.firePropertyChange("pathMoveComplete", false, true);
  }

  private void revealAdjacentTilesAlongMovepath() {
    for (Location location : movePath) {
      for (Tile adjacentTile : map.getSurroundingTiles(location, 1, 1)) {
        adjacentTile.setFogged(false);
      }
    }
  }

  public boolean isPathMoveComplete() {
    return pathMoveComplete;
  }

  public boolean foundTrapper() {
    return foundTrapper;
  }

  public Location getTrapperLocation() {
    Location moverLocation = mover.getLocation();
    Direction nextDirection = getNextDirection();
    return map.getRelativeTile(moverLocation, nextDirection);
  }

  private Direction getNextDirection() {
    if (movePath == null) {
      return Direction.STILL;
    } else {
      Location location = movePath.get(pathIndex);
      Location nextLocation = movePath.get(pathIndex + 1);

      return map.getDirectionTo(location, nextLocation);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }
}
