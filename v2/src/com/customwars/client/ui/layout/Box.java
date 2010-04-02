package com.customwars.client.ui.layout;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.Component;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.awt.Point;

/**
 * A box is a rectangle that can be rendered.
 * It can have a border and a background.
 *
 * @author stefan
 */
public abstract class Box implements Component {
  public enum ALIGNMENT {
    LEFT, CENTER, RIGHT
  }

  private int x;
  private int y;
  private int width;
  private int height;
  private Point center = new Point();
  private boolean visible = true;
  private boolean inited;
  private Color borderColor;
  private Color backgroundColor;
  private ALIGNMENT alignment = ALIGNMENT.CENTER;

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

  public void setAlignment(ALIGNMENT alignment) {
    this.alignment = alignment;
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

  public ALIGNMENT getAlignement() {
    return alignment;
  }

  public boolean isVisible() {
    return visible;
  }
}
