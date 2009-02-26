package com.customwars.client.ui;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;

/**
 * Scroll the camera by determening if the cursorlocation is x tiles from the edge of the camera
 * autoScroll will attempt to scroll after a small delay has passed,
 * turning autoScroll off means that scroll(Direction)
 * should be invoked manually when an attempt to scroll should be undertaken.
 *
 * @author stefan
 */
public class Scroller {
  private static final int SCROLL_DELAY = 150;
  private static final int TILES_FROM_EDGE = 1;
  private int time;
  private Camera2D camera;
  private Location cursorLocation;
  private boolean autoScroll;

  public Scroller(Camera2D camera) {
    this(camera, true);
  }

  public Scroller(Camera2D camera, boolean autoUpdate) {
    this.autoScroll = autoUpdate;
    this.camera = camera;
  }

  public void update(int elapsedTime) {
    if (autoScroll) {
      time += elapsedTime;

      if (time >= SCROLL_DELAY) {
        scroll(Direction.NORTH);
        scroll(Direction.EAST);
        scroll(Direction.SOUTH);
        scroll(Direction.WEST);
        time = 0;
      }
    }
  }

  public void scroll(Direction direction) {
    if (cursorLocation == null) return;

    switch (direction) {
      case EAST:
        if (cursorLocation.getCol() >= camera.getCols() - TILES_FROM_EDGE) {
          camera.moveRight();
        }
        break;
      case NORTH:
        if (cursorLocation.getRow() <= camera.getRow() + TILES_FROM_EDGE) {
          camera.moveUp();
        }
        break;
      case SOUTH:
        if (cursorLocation.getRow() >= camera.getRows() - TILES_FROM_EDGE) {
          camera.moveDown();
        }
        break;
      case WEST:
        if (cursorLocation.getCol() <= camera.getCol() + TILES_FROM_EDGE) {
          camera.moveLeft();
        }
        break;
    }
  }

  public void setCursorLocation(Location cursorLocation) {
    this.cursorLocation = cursorLocation;
  }

  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  public void toggleAutoUpdate() {
    this.autoScroll = !autoScroll;
  }
}
