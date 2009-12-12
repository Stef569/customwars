package com.customwars.client.ui.layout;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.awt.Insets;

public class TextBox extends Box {
  private String txt;
  private Font font;
  private Insets insets;

  public TextBox() {
    this.txt = "";
  }

  public TextBox(String txt, Font font) {
    this(txt, font, new Insets(0, 0, 0, 0));
  }

  public TextBox(String txt, Font font, Insets insets) {
    setText(txt);
    this.font = font;
    this.insets = insets;
    width = getTextBoxWidth();
    height = getTextBoxHeight();
  }

  public void setText(String text) {
    this.txt = text;
    if (!txt.equals(text))
      init();
  }

  protected void init() {
    center.x = center(width, getTextWidth());
    center.y = center(height, getTextHeight());
  }

  public void renderImpl(Graphics g) {
    if (txt != null) {
      if (font != null) {
        font.drawString(x + center.x, y + center.y, txt);
      } else {
        g.drawString(txt, x + center.x, y + center.y);
      }
    }
  }

  private int getTextBoxWidth() {
    return insets.left + getTextWidth() + insets.right;
  }

  private int getTextWidth() {
    if (txt != null && font != null) {
      return font.getWidth(txt);
    } else {
      return 0;
    }
  }

  private int getTextBoxHeight() {
    return insets.top + getTextHeight() + insets.bottom;
  }

  private int getTextHeight() {
    if (txt != null && font != null) {
      return font.getLineHeight();
    } else {
      return 0;
    }
  }
}
