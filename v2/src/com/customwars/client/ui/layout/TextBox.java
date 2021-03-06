package com.customwars.client.ui.layout;

import com.customwars.client.tools.Args;
import com.customwars.client.ui.GUI;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.awt.Insets;
import java.awt.Point;

/**
 * Render text within a box.
 * Based on the horizontal align the text is rendered to the left, right or center.
 * The total dimension of a box is the dimension of the text + the insets.
 */
public class TextBox extends Box {
  private String txt;
  private Font font;
  private Insets insets;

  public TextBox(String txt, Font font) {
    this(txt, font, new Insets(0, 0, 0, 0));
  }

  public TextBox(String txt, Font font, Insets insets) {
    Args.checkForNull(font);
    Args.checkForNull(insets);
    this.insets = insets;
    this.font = font;
    setText(txt);
  }

  /**
   * Change the text in this box
   * the width and height of the box are adjusted to the new text size
   * the center is reset
   */
  public void setText(String text) {
    this.txt = text;
    setWidth(getBoxWidth());
    setHeight(getBoxHeight());
    init();
  }

  @Override
  public void init() {
    Point center = GUI.getCenteredRenderPoint(getTextWidth(), getTextHeight(), getWidth(), getHeight());
    setCenter(center.x, center.y);
  }

  @Override
  public void renderImpl(Graphics g) {
    if (txt != null) {
      Font origFont = g.getFont();
      g.setFont(font);
      renderText(g);
      g.setFont(origFont);
    }
  }

  private void renderText(Graphics g) {
    int y = getY() + getCenterY();
    switch (super.getAlignement()) {
      case LEFT:
        g.drawString(txt, getX(), y);
        break;
      case CENTER:
        g.drawString(txt, getX() + getCenterX(), y);
        break;
      case RIGHT:
        g.drawString(txt, getWidth() - getBoxWidth(), y);
        break;
    }
  }

  private int getBoxWidth() {
    return insets.left + getTextWidth() + insets.right;
  }

  private int getBoxHeight() {
    return insets.top + getTextHeight() + insets.bottom;
  }

  private int getTextWidth() {
    return txt != null && font != null ? font.getWidth(txt) : 0;
  }

  private int getTextHeight() {
    return txt != null && font != null ? font.getLineHeight() : 0;
  }
}
