package com.customwars.client.ui;

import com.customwars.client.io.ResourceManager;
import org.newdawn.slick.Graphics;

/**
 * A GUI component
 */
public interface Component {
  public void loadResources(ResourceManager resources);

  void render(Graphics g);

  void setWidth(int width);

  void setHeight(int height);

  void setVisible(boolean visible);

  void setLocation(int x, int y);

  int getMaxX();

  int getMaxY();

  int getWidth();

  int getHeight();

  int getX();

  int getY();
}
