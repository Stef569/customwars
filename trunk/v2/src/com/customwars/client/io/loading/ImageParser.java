package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.ERR_READING_LINE;
import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;
import com.customwars.client.io.img.ImageLib;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Parses a text command of the following Formats:
 * o <imgName> <fileName>                             // a single image
 * s <imgName> <fileName> <tileWidth> <tileHeight>    // an images strip
 * m <imgName> <fileName> <tileWidth> <tileHeight>    // a matrix of images as an array
 * into a function that will load an img.
 *
 * @author stefan
 */
public class ImageParser extends LineParser {
  private final static Character SINGLE_IMAGE_SYMBOL = 'o';
  private final static Character STRIP_IMAGE_SYMBOL = 's';
  private final static Character MATRIX_IMAGE_SYMBOL = 'm';

  private final ImageLib imageLib;
  private final String imgPath;

  public ImageParser(ImageLib imageLib, String imagePath, String imageLoaderFileName) {
    super(ResourceLoader.getResourceAsStream(imagePath + imageLoaderFileName));
    this.imageLib = imageLib;
    this.imgPath = imagePath;
  }

  public void parseLine(String line) {
    try {
      parseCmd(line);
    } catch (IOException ex) {
      throw new RuntimeException("Could not load image for line " + line, ex);
    }
  }

  public void parseCmd(String line) throws IOException {
    char ch = Character.toLowerCase(line.charAt(0));
    if (ch == SINGLE_IMAGE_SYMBOL) {                // 1 image
      loadSingleImage(line);
    } else if (ch == STRIP_IMAGE_SYMBOL) {          // an images strip
      loadStripImage(line);
    } else if (ch == MATRIX_IMAGE_SYMBOL) {         // a Matrix images accessable by row, col
      loadSpriteSheet(line);
    } else
      throw new IllegalArgumentException(ERR_READING_LINE + line + ", unknown Char: " + ch);
  }

  /**
   * format:
   * o <imgName> <fileName> (<storeAsAwt>)
   */
  private void loadSingleImage(String line) {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 3)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage " + SINGLE_IMAGE_SYMBOL + " <imgName> <fileName>");
    else {
      tokens.nextToken();    // skip command label
      String imgRef = tokens.nextToken();
      String imgPath = this.imgPath + tokens.nextToken();
      imageLib.loadSlickImage(imgRef, imgPath);
    }
  }

  /**
   * format:
   * s <imgName> <fileName> <tileWidth> <tileHeight> (<storeAsAwt>)
   */
  private void loadStripImage(String line) {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 5)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage: " + STRIP_IMAGE_SYMBOL + " <imgName> <fileName> <tileWidth> <tileHeight>");
    else {
      tokens.nextToken();    // skip command label
      String imgRef = tokens.nextToken();
      String imgPath = this.imgPath + tokens.nextToken();
      int tileWidth = Integer.parseInt(tokens.nextToken());
      int tileHeight = Integer.parseInt(tokens.nextToken());
      boolean recolor = false;
      if (tokens.hasMoreTokens()) {
        recolor = Boolean.parseBoolean(tokens.nextToken());
      }

      if (recolor) {
        imageLib.getRecolorManager().setBaseRecolorImageStrip(imgRef, imgPath, tileWidth, tileHeight);
      } else {
        imageLib.loadSlickImageStrip(imgRef, imgPath, tileWidth, tileHeight);
      }
    }
  }

  /**
   * format:
   * m <imgName> <fileName> <tileWidth> <tileHeight> (<storeAsAwt>)
   */
  private void loadSpriteSheet(String line) {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 5)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage: " + MATRIX_IMAGE_SYMBOL + " <imgName> <fileName> <tileWidth> <tileHeight>");
    else {
      tokens.nextToken();    // skip command label
      String imgRef = tokens.nextToken();
      String imgPath = this.imgPath + tokens.nextToken();
      int tileWidth = Integer.parseInt(tokens.nextToken());
      int tileHeight = Integer.parseInt(tokens.nextToken());
      boolean recolor = false;
      if (tokens.hasMoreTokens()) {
        recolor = Boolean.parseBoolean(tokens.nextToken());
      }

      if (recolor) {
        imageLib.getRecolorManager().setBaseRecolorSpriteSheet(imgRef, imgPath, tileWidth, tileHeight);
      } else {
        imageLib.loadSlickSpriteSheet(imgRef, imgPath, tileWidth, tileHeight);
      }
    }
  }
}
