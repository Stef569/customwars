package com.customwars.client.ui.mapeditor;

import com.customwars.client.App;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.slick.MouseOverArea;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of Mouse over areas(moa's),
 * when a click has been made on a moa then the index within the moa list is stored(selectedIndex)
 * <p/>
 * The width of this component = mouseOverAreas * tileSize
 * The height of this component = the height of the first moa in the list
 *
 * @author stefan
 */
public abstract class SelectPanel extends BasicComponent {
  private static final int PANEL_SCROLL_DELAY = 150;
  private final List<MouseOverArea> mouseOverAreas = new ArrayList<MouseOverArea>();
  private final int tileSize;
  private final int scrollMargin;
  private int selectedIndex;
  private int timeTaken;
  private boolean inited;

  protected SelectPanel(GUIContext container) {
    super(container);
    tileSize = App.getInt("plugin.tilesize");
    scrollMargin = tileSize;
  }

  public void add(Image img) {
    MouseOverArea moa = new MouseOverArea(container, img);
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

  public void update(int delta) {
    if (!canFitToContainer() && isWithinComponent(0, input.getAbsoluteMouseY())) {
      timeTaken += delta;
      if (timeTaken >= PANEL_SCROLL_DELAY) {
        boolean nearLeftEdge = input.getAbsoluteMouseX() < scrollMargin;
        boolean nearRightEdge = input.getAbsoluteMouseX() > container.getWidth() - scrollMargin;
        timeTaken = 0;

        if (nearLeftEdge) {
          moveRight();
        } else if (nearRightEdge) {
          moveLeft();
        }
      }
    }
  }

  public void mouseClicked(int button, int x, int y, int clickCount) {
    int mouseX = input.getAbsoluteMouseX();
    int mouseY = input.getAbsoluteMouseY();

    if (isWithinComponent(mouseX, mouseY)) {
      selectedIndex = Math.round(mouseX / tileSize);
    }
  }

  private boolean canFitToContainer() {
    return getWidth() <= container.getWidth() && getHeight() <= container.getHeight();
  }

  @Override
  public void renderimpl(GUIContext container, Graphics g) {
    if (!inited) {
      init();
      inited = true;
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
    move(-scrollMargin);
  }

  /**
   * Move 1 tile to the right
   */
  public void moveRight() {
    move(scrollMargin);
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

  public void clear() {
    mouseOverAreas.clear();
  }

  void setSelectedIndex(int index) {
    this.selectedIndex = index;
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public abstract void recolor(java.awt.Color color);

  @Override
  public boolean isWithinComponent(int x, int y) {
    return x >= getX() && x <= getX() + getWidth() && y >= getY() && y <= getY() + getHeight();
  }

  public abstract boolean canSelect(Tile cursorLocation);

  /**
   * Allows external selection based on the given location.
   * The internal selection index will be set to the object ID on the location
   * Pre: canSelect( cursorLocation ) == true
   */
  public abstract void select(Tile location);
}
