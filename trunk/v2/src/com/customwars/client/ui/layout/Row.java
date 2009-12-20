package com.customwars.client.ui.layout;

import com.customwars.client.tools.NumberUtil;
import org.newdawn.slick.Graphics;

/**
 * A Row contains 2 boxes
 * an Image box and a text box with horizontalSpacing between them.
 * The row always has the height of the tallest box
 */
public class Row extends Box {
  private final Box imageBox;
  private final TextBox textBox;
  private int rowHeight, rowWidth;
  private int horizontalSpacing;

  public Row(Box box, TextBox textBox) {
    this.imageBox = box;
    this.textBox = textBox;
    init();
  }

  @Override
  public void init() {
    rowHeight = NumberUtil.findHighest(imageBox.getHeight(), textBox.getHeight());
    setHeight(rowHeight);
    rowWidth = imageBox.getWidth() + textBox.getWidth();
  }

  @Override
  public void renderImpl(Graphics g) {
    imageBox.render(g);
    textBox.render(g);
  }

  @Override
  public void setLocation(int x, int y) {
    super.setLocation(x, y);
    imageBox.setLocation(x, y);
    textBox.setLocation(x + imageBox.getWidth() + horizontalSpacing, y);
  }

  @Override
  public void setHeight(int height) {
    imageBox.setHeight(height);
    textBox.setHeight(height);
    rowHeight = height;
  }

  @Override
  public int getHeight() {
    return rowHeight;
  }

  @Override
  public int getWidth() {
    return rowWidth;
  }

  public void setText(String text) {
    textBox.setText(text);
    init();
  }

  public void setHorizontalSpacing(int horizontalSpacing) {
    this.horizontalSpacing = horizontalSpacing;
  }
}