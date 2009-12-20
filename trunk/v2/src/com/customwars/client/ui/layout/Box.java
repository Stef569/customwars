package com.customwars.client.ui.layout;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.Component;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.awt.Point;

/**
 * A box is a rectangle that renders it's content in the center
 *
 * @author stefan
 */
public abstract class Box implements Component {
  private int x;
  private int y;
  private int width;
  private int height;
  private Point center = new Point();
  private boolean visible = true;
  private boolean inited;
  private Color borderColor;
  private Color backgroundColor;

  public void loadResources(ResourceManager resources) {
  }

  public final void render(Graphics g) {
    if (!inited) {
      init();
      inited = true;
    }
    if (visible) {
      renderDecoration(g);
      renderImpl(g);
    }
  }

  private void renderDecoration(Graphics g) {
    Color origColor = g.getColor();
    if (borderColor != null) {
      renderBorder(g);
    } else if (backgroundColor != null) {
      renderBackground(g);
    }
    g.setColor(origColor);
  }

  private void renderBorder(Graphics g) {
    g.setColor(borderColor);
    g.drawRect(x, y, width, height);
  }

  private void renderBackground(Graphics g) {
    g.setColor(backgroundColor);
    g.fillRect(x, y, width, height);
  }

  public abstract void renderImpl(Graphics g);

  /**
   * Init this box, this method is called when the box size changes
   * this allows the box to recenter it's content
   */
  public void init() {
  }

  void setCenter(int centerX, int centerY) {
    center = new Point(centerX, centerY);
  }

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setWidth(int width) {
    this.width = width;
    init();
  }

  public void setHeight(int height) {
    this.height = height;
    init();
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getMaxX() {
    return x + width;
  }

  public int getMaxY() {
    return y + height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  int getCenterX() {
    return center.x;
  }

  int getCenterY() {
    return center.y;
  }

  public boolean isVisible() {
    return visible;
  }
}
