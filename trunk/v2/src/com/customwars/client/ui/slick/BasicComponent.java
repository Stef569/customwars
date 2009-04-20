package com.customwars.client.ui.slick;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.Camera2D;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public abstract class BasicComponent extends AbstractComponent {
  private int x;
  private int y;
  private int width;
  private int height;
  private boolean visible = true;
  protected static Camera2D camera;

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

  /**
   * Gives the focus to this component with a click of the mouse.
   *
   * @see org.newdawn.slick.gui.AbstractComponent#mouseReleased(int,int,int)
   */
  public void mouseReleased(int button, int x, int y) {
    if (camera == null) return;
    setFocus(Rectangle.contains(
            camera.convertToGameX(x),
            camera.convertToGameY(y),
            getX(), getY(),
            getWidth(), getHeight()));
  }

  public static void setCamera(Camera2D camera) {
    BasicComponent.camera = camera;
  }
}
