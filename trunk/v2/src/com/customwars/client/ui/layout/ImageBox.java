package com.customwars.client.ui.layout;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.Insets;

public class ImageBox extends Box {
  private Image img;
  private Insets insets;

  public ImageBox() {
    this(null, new Insets(0, 0, 0, 0));
  }

  public ImageBox(Image img) {
    this(img, new Insets(0, 0, 0, 0));
  }

  public ImageBox(Image img, Insets insets) {
    this.img = img;
    this.insets = insets;
    init();
  }

  protected void init() {
    width = getImgBoxWith();
    height = getImgBoxHeight();
    center.x = center(width, getImgWidth());
    center.y = center(height, getImgHeight());
  }

  public void renderImpl(Graphics g) {
    if (img != null)
      g.drawImage(img, x + center.x, y + center.y);
  }

  private int getImgBoxWith() {
    int totalWidth;
    int imgWidth = getImgWidth();

    if (width < imgWidth) {
      totalWidth = imgWidth;
    } else {
      totalWidth = width;
    }
    return insets.left + totalWidth + insets.right;
  }

  private int getImgBoxHeight() {
    int totalHeight;
    int imgHeight = getImgHeight();

    if (height < imgHeight) {
      totalHeight = imgHeight;
    } else {
      totalHeight = height;
    }
    return insets.top + totalHeight + insets.bottom;
  }

  private int getImgWidth() {
    if (img != null) {
      return img.getWidth();
    } else {
      return 0;
    }
  }

  private int getImgHeight() {
    if (img != null) {
      return img.getHeight();
    } else {
      return 0;
    }
  }

  public void setImage(Image img) {
    this.img = img;
    init();
  }
}
