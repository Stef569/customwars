package com.customwars.client.ui.layout;

import org.newdawn.slick.Graphics;

import java.awt.Point;

/**
 * A box is a rectangle that can center it's content
 * and render it
 *
 * @author stefan
 */
public abstract class Box {
  int x;
  int y;
  int width;
  int height;
  Point center = new Point();
  private boolean visible = true;
  private boolean inited;

  public final void render(Graphics g) {
    if (!inited) {
      init();
      inited = true;
    }
    if (visible) {
      renderImpl(g);
    }
  }

  protected abstract void init();

  public abstract void renderImpl(Graphics g);

  /**
   * Center inner inside total
   *
   * @return The left top point to render inner inside the box
   */
  int center(int box, int inner) {
    if (inner < box) {
      return box / 2 - inner / 2;
    } else {
      return 0;
    }
  }

  public Point getCenter() {
    return center;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setWidth(int width) {
    this.width = width;
    inited = false;
  }

  public void setHeight(int height) {
    this.height = height;
    inited = false;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
