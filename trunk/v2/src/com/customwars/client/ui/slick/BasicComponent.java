package com.customwars.client.ui.slick;

import com.customwars.client.io.ResourceManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Dimension;

public abstract class BasicComponent extends AbstractComponent {
  private int x;
  private int y;
  private int width;
  private int height;
  private boolean visible = true;

  /**
   * Create a new component
   *
   * @param container The container displaying this component
   */
  public BasicComponent(GUIContext container) {
    super(container);
    consumeEvent();
  }

  public void loadResources(ResourceManager resources) {
  }

  public final void render(GUIContext container, Graphics g) {
    if (visible) {
      renderimpl(container, g);
    }
  }

  public abstract void renderimpl(GUIContext container, Graphics g);

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public Dimension getSize() {
    return new Dimension(width, height);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean isWithinComponent(int x, int y) {
    return y >= getY() && y <= getY() + getHeight() &&
      x >= getX() && x <= getX() + getWidth();
  }
}
