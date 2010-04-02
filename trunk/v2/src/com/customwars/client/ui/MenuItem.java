package com.customwars.client.ui;

import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.Layout;
import com.customwars.client.ui.slick.MouseOverArea;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A single Menu item containing:
 * An Icon and text
 * Each of the above is optional and rendered centered in a box.
 * The menu item height is set to the highest inner box height.
 *
 * @author stefan
 */
public abstract class MenuItem extends MouseOverArea {
  private static final int MENU_ITEM_MARGIN = 1;
  private Color textColor = Color.white;
  private final List<Component> boxes = new ArrayList<Component>();

  public MenuItem(GUIContext container) {
    super(container);
  }

  public final void addBox(Box box) {
    boxes.add(box);
    initArea();
  }

  /**
   * Set the area that this menu items covers
   */
  private void initArea() {
    int x = getX() - MENU_ITEM_MARGIN;
    int y = getY() - MENU_ITEM_MARGIN;
    int maxWidth = getMaxWidth() + MENU_ITEM_MARGIN;
    int maxHeight = getMaxHeight() + MENU_ITEM_MARGIN;
    setArea(x, y, maxWidth, maxHeight);
  }

  public void setTextColor(Color txtColor) {
    this.textColor = txtColor;
  }

  protected final void layout() {
    initHorizontalBoxSizes();
    initVerticalBoxSizes();
    initBoxLocations();
  }

  protected abstract void initHorizontalBoxSizes();

  /**
   * This method determines to what Box the excess horizontal and vertical space goes to
   *
   * All boxes have the same height as the menu item
   */
  private void initVerticalBoxSizes() {
    for (Component box : boxes) {
      box.setHeight(getHeight());
    }
  }

  private void initBoxLocations() {
    int x = getX();
    int y = getY();

    Layout.locateLeftToRight(boxes, x, y);
  }

  private int getMaxWidth() {
    int totalWidth = 0;
    for (Component box : boxes) {
      totalWidth += box.getWidth();
    }
    return totalWidth;
  }

  private int getMaxHeight() {
    int highest = 0;

    for (Component box : boxes) {
      if (box.getHeight() > highest) highest = box.getHeight();
    }
    return highest;
  }

  public final void renderimpl(GUIContext container, Graphics g) {
    super.renderimpl(container, g);
    renderBorder(g);
    renderBoxes(g);
  }

  private void renderBoxes(Graphics g) {
    g.setColor(textColor);

    for (Component box : boxes) {
      box.render(g);
    }
  }

  private void renderBorder(Graphics g) {
    g.setColor(Color.black);
    g.drawRect(getX(), getY(), getWidth(), getHeight());
  }

  /**
   * Invoked when the menu item is activated by a key press.
   */
  protected final void activate() {
    super.notifyListeners();
  }
}