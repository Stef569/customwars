package com.customwars.client.ui;

import com.customwars.client.model.map.Location;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Handles screen and world coordinates
 * The cameraX and cameraY values are used as offset for rendering other components.
 * In reality there is no camera, the components x and y coordinates are just translated.
 *
 * @author stefan
 */
public class Camera2D {
  private static final double MIN_ZOOM_LVL = 0.3;
  private static final double MAX_ZOOM_LVL = 1.6;
  private static final float ZOOM_STEP = 0.05f;
  private final Dimension worldSize;
  private final Dimension cameraSize;
  private final int tileSize;
  private float zoomLvl = 1;
  private int cameraX, cameraY;
  private boolean zoomEnabled;

  private Point shakeStartPoint;
  private int shakeMoveCount;
  private boolean shake;

  public Camera2D(Dimension cameraSize, Dimension worldSize, int tileSize) {
    this.cameraSize = new Dimension(cameraSize);
    this.worldSize = new Dimension(worldSize);
    this.tileSize = tileSize;
  }

  public void update(int elapsedTime) {
    if (shake) {
      shakeCamera();
    }
  }

  private void shakeCamera() {
    if (shakeStartPoint == null) shakeStartPoint = new Point(cameraX, cameraY);

    // Move 3x UP - 3x DOWN - 3x UP
    boolean canMoveUp = shakeMoveCount < 3 || (shakeMoveCount >= 6 && shakeMoveCount < 9);
    boolean canMoveDown = shakeMoveCount >= 3 && shakeMoveCount < 6;

    if (canMoveUp) {
      cameraY += 1;
      shakeMoveCount++;
    } else if (canMoveDown) {
      cameraY -= 1;
      shakeMoveCount++;
    } else {
      shake = false;
      shakeMoveCount = 0;
      cameraX = shakeStartPoint.x;
      cameraY = shakeStartPoint.y;
      shakeStartPoint = null;
    }
  }

  public void centerOnTile(Location location) {
    centerOnTile(location.getCol(), location.getRow());
  }

  public void centerOnTile(int col, int row) {
    if (canMoveHorizontal()) {
      setX((int) (col * tileSize - cameraSize.getWidth() / 2));
    } else if (canMoveVertical()) {
      setY((int) (row * tileSize - cameraSize.getHeight() / 2));
    }
  }

  public void zoomIn() {
    if (zoomEnabled) {
      setZoomLvl(zoomLvl + ZOOM_STEP);
    }
  }

  public void zoomOut() {
    if (zoomEnabled) {
      setZoomLvl(zoomLvl - ZOOM_STEP);
    }
  }

  private void setZoomLvl(float newZoomLvl) {
    if (newZoomLvl > MIN_ZOOM_LVL && newZoomLvl < MAX_ZOOM_LVL) {
      this.zoomLvl = newZoomLvl;
    }
  }

  public void moveLeft() {
    moveLeft(tileSize);
  }

  private void moveLeft(int amount) {
    if (canMoveHorizontal())
      setX(cameraX - amount);
  }

  public void moveRight() {
    moveRight(tileSize);
  }

  private void moveRight(int amount) {
    if (canMoveHorizontal())
      setX(cameraX + amount);
  }

  public void moveUp() {
    moveUp(tileSize);
  }

  private void moveUp(int amount) {
    if (canMoveVertical())
      setY(cameraY - amount);
  }

  public void moveDown() {
    moveDown(tileSize);
  }

  private void moveDown(int amount) {
    if (canMoveVertical())
      setY(cameraY + amount);
  }

  public void setZoomingEnabled(boolean enable) {
    this.zoomEnabled = enable;
  }

  /**
   * Set the horizontal camera position
   * Make sure the camera doesn't scroll off the map
   *
   * @param cameraX the new camera position on the x axis
   */
  private void setX(int cameraX) {
    if (cameraX < 0) cameraX = 0;
    if (cameraX + cameraSize.width >= worldSize.getWidth()) {
      cameraX = (int) (worldSize.getWidth() - getWidth());
    }
    this.cameraX = cameraX;
  }

  /**
   * Set the vertical camera position
   * Make sure the camera doesn't scroll off the map
   *
   * @param cameraY the new camera position on the y axis
   */
  private void setY(int cameraY) {
    if (cameraY < 0) cameraY = 0;
    if (cameraY + cameraSize.height >= worldSize.getHeight()) {
      cameraY = (int) (worldSize.getHeight() - getHeight());
    }
    this.cameraY = cameraY;
  }

  public int getMaxX() {
    return cameraX + cameraSize.width;
  }

  public int getMaxY() {
    return cameraY + cameraSize.height;
  }

  /**
   * @return The maximum width in tiles relative to the left map edge
   *         The return value rises when moving to the right
   */
  public int getMaxCols() {
    return getMaxX() / tileSize;
  }

  /**
   * @return The maximum height in tiles relative to the top map edge
   *         The return value rises when moving down
   */
  public int getMaxRows() {
    return getMaxY() / tileSize;
  }

  public int getCol() {
    return cameraX / tileSize;
  }

  public int getRow() {
    return cameraY / tileSize;
  }

  public int getX() {
    return cameraX;
  }

  public int getY() {
    return cameraY;
  }

  public float getZoomLvl() {
    return zoomLvl;
  }

  private boolean canMoveHorizontal() {
    return getMaxX() <= worldSize.getWidth();
  }

  private boolean canMoveVertical() {
    return getMaxY() <= worldSize.getHeight();
  }

  public int getWidth() {
    return (int) cameraSize.getWidth();
  }

  public int getHeight() {
    return (int) cameraSize.getHeight();
  }

  public void shake() {
    shake = true;
  }

  /**
   * @return Can the rectangle(x,y,width,height) fit within this camera
   */
  public boolean canFitWithin(int x, int y, int width, int height) {
    int maxX = x + width;
    int maxY = y + height;
    return x >= cameraX && maxX <= getMaxX() && y >= cameraY && maxY <= getMaxY();
  }
}
