package com.customwars.client.ui;

import com.customwars.client.model.map.Location;

import java.awt.Dimension;

/**
 * Handles screen and world coordinates
 * The cameraX and cameraY values are used as offset for rendering other components.
 * In reality there is no camera, the components x and y coordinates are just translated.
 *
 * Further improvements can be shaking the screen throught the update method.
 *
 * @author stefan
 */
public class Camera2D {
  private static final float ZOOM_STEP = 0.05f;
  private float zoomLvl = 1;
  private Dimension world;
  private Dimension camera;
  private int cameraX, cameraY;
  private int tileSize;

  public Camera2D(Dimension cameraSize, Dimension worldSize, int tileSize) {
    this.camera = new Dimension(cameraSize);
    this.world = new Dimension(worldSize);
    this.tileSize = tileSize;
  }

  public void update(int elapsedTime) {

  }

  public void centerOnTile(Location location) {
    centerOnTile(location.getCol(), location.getRow());
  }

  public void centerOnTile(int col, int row) {
    if (canMoveHorizontal()) {
      setX((int) (col * tileSize - camera.getWidth() / 2));
    } else if (canMoveVertical()) {
      setY((int) (row * tileSize - camera.getHeight() / 2));
    }
  }

  public void zoomIn() {
    setZoomLvl(zoomLvl + ZOOM_STEP);
  }

  public void zoomOut() {
    setZoomLvl(zoomLvl - ZOOM_STEP);
  }

  private void setZoomLvl(float newZoomLvl) {
    if (newZoomLvl > 0.3 && newZoomLvl < 1.6) {
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

  /**
   * Make sure the camera doesn't scroll off the map
   *
   * @param cameraX the new camera position on the x axis
   */
  private void setX(int cameraX) {
    if (cameraX < 0) cameraX = 0;
    if (cameraX + camera.width >= world.getWidth()) {
      cameraX = (int) (world.getWidth() - getWidth());
    }
    this.cameraX = cameraX;
  }

  /**
   * Make sure the camera doesn't scroll off the map
   *
   * @param cameraY the new camera position on the y axis
   */
  private void setY(int cameraY) {
    if (cameraY < 0) cameraY = 0;
    if (cameraY + camera.height >= world.getHeight()) {
      cameraY = (int) (world.getHeight() - getHeight());
    }
    this.cameraY = cameraY;
  }

  public int getMaxX() {
    return cameraX + camera.width;
  }

  public int getMaxY() {
    return cameraY + camera.height;
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
    return getMaxX() <= world.getWidth();
  }

  private boolean canMoveVertical() {
    return getMaxY() <= world.getHeight();
  }

  public int getWidth() {
    return (int) camera.getWidth();
  }

  public int getHeight() {
    return (int) camera.getHeight();
  }

  /**
   * @return Can the rectange fit within this camera
   */
  public boolean canFitWithin(int x, int y, int width, int height) {
    int maxX = x + width;
    int maxY = y + height;
    return x > getX() && maxX < getMaxX() && y > getY() && maxY < getMaxY();
  }
}
