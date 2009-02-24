package com.customwars.client.io.img.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An Image that contains only 1 Row. Aka an ImageStrip
 * each img is accessible by index(col) number
 *
 * It can read sprite sheet images(multi rows, multi columns) and transform it into a single row.
 *
 * @author stefan
 */
public class ImageStrip extends Image {
  private List<Image> imgStrip = new ArrayList<Image>();
  private int tileWidth, tileHeight;

  public ImageStrip(int tileWidth, int tileHeight) {
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  public ImageStrip(String imgLocation, int tileWidth, int tileHeight) throws SlickException {
    this(imgLocation, tileWidth, tileHeight, null);
  }

  public ImageStrip(String imgLocation, int tileWidth, int tileHeight, Color transparentColor) throws SlickException {
    super(imgLocation, false, FILTER_NEAREST, transparentColor);
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  public ImageStrip(String imgName, InputStream imgStream, int tileWidth, int tileHeight) throws SlickException {
    super(imgStream, imgName, false);
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
  }

  protected void initImpl() {
    loadStripImageArray();
  }

  /**
   * Extract the individual images from the image
   * We assume that each image has equal height/width.
   */
  private void loadStripImageArray() {
    imgStrip.clear();
    if (tileWidth <= 0) throw new IllegalArgumentException("Invalid input tileWidth <=0");
    if (tileHeight <= 0) throw new IllegalArgumentException("Invalid input tileHeight <=0");

    int cols = getWidth() / tileWidth;
    int rows = getHeight() / tileHeight;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        imgStrip.add(getSubImage(col * tileWidth, row * tileHeight, tileWidth, tileHeight));
      }
    }
  }

  /**
   * Get the sub image cached in this imageStrip
   *
   * @param col The column position in tiles of the image to get, starting at 0
   * @return The subimage at that location on the strip
   */
  public Image getSubImage(int col) {
    init();

    if (col < 0 || col >= imgStrip.size()) {
      throw new RuntimeException("SubImage out of Image strip bounds: " + col + "," + imgStrip.size());
    }

    return imgStrip.get(col);
  }

  public int getCols() {
    init();

    return imgStrip.size();
  }

  public Image[] toArray() {
    init();
    return imgStrip.toArray(new Image[0]);
  }

  public int getTileWidth() {
    return tileWidth;
  }

  public int getTileHeight() {
    return tileHeight;
  }
}
