package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import tools.Args;

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
 *
 * @author stefan
 */
public class MoveTraverse {
  private static final Logger logger = Logger.getLogger(MoveTraverse.class);
  private PropertyChangeSupport changeSupport;
  private TileMap<Tile> map;          // The map the mover can move in
  private PathFinder pathFinder;      // Path generator
  private List<Location> movePath;    // The path we are going to move over
  private Mover mover;                // Mover that is moving or is ready to move through the movePath

  private int pathIndex;              // The current position in the move path
  private boolean pathMoveComplete;
  private boolean foundTrapper;       // Indicates that a trapper was found on the movepath
  private Direction trappedDirection; // if a trapper was found, in what direction relative to the mover was it.

  public MoveTraverse(TileMap<Tile> map) {
    this.map = map;
    pathFinder = new PathFinder(map);
    changeSupport = new PropertyChangeSupport(this);
  }

  public void prepareMove(Mover mover, Location destination) {
    Args.checkForNull(mover);
    Args.checkForNull(destination);

    prepareForNextMove();
    this.mover = mover;

    if (pathFinder.canMoveTo(mover, destination)) {
      this.movePath = pathFinder.getMovementPath(mover, destination);
    } else {
      logger.warn("Could not prepare move" +
              " from " + mover.getLocation().getCol() + "," + mover.getLocation().getRow() +
              " to " + destination.getCol() + "," + destination.getRow() +
              " for " + mover);

      pathMoveComplete = true;
      setTrapped(true);
      trappedDirection = map.getDirectionTo(mover.getLocation(), destination);
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
    setTrapped(false);
    trappedDirection = Direction.STILL;
  }

  /**
   * Updates the map position and orientation of the mover By moving one step further in the movePath
   * movePath and mover are set by {@link #prepareMove(Mover,Location)}
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
    return mover.canMove() && withinPath(pathIndex) && withinPath(pathIndex + 1) &&
            !trapperOnNextLocation();
  }

  private boolean trapperOnNextLocation() {
    Location nextLocation = movePath.get(pathIndex + 1);
    return mover.hasTrapperOn(nextLocation);
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
    if (trapperFound()) {
      setTrapped(true);
      trappedDirection = getNextDirection();
    }

    pathMoveComplete = true;
    changeSupport.firePropertyChange("pathMoveComplete", false, true);
  }

  private boolean trapperFound() {
    Location trappedLocation = movePath.get(pathIndex);
    return mover.hasTrapperOn(trappedLocation);
  }

  private Direction getNextDirection() {
    Location location = movePath.get(pathIndex);
    Location nextLocation = movePath.get(pathIndex + 1);

    return map.getDirectionTo(location, nextLocation);
  }

  private void setTrapped(boolean foundTrapper) {
    this.foundTrapper = foundTrapper;
    changeSupport.firePropertyChange("trapped", null, foundTrapper);
  }

  /**
   * @return if index is within the movePath bounds, 0 based
   */
  private boolean withinPath(int index) {
    return index >= 0 && index <= movePath.size() - 1;
  }

  public boolean isPathMoveComplete() {
    return pathMoveComplete;
  }

  public boolean foundTrapper() {
    return foundTrapper;
  }

  public void reset() {
    foundTrapper = false;
    pathMoveComplete = false;
  }

  public Mover getTrapper() {
    return mover;
  }

  public Direction getTrappedDirection() {
    if (foundTrapper) {
      return trappedDirection;
    } else {
      return Direction.STILL;
    }
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }
}
