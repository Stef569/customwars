package com.customwars.client.ui;

import com.customwars.client.App;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;

/**
 * Scroll the camera by determining if the cursor location is TILES_FROM_EDGE tiles from the camera edge.
 * autoScroll will attempt to scroll after SCROLL_DELAY has passed in each game loop.
 *
 * @author stefan
 */
public class Scroller {
  private final int SCROLL_DELAY;
  private final int TILES_FROM_EDGE;
  private int time;
  private Camera2D camera;
  private Location cursorLocation;
  private boolean autoScroll;

  public Scroller(Camera2D camera) {
    this(camera, true);
  }

  public Scroller(Camera2D camera, boolean autoScroll) {
    this.camera = camera;
    this.autoScroll = autoScroll;
    SCROLL_DELAY = App.getInt("scroller.scrolldelay", 50);
    TILES_FROM_EDGE = App.getInt("scroller.startscrolling", 1);
  }

  /**
   * if autoScroll is on then attempt to scroll in each game loop
   */
  public void update(int elapsedTime) {
    if (autoScroll) {
      time += elapsedTime;

      if (time >= SCROLL_DELAY) {
        attemptToScroll(Direction.NORTH);
        attemptToScroll(Direction.EAST);
        attemptToScroll(Direction.SOUTH);
        attemptToScroll(Direction.WEST);
        time = 0;
      }
    }
  }

  public void attemptToScroll(Direction direction) {
    if (canScroll(direction))
      scroll(direction);
  }

  private boolean canScroll(Direction direction) {
    if (cursorLocation == null) return false;

    switch (direction) {
      case EAST:
        if (cursorLocation.getCol() >= camera.getMaxCols() - TILES_FROM_EDGE) {
          return true;
        }
        break;
      case NORTH:
        if (cursorLocation.getRow() <= camera.getRow() + TILES_FROM_EDGE) {
          return true;
        }
        break;
      case SOUTH:
        if (cursorLocation.getRow() >= camera.getMaxRows() - TILES_FROM_EDGE) {
          return true;
        }
        break;
      case WEST:
        if (cursorLocation.getCol() <= camera.getCol() + TILES_FROM_EDGE) {
          return true;
        }
        break;
    }
    return false;
  }

  /**
   * Scroll to the given direction
   */
  private void scroll(Direction direction) {
    switch (direction) {
      case EAST:
        camera.moveRight();
        break;
      case NORTH:
        camera.moveUp();
        break;
      case SOUTH:
        camera.moveDown();
        break;
      case WEST:
        camera.moveLeft();
        break;
    }
  }

  public void setCursorLocation(Location cursorLocation) {
    this.cursorLocation = cursorLocation;
  }

  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  public boolean isAutoScrollOn() {
    return autoScroll;
  }
}
