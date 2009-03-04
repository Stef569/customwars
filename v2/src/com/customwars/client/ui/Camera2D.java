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
  private Dimension screen;
  private Dimension camera;
  private int cameraX, cameraY;
  private int tileSize;

  public Camera2D(Dimension cameraSize, Dimension screenSize, Dimension worldSize, int tileSize) {
    this.camera = new Dimension(cameraSize.width, cameraSize.height);
    this.screen = screenSize;
    this.world = worldSize;
    this.tileSize = tileSize;
  }

  public Camera2D(Dimension screenSize, Dimension worldSize, int tileSize) {
    this(screenSize, screenSize, worldSize, tileSize);
  }

  public void update(int elapsedTime) {

  }

  public void centerOnTile(Location location) {
    centerOnTile(location.getCol(), location.getRow());
  }

  public void centerOnTile(int col, int row) {
    if (canMove()) {
      setX((int) (col * tileSize - camera.getWidth() / 2));
      setY((int) (row * tileSize - camera.getHeight() / 2));
    }
  }

  public int convertToGameX(int x) {
    return (int) (x / zoomLvl) + cameraX;
  }

  public int convertToGameY(int y) {
    return (int) (y / zoomLvl) + cameraY;
  }

  public void zoomIn() {
    setZoomLvl(zoomLvl + ZOOM_STEP);
  }

  public void zoomOut() {
    setZoomLvl(zoomLvl - ZOOM_STEP);
  }

  public void moveLeft() {
    moveLeft(tileSize);
  }

  private void moveLeft(int amount) {
    if (canMove())
      setX(cameraX - amount);
  }

  public void moveRight() {
    moveRight(tileSize);
  }

  private void moveRight(int amount) {
    if (canMove())
      setX(cameraX + amount);
  }

  public void moveUp() {
    moveUp(tileSize);
  }

  private void moveUp(int amount) {
    if (canMove())
      setY(cameraY - amount);
  }

  public void moveDown() {
    moveDown(tileSize);
  }

  private void moveDown(int amount) {
    if (canMove())
      setY(cameraY + amount);
  }

  private void setZoomLvl(float newZoomLvl) {
    if (newZoomLvl > 0.3 && newZoomLvl < 1.6) {
      this.zoomLvl = newZoomLvl;
    }
  }

  /**
   * Make sure the camera doesn't scroll off the map
   *
   * @param cameraX the new camera position on the x axis
   */
  private void setX(int cameraX) {
    if (cameraX < 0) cameraX = 0;
    if (cameraX >= world.getWidth() - screen.getWidth()) {
      cameraX = (int) (world.getWidth() - screen.getWidth());
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
    if (cameraY >= world.getWidth() - screen.getWidth()) {
      cameraY = (int) (world.getWidth() - screen.getWidth());
    }
    this.cameraY = cameraY;
  }

  public int getMaxX() {
    return cameraX + camera.width;
  }

  public int getMaxY() {
    return cameraY + camera.height;
  }

  public int getCols() {
    return getMaxX() / tileSize;
  }

  public int getRows() {
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

  /**
   * @return True when the camera is larger then the game world
   */
  private boolean canMove() {
    return camera.getWidth() < world.getWidth() &&
            camera.getHeight() < world.getHeight();
  }
}
