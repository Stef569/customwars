package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.List;

/**
 * Allows Movers to traverse within a TileMap
 *
 * Usage:
 * moveSystem.prepareMove(mover, destination)
 * Then repeatedly call moveSystem.update() to do 1 step in the path
 *
 * There can only be one mover moving through the map at any time.
 * The pathFinder creates the shortest path from the mover location to the destination.
 * The calculation is based on the MovementCost to move from 1 location to another,
 * This is defined by getMovementCost(int col, int row) in the movers MoveCost.
 *
 * When the move is completed(trapper on the path or destination reached)
 * isPathMoveComplete() will return true
 *
 * @author stefan
 */
public class MoveTraverse {
  private static final Logger logger = Logger.getLogger(MoveTraverse.class);
  private TileMap<Tile> map;      // The map the mover can move in
  private Mover mover;
  private int totalMoveCost;

  private int pathIndex;              // Contains the current position in the move path
  private boolean pathMoveComplete;   // The mover has completed the move(trapped or chosen destination)
  private boolean foundTrapper;       // Indicates that an enemy Locatable was found on the movepath(a trapper)
  private static PathFinder pathFinder;
  private List<Location> movePath;    // The path we are going to move over

  public MoveTraverse(TileMap<Tile> map) {
    this.map = map;
    pathFinder = new PathFinder(map);
  }

  public void prepareMove(Mover mover, Location destination) {
    Args.checkForNull(mover);
    Args.checkForNull(destination);

    prepareForNextMove();
    pathFinder.setMover(mover);

    if (pathFinder.canMoveTo(mover, destination)) {
      this.mover = mover;
      this.movePath = pathFinder.getMovementPath(mover, destination);
    } else {
      pathMoveComplete = true;
      logger.warn("Could not prepare move" +
              " from " + mover.getLocation().getCol() + "," + mover.getLocation().getRow() +
              " to " + destination.getCol() + "," + destination.getRow() +
              " for " + mover);
    }
  }

  /**
   * Cleans up the values from a previous move
   */
  void prepareForNextMove() {
    mover = null;
    movePath = null;
    pathIndex = 0;
    pathMoveComplete = false;
    foundTrapper = false;
    totalMoveCost = 0;
  }

  /**
   * Updates the model position and orientation of the mover By moving one step further in the movePath
   * movePath and mover are set by {@link #prepareMove(Mover,Location)}
   * if the path is already completed or the movePath has not been generated then nothing happens.
   * if the destination is reached then
   * {@link #isPathMoveComplete()} returns true
   * if a trapper was found within the path then
   * {@link #foundTrapper()} and {@link #isPathMoveComplete()} return true
   */
  public void update() {
    if (pathMoveComplete || movePath == null) return;

    if (withinPath(pathIndex)) {
      if (withinPath(pathIndex + 1)) {
        if (hasEnemyOnNextLocation()) {
          foundTrapper = true;
          pathMoveComplete();
        } else {
          moveToNextLocationOnPath();
          pathIndex++;
        }
      } else {
        pathMoveComplete();
      }
    }
  }

  private void pathMoveComplete() {
    pathMoveComplete = true;
    mover.addPathMoveCost(-totalMoveCost);
  }

  boolean hasEnemyOnNextLocation() {
    Location location = movePath.get(pathIndex + 1);
    Locatable locatable = location.getLastLocatable();

    if (locatable instanceof Mover) {
      Mover trapper = (Mover) locatable;
      return !trapper.getOwner().isAlliedWith(mover.getOwner());
    }
    return false;
  }

  void moveToNextLocationOnPath() {
    Location currentLocation = movePath.get(pathIndex);
    Tile nextLocation = (Tile) movePath.get(pathIndex + 1);
    mover.setOrientation(map.getDirectionTo(currentLocation, nextLocation));
    map.teleport(currentLocation, nextLocation, mover);
    totalMoveCost += nextLocation.getTerrain().getMoveCost(mover.getMovementType());
  }

  /**
   * Inside movePath bounds check, 0 based
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
}
