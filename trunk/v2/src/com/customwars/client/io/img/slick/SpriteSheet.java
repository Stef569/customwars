package com.customwars.client.io.img.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.io.InputStream;

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
      int tilesAcross = ((getWidth() - tw) / (tw + spacing)) + 1;
      int tilesDown = ((getHeight() - th) / (th + spacing)) + 1;
      if ((getHeight() - th) % (th + spacing) != 0) {
        tilesDown++;
      }

      subImages = new Image[tilesAcross][tilesDown];
      for (int x = 0; x < tilesAcross; x++) {
        for (int y = 0; y < tilesDown; y++) {
          subImages[x][y] = getSprite(x, y);
        }
      }
    }
  }

  /**
   * Get the sub image cached in this sprite sheet
   *
   * @param x The x position in tiles of the image to get
   * @param y The y position in tiles of the image to get
   * @return The subimage at that location on the sheet
   */
  public Image getSubImage(int x, int y) {
    if ((x < 0) || (x >= subImages.length)) {
      throw new RuntimeException("SubImage out of sheet bounds: " + x + "," + y);
    }
    if ((y < 0) || (y >= subImages[0].length)) {
      throw new RuntimeException("SubImage out of sheet bounds: " + x + "," + y);
    }

    return subImages[x][y];
  }

  /**
   * Get a sprite at a particular cell on the sprite sheet
   *
   * @param x The x position of the cell on the sprite sheet
   * @param y The y position of the cell on the sprite sheet
   * @return The single image from the sprite sheet
   */
  public Image getSprite(int x, int y) {
    if ((x < 0) || (x >= subImages.length)) {
      throw new RuntimeException("SubImage out of sheet bounds: " + x + "," + y);
    }
    if ((y < 0) || (y >= subImages[0].length)) {
      throw new RuntimeException("SubImage out of sheet bounds: " + x + "," + y);
    }

    return getSubImage(x * (tw + spacing), y * (th + spacing), tw, th);
  }

  /**
   * Get the number of sprites across the sheet
   *
   * @return The number of sprites across the sheet
   */
  public int getHorizontalCount() {
    initImpl();

    return subImages.length;
  }

  /**
   * Get the number of sprites down the sheet
   *
   * @return The number of sprite down the sheet
   */
  public int getVerticalCount() {
    initImpl();

    return subImages[0].length;
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
}