package com.customwars.client.controller;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.sprite.SpriteManager;

import java.util.List;

/**
 * Controls the active cursor within a map
 * Cursor movement can be enabled/disabled by #setCursorLocked(boolean)
 * The cursor movement can be limited to a list of locations
 * this can be used to iterate over attack or drop locations eg:
 *
 * startCursorTraversal (drop locations)   // Cursor can only move within the drop locations
 * moveCursorToNextLocation ()      // move cursor to next drop location
 * moveCursorToPreviousLocation ()  // previous drop location
 * stopCursorTraversal ()      // remove the 'move to drop locations only' restriction
 */
public class CursorController {
  private int cursorTraversalPos; // The current position within the limitedcursorLocations list in traversing mode
  private List<Location> limitedCursorLocations;  // A list of locations in where a cursor can move
  private boolean isTraversing;                   // Is the cursor limited to the above locations
  private boolean cursorLocked;                   // Is cursor moving disabled

  private TileMap<Tile> map;
  private SpriteManager spriteManager;

  public CursorController(TileMap<Tile> map, SpriteManager spriteManager) {
    this.map = map;
    this.spriteManager = spriteManager;
  }

  /**
   * This starts Cursor traversal mode
   * Invoking {@link #moveCursorToNextLocation} will move the cursor 1 location further
   * in the limitedCursorLocations collection.
   * When the cursor should no longer have a limited movement invoke {@link #stopCursorTraversal}
   *
   * @param locations the limited Locations the cursor can move in
   * @pre A cursor has been actived, locations is not null
   * @post The cursor is located on one of the tiles within locations
   */
  public void startCursorTraversal(List<Location> locations) {
    isTraversing = true;
    limitedCursorLocations = locations;
    moveCursorToNextLocation();
  }

  /**
   * Move the cursor to the next location in
   * limitedcursorLocations when out of bounds goto 0.
   */
  public void moveCursorToNextLocation() {
    if (canMoveInCursorTraversalMode()) {
      if (cursorTraversalPos + 1 >= limitedCursorLocations.size()) {
        cursorTraversalPos = 0;
      } else {
        cursorTraversalPos++;
      }
      Location nextLocation = limitedCursorLocations.get(cursorTraversalPos);
      moveCursor(nextLocation);
    }
  }

  /**
   * Move the cursor to the previous location in
   * limitedcursorLocations when out of bounds goto last location.
   */
  public void moveCursorToPreviousLocation() {
    if (canMoveInCursorTraversalMode()) {
      if (cursorTraversalPos - 1 < 0) {
        cursorTraversalPos = limitedCursorLocations.size() - 1;
      } else {
        cursorTraversalPos--;
      }
      Location previousLocation = limitedCursorLocations.get(cursorTraversalPos);
      moveCursor(previousLocation);
    }
  }

  private boolean canMoveInCursorTraversalMode() {
    if (!isTraversing)
      throw new IllegalStateException("Cursor traversal mode not started invoke startCursorTraversal() first");

    return (limitedCursorLocations != null && !limitedCursorLocations.isEmpty());
  }

  /**
   * This stops Cursor traversal mode
   * Allow the cursor to move to any tile
   */
  public void stopCursorTraversal() {
    cursorTraversalPos = 0;
    isTraversing = false;
    limitedCursorLocations = null;
  }

  public void toggleCursorLock() {
    this.cursorLocked = !cursorLocked;
  }

  public void setCursorLocked(boolean locked) {
    cursorLocked = locked;
  }

  public void activateCursor(String cursor) {
    spriteManager.setActiveCursor(cursor);
  }

  public void moveCursor(int x, int y) {
    Location newLocation = map.pixelsToTile(x, y);
    moveCursor(newLocation);
  }

  /**
   * In traversal mode:
   * Move the cursor to the next or previous location
   *
   * Normal mode:
   * Move the cursor to the direction relative to the current cursor location
   *
   * @param direction
   */
  public void moveCursor(Direction direction) {
    if (isTraversing) {
      moveCursorInTraversalMode(direction);
    } else {
      Location cursorLocation = spriteManager.getCursorLocation();
      Location newLocation = map.getRelativeTile(cursorLocation, direction);
      moveCursor(newLocation);
    }
  }

  private void moveCursorInTraversalMode(Direction direction) {
    switch (direction) {
      case EAST:
        moveCursorToNextLocation();
        break;
      case NORTH:
        moveCursorToNextLocation();
        break;
      case SOUTH:
        moveCursorToPreviousLocation();
        break;
      case WEST:
        moveCursorToPreviousLocation();
        break;
    }
  }

  public void moveCursor(Location location) {
    if (canMoveCursor(location)) {
      spriteManager.moveCursorTo(location);
    }
  }

  private boolean canMoveCursor(Location location) {
    return !cursorLocked && (!isTraversing || limitedCursorLocations.contains(location));
  }

  public boolean isTraversing() {
    return isTraversing;
  }

  public boolean isCursorLocked() {
    return cursorLocked;
  }

  public boolean cursorMoved(Location originalCursorLocation) {
    return spriteManager.getCursorLocation() != originalCursorLocation;
  }
}
