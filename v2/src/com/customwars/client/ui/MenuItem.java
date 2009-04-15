package com.customwars.client.ui;

import com.customwars.client.ui.layout.AnimationBox;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.layout.ImageBox;
import com.customwars.client.ui.layout.TextBox;
import com.customwars.client.ui.slick.MouseOverArea;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * A single Menu item containing:
 * An animated cursor, Icon and text
 * Each of the above is optional and rendered centered in a box, each box is the dimension of the Image/text + the margins
 * The menu item height is set to the highest box height.
 *
 * @author stefan
 */
public class MenuItem extends MouseOverArea {
  private static final int CURSOR_LEFT_MARGIN = 1, CURSOR_RIGHT_MARGIN = 5;
  private static final int ICON_LEFT_MARGIN = 2, ICON_RIGHT_MARGIN = 1;
  private static final int FONT_HORIZONTAL_MARGIN = 15;
  private static final int MENU_ITEM_MARGIN = 1;
  private Color textColor = Color.white;

  private Box animBox = new AnimationBox();
  private Box imgBox = new ImageBox();
  private Box textBox = new TextBox("", container.getDefaultFont());
  private List<Box> boxes = new ArrayList<Box>();

  public MenuItem(GUIContext container) {
    this(null, null, null, container);
  }

  public MenuItem(String txt, GUIContext container) {
    this(null, null, txt, container);
  }

  public MenuItem(Image icon, GUIContext container) {
    this(null, icon, null, container);
  }

  public MenuItem(Animation cursorAnim, Image icon, GUIContext container) {
    this(cursorAnim, icon, "", container);
  }

  public MenuItem(Image icon, String txt, GUIContext container) {
    this(null, icon, txt, container);
  }

  public MenuItem(Animation cursorAnim, Image icon, String txt, GUIContext container) {
    super(container);
    init(cursorAnim, icon, txt);
  }

  protected void init(Animation cursorAnim, Image icon, String txt) {
    setCursorAnim(cursorAnim);
    setIcon(icon);
    setText(txt);
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

  public void setCursorAnim(Animation cursorAnim) {
    if (cursorAnim != null) {
      Insets insets = new Insets(0, CURSOR_LEFT_MARGIN, 0, CURSOR_RIGHT_MARGIN);
      animBox = new AnimationBox(cursorAnim, insets);
      boxes.add(animBox);
    }
  }

  public void setIcon(Image icon) {
    if (icon != null) {
      Insets insets = new Insets(0, ICON_LEFT_MARGIN, 0, ICON_RIGHT_MARGIN);
      imgBox = new ImageBox(icon, insets);
      boxes.add(imgBox);
    }
  }

  public void setText(String text) {
    if (text != null) {
      Insets insets = new Insets(0, FONT_HORIZONTAL_MARGIN, 0, FONT_HORIZONTAL_MARGIN);
      textBox = new TextBox(text, container.getDefaultFont(), insets);
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
   * Excess horizontal space goes to the textbox
   * All boxes have the same height as the menu item
   */
  private void initBoxSizes() {
    textBox.setWidth(getWidth() - animBox.getWidth() - imgBox.getWidth());

    for (Box b : boxes) {
      b.setHeight(getHeight());
    }
  }

  private void initBoxLocations() {
    int x = getX();
    int y = getY();

    animBox.setLocation(x, y);
    imgBox.setLocation(x + animBox.getWidth(), y);
    textBox.setLocation(x + animBox.getWidth() + imgBox.getWidth(), y);
  }

  private int getMaxWidth() {
    int totalWidth = 0;
    for (Box b : boxes) {
      totalWidth += b.getWidth();
    }
    return totalWidth;
  }

  private int getMaxHeight() {
    int highest = 0;

    for (Box b : boxes) {
      if (b.getHeight() > highest) highest = b.getHeight();
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
    if (isSelected())
      animBox.render(g);
    imgBox.render(g);
    textBox.render(g);
  }

  private void renderBorder(Graphics g) {
    g.setColor(Color.black);
    g.drawRect(getX(), getY(), getWidth(), getHeight());
  }
}
