package com.customwars.client.ui.slick;

import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public abstract class BasicComponent extends AbstractComponent {
  protected int x;
  protected int y;
  protected int width;
  protected int height;

  /**
   * Create a new component
   *
   * @param container The container displaying this component
   */
  public BasicComponent(GUIContext container) {
    super(container);
  }

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
