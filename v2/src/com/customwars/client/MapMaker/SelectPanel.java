package com.customwars.client.MapMaker;

import com.customwars.client.App;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.slick.MouseOverArea;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of Mouse over areas(moa's),
 * when a click has been made on a moa then the index within the moa list is stored(selectedIndex)
 *
 * The width of this component = mouseOverAreas * tileSize
 * The height of this component = the height of the first moa in the list
 *
 * @author stefan
 */
public class SelectPanel extends BasicComponent implements ComponentListener {
  private List<MouseOverArea> mouseOverAreas = new ArrayList<MouseOverArea>();
  private int tileSize;
  private int selectedIndex;
  boolean inited;

  public SelectPanel(GUIContext container) {
    super(container);
    tileSize = App.getInt("plugin.tilesize");
  }

  public void add(Image img) {
    MouseOverArea moa = new MouseOverArea(container, img, this);
    moa.setNormalColor(new Color(1, 1, 1, 0.8f));
    moa.setMouseOverColor(new Color(1, 1, 1, 0.9f));
    mouseOverAreas.add(moa);
    setWidth(getTotalWidth());
  }

  private int getTotalWidth() {
    int totalWidth = 0;
    for (MouseOverArea moa : mouseOverAreas) {
      totalWidth += moa.getWidth();
    }
    return totalWidth;
  }

  @Override
  public void setLocation(int x, int y) {
    super.setLocation(x, y);
    init();
  }

  @Override
  public void renderimpl(GUIContext container, Graphics g) {
    if (inited) {
      init();
      inited = false;
    }

    for (MouseOverArea moa : mouseOverAreas) {
      moa.render(container, g);
    }
  }

  public void init() {
    if (mouseOverAreas != null) {
      locateMouseOverAreas();
      setHeight(mouseOverAreas.get(0).getHeight());
    }
  }

  /**
   * Locate the moa's from left to right
   */
  private void locateMouseOverAreas() {
    for (int i = 0; i < mouseOverAreas.size(); i++) {
      int x = getX() + i * tileSize;
      int y = getY();

      MouseOverArea moa = mouseOverAreas.get(i);
      moa.setLocation(x, y);
    }
  }

  /**
   * Move 1 tile to the left
   */
  public void moveLeft() {
    move(-tileSize);
  }

  /**
   * Move 1 tile to the right
   */
  public void moveRight() {
    move(tileSize);
  }

  /**
   * Move the x coordinate with xOffset
   */
  public void move(int xOffset) {
    int totalWidth = getWidth();
    int x = getX() + xOffset;
    if (x + totalWidth < 0) x = 0;

    setLocation(x, getY());
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void componentActivated(AbstractComponent source) {
    selectedIndex = mouseOverAreas.indexOf(source);
  }
}
