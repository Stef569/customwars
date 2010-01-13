package com.customwars.client.controller;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Controls the active cursor within a map
 * Cursor movement can be enabled/disabled by #setCursorLocked(boolean)
 * The cursor movement can be limited to a list of locations
 * this can be used to iterate over attack or drop locations
 *
 * eg: To limit the cursor to drop locations only:
 * startCursorTraversal (drop locations)   Cursor can only move within the drop locations
 * moveCursorToNextLocation ()             move cursor to next drop location
 * moveCursorToPreviousLocation ()         move cursor to previous drop location
 * stopCursorTraversal ()                  remove the 'move to drop locations only' restriction
 */
public class CursorController {
  private final TileMap<Tile> map;
  private final SpriteManager spriteManager;

  private int cursorTraversalPos; // The current position within the restricted cursor range list in traversing mode
  private List<Location> restrictedCursorRange;   // A list of locations in where a cursor can move
  private boolean isTraversing;                   // Is the cursor limited to the above locations
  private boolean cursorLocked;                   // Is cursor moving disabled

  public CursorController(TileMap<Tile> map, SpriteManager spriteManager) {
    this.map = map;
    this.spriteManager = spriteManager;
  }

  /**
   * Add a cursor keyed by cursorName, to start rendering a cursor call #activateCursor(cursorName)
   *
   * @param cursorName   The name to store the cursor by
   * @param cursorSprite The cursor sprite to add
   */
  public void addCursor(String cursorName, TileSprite cursorSprite) {
    spriteManager.addCursor(cursorName, cursorSprite);
  }

  /**
   * Active a cursor, this cursor will be rendered
   * At any time there can only be 1 active cursor
   *
   * @param cursorName The name of the cursor to activate
   */
  public void activateCursor(String cursorName) {
    spriteManager.setActiveCursor(cursorName);
  }

  /**
   * This starts Cursor traversal mode
   * Invoking {@link #moveCursorToNextLocation} will move the cursor 1 location further
   * in the restrictedCursorRange collection.
   * When the cursor should no longer have a limited movement invoke {@link #stopCursorTraversal}
   *
   * pre A cursor has been actived, locations is not null
   * post The cursor is located on one of the tiles within locations
   *
   * @param restrictedCursorRange the Locations the cursor is allowed to move to
   */
  public void startCursorTraversal(List<Location> restrictedCursorRange) {
    isTraversing = true;
    this.restrictedCursorRange = restrictedCursorRange;
    moveCursorToNextLocation();
  }

  /**
   * Move the cursor to the next location in
   * restrictedCursorRange when out of bounds move the cursor to the first location.
   */
  public void moveCursorToNextLocation() {
    if (canMoveInCursorTraversalMode()) {
      if (cursorTraversalPos + 1 >= restrictedCursorRange.size()) {
        cursorTraversalPos = 0;
      } else {
        cursorTraversalPos++;
      }
      Location nextLocation = restrictedCursorRange.get(cursorTraversalPos);
      moveCursor(nextLocation);
    }
  }

  /**
   * Move the cursor to the previous location in
   * restrictedCursorRange when out of bounds move the cursor to the last location.
   */
  public void moveCursorToPreviousLocation() {
    if (canMoveInCursorTraversalMode()) {
      if (cursorTraversalPos - 1 < 0) {
        cursorTraversalPos = restrictedCursorRange.size() - 1;
      } else {
        cursorTraversalPos--;
      }
      Location previousLocation = restrictedCursorRange.get(cursorTraversalPos);
      moveCursor(previousLocation);
    }
  }

  private boolean canMoveInCursorTraversalMode() {
    if (!isTraversing)
      throw new IllegalStateException("Cursor traversal mode not started invoke startCursorTraversal() first");

    return (restrictedCursorRange != null && !restrictedCursorRange.isEmpty());
  }

  /**
   * This stops Cursor traversal mode
   * Allowing the cursor to move to any tile
   */
  public void stopCursorTraversal() {
    cursorTraversalPos = 0;
    isTraversing = false;
    restrictedCursorRange = null;
  }

  public void toggleCursorLock() {
    this.cursorLocked = !cursorLocked;
  }

  public void setCursorLocked(boolean locked) {
    cursorLocked = locked;
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
   * @param direction The direction to move to relative to the current cursor location
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
    return !cursorLocked && (!isTraversing || restrictedCursorRange.contains(location));
  }

  public boolean isTraversing() {
    return isTraversing;
  }

  public boolean isCursorLocked() {
    return cursorLocked;
  }

  /**
   * Stop sending cursor events to the listener
   *
   * @param listener the object that wants to stop receiving cursor events
   */
  public void removeListener(PropertyChangeListener listener) {
    spriteManager.removeCursorListener(listener);
  }

  /**
   * Start sending cursor events to the listener
   *
   * @param listener the object that wants to receiving cursor events
   */
  public void addListener(PropertyChangeListener listener) {
    spriteManager.addCursorListener(listener);
  }
}
