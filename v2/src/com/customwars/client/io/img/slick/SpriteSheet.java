package com.customwars.client.io.img.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A sheet of sprites that can be drawn individually
 *
 * @author Kevin Glass
 */
public class SpriteSheet extends Image {
  /**
   * The width of a single element in pixels
   */
  private int tw;
  /**
   * The height of a single element in pixels
   */
  private int th;
  /**
   * Subimages
   */
  private Image[][] subImages;
  /**
   * The spacing between tiles
   */
  private int spacing;

  /**
   * Create a new empty sprite sheet
   *
   * @param tw The width of the tiles on the sheet
   * @param th The height of the tiles on the sheet
   */
  public SpriteSheet(int tw, int th) {
    this.tw = tw;
    this.th = th;
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param image The image to based the sheet of
   * @param tw    The width of the tiles on the sheet
   * @param th    The height of the tiles on the sheet
   */
  public SpriteSheet(Image image, int tw, int th) {
    super(image);

    this.tw = tw;
    this.th = th;

    // call init manually since constructing from an image will have previously initialised
    // from incorrect values
    initImpl();
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param image   The image to based the sheet of
   * @param tw      The width of the tiles on the sheet
   * @param th      The height of the tiles on the sheet
   * @param spacing The spacing between tiles
   */
  public SpriteSheet(Image image, int tw, int th, int spacing) {
    super(image);

    this.tw = tw;
    this.th = th;
    this.spacing = spacing;

    // call init manually since constructing from an image will have previously initialised
    // from incorrect values
    initImpl();
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param ref     The location of the sprite sheet to load
   * @param tw      The width of the tiles on the sheet
   * @param th      The height of the tiles on the sheet
   * @param spacing The spacing between tiles
   * @throws SlickException Indicates a failure to load the image
   */
  public SpriteSheet(String ref, int tw, int th, int spacing) throws SlickException {
    this(ref, tw, th, null, spacing);
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param ref The location of the sprite sheet to load
   * @param tw  The width of the tiles on the sheet
   * @param th  The height of the tiles on the sheet
   * @throws SlickException Indicates a failure to load the image
   */
  public SpriteSheet(String ref, int tw, int th) throws SlickException {
    this(ref, tw, th, null);
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param ref The location of the sprite sheet to load
   * @param tw  The width of the tiles on the sheet
   * @param th  The height of the tiles on the sheet
   * @param col The colour to treat as transparent
   * @throws SlickException Indicates a failure to load the image
   */
  public SpriteSheet(String ref, int tw, int th, Color col) throws SlickException {
    this(ref, tw, th, col, 0);
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param ref     The location of the sprite sheet to load
   * @param tw      The width of the tiles on the sheet
   * @param th      The height of the tiles on the sheet
   * @param col     The colour to treat as transparent
   * @param spacing The spacing between tiles
   * @throws SlickException Indicates a failure to load the image
   */
  public SpriteSheet(String ref, int tw, int th, Color col, int spacing) throws SlickException {
    super(ref, false, FILTER_NEAREST, col);

    this.tw = tw;
    this.th = th;
    this.spacing = 0;
  }

  /**
   * Create a new sprite sheet based on a image location
   *
   * @param name The name to give to the image in the image cache
   * @param ref  The stream from which we can load the image
   * @param tw   The width of the tiles on the sheet
   * @param th   The height of the tiles on the sheet
   * @throws SlickException Indicates a failure to load the image
   */
  public SpriteSheet(String name, InputStream ref, int tw, int th) throws SlickException {
    super(ref, name, false);

    this.tw = tw;
    this.th = th;
  }

  /**
   * @see org.newdawn.slick.Image#initImpl()
   */
  protected void initImpl() {
    if (subImages == null) {
      int cols = (int) (Math.floor(getWidth() / (tw + spacing)));
      int rows = (int) (Math.floor(getHeight() / (th + spacing)));

      subImages = new Image[rows][cols];
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          subImages[row][col] = getSprite(col, row);
        }
      }
    }
  }

  /**
   * Get the sub image from this sprite sheet
   *
   * @param col The col position in tiles of the image to get
   * @param row The row position in tiles of the image to get
   * @return The subimage at that location on the sheet
   */
  public Image getSubImage(int col, int row) {
    if ((row < 0) || (row >= subImages.length)) {
      throw new RuntimeException("SubImage row out of sheet bounds: " + row + "/" + subImages.length);
    }
    if ((col < 0) || (col >= subImages[0].length)) {
      throw new RuntimeException("SubImage col out of sheet bounds: " + col + "/" + subImages[0].length);
    }

    return subImages[row][col];
  }

  /**
   * Grabs the entries of an inner List(row or col) in the this spritesheet
   *
   * @param startPoint Start point of the inner List
   * @param endPoint   End point of the ineer list
   * @return the entries of the inner List from startPoint to endPoint
   * @throws IndexOutOfBoundsException if the inner List is bigger then the sheet bounds
   */
  public List<Image> getInnerList(Point startPoint, Point endPoint) {
    final List<Image> result = new ArrayList<Image>();
    if (isOnSameRow(startPoint, endPoint)) {
      for (int colIndex = startPoint.x; colIndex <= endPoint.x; colIndex++) {
        result.add(getSubImage(colIndex, startPoint.y));
      }
    } else {
      for (int rowIndex = startPoint.y; rowIndex <= endPoint.y; rowIndex++) {
        result.add(getSubImage(rowIndex, startPoint.x));
      }
    }
    return result;
  }

  /**
   * 2 points are in the same row if their y value are the same
   *
   * @return if 2 points are in the same row
   */
  private boolean isOnSameRow(Point a, Point b) {
    return a.y == b.y;
  }

  /**
   * Get a sprite at a particular cell on the sprite sheet
   *
   * @param col The col position of the cell on the sprite sheet
   * @param row The row position of the cell on the sprite sheet
   * @return The single image from the sprite sheet
   */
  public Image getSprite(int col, int row) {
    if ((row < 0) || (row >= subImages.length)) {
      throw new RuntimeException("SubImage row out of sheet bounds: " + col + "," + row);
    }
    if ((col < 0) || (col >= subImages[0].length)) {
      throw new RuntimeException("SubImage col out of sheet bounds: " + col + "," + row);
    }

    return getSubImage(col * (tw + spacing), row * (th + spacing), tw, th);
  }

  /**
   * Get the number of sprites across the sheet
   *
   * @return The number of sprites across the sheet
   */
  public int getHorizontalCount() {
    initImpl();

    return subImages[0].length;
  }

  /**
   * Get the number of sprites down the sheet
   *
   * @return The number of sprite down the sheet
   */
  public int getVerticalCount() {
    initImpl();

    return subImages.length;
  }

  /**
   * Render a sprite when this sprite sheet is in use.
   *
   * @param x  The x position to render the sprite at
   * @param y  The y position to render the sprite at
   * @param sx The x location of the cell to render
   * @param sy The y location of the cell to render
   * @see #startUse()
   * @see #endUse()
   */
  public void renderInUse(int x, int y, int sx, int sy) {
    subImages[sx][sy].drawEmbedded(x, y, tw, th);
  }

  /**
   * @see org.newdawn.slick.Image#endUse()
   */
  public void endUse() {
    super.endUse();
  }

  /**
   * @see org.newdawn.slick.Image#startUse()
   */
  public void startUse() {
    super.startUse();
  }

  public int getTileHeight() {
    return th;
  }

  public int getTileWidth() {
    return tw;
  }
}
