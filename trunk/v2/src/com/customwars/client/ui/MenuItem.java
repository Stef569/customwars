package com.customwars.client.ui;

import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.slick.MouseOverArea;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A single Menu item containing:
 * An animated cursor, Icon and text
 * Each of the above is optional and rendered centered in a box, each box is the dimension of the Image/text + the margins
 * The menu item height is set to the highest inner box height.
 *
 * @author stefan
 */
public class MenuItem extends MouseOverArea {
  private static final int CURSOR_LEFT_MARGIN = 1, CURSOR_RIGHT_MARGIN = 5;
  private static final int ICON_LEFT_MARGIN = 2, ICON_RIGHT_MARGIN = 1;
  private static final int FONT_HORIZONTAL_MARGIN = 15;
  private static final int MENU_ITEM_MARGIN = 1;
  private Color textColor = Color.white;

  private Box imgBox = new ImageBox();
  private Box textBox = new TextBox("", container.getDefaultFont());
  private final Collection<Box> boxes = new ArrayList<Box>();

  public MenuItem(GUIContext container) {
    this(null, null, null, container);
  }

  public MenuItem(String txt, Font font, GUIContext container) {
    this(null, txt, font, container);
  }

  public MenuItem(String txt, GUIContext container) {
    this(null, txt, container.getDefaultFont(), container);
  }

  public MenuItem(Image icon, GUIContext container) {
    this(icon, null, null, container);
  }

  public MenuItem(Image icon, String txt, GUIContext container) {
    this(icon, txt, container.getDefaultFont(), container);
  }

  public MenuItem(Image icon, String txt, Font font, GUIContext container) {
    super(container);
    init(icon, txt, font);
  }

  protected void init(Image icon, String txt, Font font) {
    setIcon(icon);
    setText(txt, font);
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

  public void setIcon(Image icon) {
    if (icon != null) {
      Insets insets = new Insets(0, ICON_LEFT_MARGIN, 0, ICON_RIGHT_MARGIN);
      imgBox = new ImageBox(icon, insets);
      boxes.add(imgBox);
    }
  }

  public void setText(String text, Font font) {
    if (text != null) {
      Insets insets = new Insets(0, FONT_HORIZONTAL_MARGIN, 0, FONT_HORIZONTAL_MARGIN);
      textBox = new TextBox(text, font, insets);
      boxes.add(textBox);
    }
  }

  public void setTextColor(Color txtColor) {
    this.textColor = txtColor;
  }

  public void initBoxes() {
    initBoxSizes();
    initBoxLocations();
  }

  /**
   * Excess horizontal space goes
   * to the text if there is any text
   * to the image if there is no text
   *
   * All boxes have the same height as the menu item
   */
  private void initBoxSizes() {
    if (textBox.getWidth() > 0) {
      textBox.setWidth(getWidth() - imgBox.getWidth());
    } else {
      imgBox.setWidth(getWidth() - textBox.getWidth());
    }

    for (Box box : boxes) {
      box.setHeight(getHeight());
    }
  }

  private void initBoxLocations() {
    int x = getX();
    int y = getY();

    imgBox.setLocation(x, y);
    textBox.setLocation(x + imgBox.getWidth(), y);
  }

  private int getMaxWidth() {
    int totalWidth = 0;
    for (Box box : boxes) {
      totalWidth += box.getWidth();
    }
    return totalWidth;
  }

  private int getMaxHeight() {
    int highest = 0;

    for (Box box : boxes) {
      if (box.getHeight() > highest) highest = box.getHeight();
    }
    return highest;
  }

  public void renderimpl(GUIContext container, Graphics g) {
    super.renderimpl(container, g);
    renderBorder(g);
    renderBoxes(g);
  }

  private void renderBoxes(Graphics g) {
    g.setColor(textColor);
    imgBox.render(g);
    textBox.render(g);
  }

  private void renderBorder(Graphics g) {
    g.setColor(Color.black);
    g.drawRect(getX(), getY(), getWidth(), getHeight());
  }
}
