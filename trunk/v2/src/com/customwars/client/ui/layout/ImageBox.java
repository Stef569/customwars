package com.customwars.client.ui.layout;

import com.customwars.client.ui.GUI;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.Insets;
import java.awt.Point;

/**
 * An Image box is an image within a box
 * The box can be larger then the image by having insets
 */
public class ImageBox extends Box {
  private Image img;
  private Insets insets;

  /**
   * Create an empty ImageBox
   */
  public ImageBox() {
    this(null, new Insets(0, 0, 0, 0));
  }

  public ImageBox(Image img) {
    this(img, new Insets(0, 0, 0, 0));
  }

  public ImageBox(Image img, Insets insets) {
    this.insets = insets;
    this.img = img;
    setImage(img);
  }

  /**
   * Change the image in this box
   * the width and height of the box are adjusted to the new image size
   * the center is reset
   */
  public void setImage(Image img) {
    this.img = img;
    setWidth(getBoxWidth());
    setHeight(getBoxHeight());
  }

  @Override
  public void init() {
    Point center = GUI.getCenteredRenderPoint(getImgWidth(), getImgHeight(), getWidth(), getHeight());
    setCenter(center.x, center.y);
  }

  @Override
  public void renderImpl(Graphics g) {
    if (img != null) {
      g.drawImage(img, getX() + getCenterX(), getY() + getCenterY());
    }
  }

  public int getBoxWidth() {
    return insets.left + getImgWidth() + insets.right;
  }

  public int getBoxHeight() {
    return insets.top + getImgHeight() + insets.bottom;
  }

  private int getImgWidth() {
    return img != null ? img.getWidth() : 0;
  }

  private int getImgHeight() {
    return img != null ? img.getHeight() : 0;
  }
}
