package com.customwars.client.ui;

import com.customwars.client.ui.slick.MouseOverArea;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Point;

/**
 * A single Menu item containing:
 * An animated cursor, Icon and text
 * Each of the above is optional and rendered centered in a box, each box is the dimension of the Image/text + the margins
 * The menu item height is put to the highest box height.
 *
 * @author stefan
 */
public class MenuItem extends MouseOverArea {
  private static final int CURSOR_LEFT_MARGIN = 1, CURSOR_RIGHT_MARGIN = 5;
  private static final int ICON_LEFT_MARGIN = 1, ICON_RIGHT_MARGIN = 5;
  private static final int FONT_HORIZONTAL_MARGIN = 10;
  private static final int FONT_VERTICAL_MARGIN = 3;
  private static final int MENU_ITEM_MARGIN = 5;
  private Color textColor = Color.white;

  private Animation cursorAnim;
  private Image icon;
  private String txt;

  private Point cursorPoint, iconPoint, txtPoint;
  private Font font;

  public MenuItem(GUIContext container) {
    super(container);
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
    this.font = container.getDefaultFont();
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
   * used by the MouseOverArea
   */
  private void initArea() {
    int x = getX() - MENU_ITEM_MARGIN;
    int y = getY() - MENU_ITEM_MARGIN;
    int width = getTotalWidth() + MENU_ITEM_MARGIN;
    int height = getTotalHeight() + MENU_ITEM_MARGIN;
    setArea(x, y, width, height);
  }

  public void setCursorAnim(Animation cursorAnim) {
    this.cursorAnim = cursorAnim;
  }

  public void setIcon(Image icon) {
    this.icon = icon;
  }

  public void setText(String text) {
    this.txt = text;
  }

  public void setTextColor(Color txtColor) {
    this.textColor = txtColor;
  }

  /**
   * Init the positions of the cursor, icon and text
   */
  public void initPositions() {
    int cursorBoxWidth = getCursorBoxWith();
    int iconBoxWidth = getIconBoxWith();
    int textBoxWidth = getTextBoxWith();

    int cursorCenterX = center(cursorBoxWidth, getCursorWidth());
    int cursorCenterY = center(getHeight(), getCursorHeight());

    int iconCenterX = center(iconBoxWidth, getIconWidth());
    int iconCenterY = center(getHeight(), getIconHeight());

    int txtCenterX = center(textBoxWidth, getTextWidth());
    int txtCenterY = center(getHeight(), getTextHeight());

    cursorPoint = new Point(getX() + cursorCenterX, getY() + cursorCenterY);
    iconPoint = new Point(getX() + cursorBoxWidth + iconCenterX, getY() + iconCenterY);
    txtPoint = new Point(getX() + cursorBoxWidth + iconBoxWidth + txtCenterX, getY() + txtCenterY);
  }

  /**
   * Center inner inside total
   *
   * @return The left top point to render inner inside the box
   */
  private int center(int box, int inner) {
    if (inner < box) {
      return box / 2 - inner / 2;
    } else {
      return 0;
    }
  }


  private int getTotalWidth() {
    int totalWidth = 0;
    totalWidth += getCursorBoxWith();
    totalWidth += getIconBoxWith();
    totalWidth += getTextBoxWith();
    return totalWidth;
  }

  private int getTotalHeight() {
    return findHighestValue(getCursorHeight(), getIconHeight(), getTextHeight());
  }

  private int getCursorBoxWith() {
    return CURSOR_LEFT_MARGIN + getCursorWidth() + CURSOR_RIGHT_MARGIN;
  }

  private int getIconBoxWith() {
    return ICON_LEFT_MARGIN + getIconWidth() + ICON_RIGHT_MARGIN;
  }

  private int getTextBoxWith() {
    return getTextWidth();
  }

  private int getCursorHeight() {
    if (cursorAnim != null) {
      return cursorAnim.getHeight();
    } else {
      return 0;
    }
  }

  private int getIconHeight() {
    if (icon != null) {
      return icon.getHeight();
    } else {
      return 0;
    }
  }

  private int getTextHeight() {
    if (txt != null && font != null) {
      return font.getLineHeight() + FONT_VERTICAL_MARGIN;
    } else {
      return 0;
    }
  }

  private int getCursorWidth() {
    if (cursorAnim != null) {
      return cursorAnim.getWidth();
    } else {
      return 0;
    }
  }

  private int getIconWidth() {
    if (icon != null) {
      return icon.getWidth();
    } else {
      return 0;
    }
  }

  private int getTextWidth() {
    if (txt != null) {
      return font.getWidth(txt) + FONT_HORIZONTAL_MARGIN;
    } else {
      return 0;
    }
  }

  private int findHighestValue(int... values) {
    int highest = 0;

    for (int val : values) {
      if (val > highest) highest = val;
    }
    return highest;
  }

  public void render(GUIContext container, Graphics g) {
    super.render(container, g);
    g.setColor(textColor);
    if (cursorAnim != null && isSelected()) cursorAnim.draw(cursorPoint.x, cursorPoint.y);
    if (icon != null) g.drawImage(icon, iconPoint.x, iconPoint.y);
    if (txt != null) g.drawString(txt, txtPoint.x, txtPoint.y);
  }
}
