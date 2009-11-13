package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.ERR_READING_LINE;
import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.tools.IOUtil;
import com.customwars.client.tools.StringUtil;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class ImageParser {
  private final static Character SINGLE_IMAGE_SYMBOL = 'o';
  private final static Character STRIP_IMAGE_SYMBOL = 's';
  private final static Character MATRIX_IMAGE_SYMBOL = 'm';
  private final static String COMMENT_PREFIX = "//";

  private final ImageLib imageLib;
  private String fullImgPath;

  public ImageParser(ImageLib imageLib) {
    this.imageLib = imageLib;
  }

  /**
   * Loads a Image into the ImageLib by parsing a string command.
   *
   * @param line The command
   * @throws IOException when the image could not be found
   */
  public void loadImageByCmd(String line) throws IOException {
    parseCmd(line);
  }

  /**
   * @param stream the config file stream
   * @throws IOException when an image could not be loaded
   */
  public void loadConfigFile(InputStream stream) throws IOException {
    String line = "";
    BufferedReader br = new BufferedReader(new InputStreamReader(stream));

    try {
      while ((line = br.readLine()) != null) {
        if (line.length() == 0)
          continue;
        if (line.startsWith(COMMENT_PREFIX))
          continue;
        parseCmd(line);
      }
    } catch (IOException ex) {
      throw new IOException("Could not load image for line " + line);
    } finally {
      IOUtil.closeStream(stream);
    }
  }

  public void parseCmd(String line) throws IOException {
    char ch = Character.toLowerCase(line.charAt(0));
    try {
      if (ch == SINGLE_IMAGE_SYMBOL) {                // 1 image, it's put into a strip
        loadSingleImage(line);
      } else if (ch == STRIP_IMAGE_SYMBOL) {          // an images strip
        loadStripImages(line);
      } else if (ch == MATRIX_IMAGE_SYMBOL) {         // a Matrix images accessable by row, col
        loadSpriteSheet(line);
      } else
        throw new IllegalArgumentException(ERR_READING_LINE + line + ", unknown Char: " + ch);
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * format:
   * o <imgName> <fileName> (<storeAsAwt>)
   */
  private void loadSingleImage(String line) throws NumberFormatException, IOException, SlickException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 3)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage " + SINGLE_IMAGE_SYMBOL + " <imgName> <fileName>");
    else {
      tokens.nextToken();    // skip command label
      String imgName = tokens.nextToken();
      String imgPath = fullImgPath + tokens.nextToken();
      imageLib.addSlickImg(imgName, new Image(imgPath));
    }
  }

  /**
   * format:
   * s <imgName> <fileName> <tileWidth> <tileHeight> (<storeAsAwt>)
   */
  private void loadStripImages(String line) throws NumberFormatException, IOException, SlickException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 5)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage: " + STRIP_IMAGE_SYMBOL + " <imgName> <fileName> <tileWidth> <tileHeight>");
    else {
      tokens.nextToken();    // skip command label
      String imgName = tokens.nextToken();
      String imgPath = fullImgPath + tokens.nextToken();
      int tileWidth = Integer.parseInt(tokens.nextToken());
      int tileHeight = Integer.parseInt(tokens.nextToken());
      boolean recolor = false;
      if (tokens.hasMoreTokens()) {
        recolor = Boolean.parseBoolean(tokens.nextToken());
      }

      if (recolor) {
        imageLib.loadAwtImg(imgName, imgPath);
        imageLib.loadSlickImageStrip(imgName, tileWidth, tileHeight);
      } else {
        imageLib.addSlickImg(imgName, new ImageStrip(imgPath, tileWidth, tileHeight));
      }
    }
  }

  /**
   * format:
   * m <imgName> <fileName> <tileWidth> <tileHeight> (<storeAsAwt>)
   */
  private void loadSpriteSheet(String line) throws NumberFormatException, IOException, SlickException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 5)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage: " + MATRIX_IMAGE_SYMBOL + " <imgName> <fileName> <tileWidth> <tileHeight>");
    else {
      tokens.nextToken();    // skip command label
      String imgName = tokens.nextToken();
      String imgPath = fullImgPath + tokens.nextToken();
      int tileWidth = Integer.parseInt(tokens.nextToken());
      int tileHeight = Integer.parseInt(tokens.nextToken());
      boolean recolor = false;
      if (tokens.hasMoreTokens()) {
        recolor = Boolean.parseBoolean(tokens.nextToken());
      }

      if (recolor) {
        imageLib.loadAwtImg(imgName, imgPath);
        imageLib.loadSlickSpriteSheet(imgName, tileWidth, tileHeight);
      } else {
        imageLib.addSlickImg(imgName, new SpriteSheet(imgPath, tileWidth, tileHeight));
      }
    }
  }

  public void setImgPath(String fullImgPath) {
    fullImgPath = StringUtil.appendTrailingSuffix(fullImgPath, "/");
    this.fullImgPath = fullImgPath;
  }
}
