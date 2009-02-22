package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.*;
import com.customwars.client.io.img.ImageLib;
import tools.IOUtil;

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

  private ImageLib imageLib;

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
        if (line.length() == 0)                         // blank line
          continue;
        if (line.startsWith(COMMENT_PREFIX))            // comment
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
    if (line == null) {
      throw new IllegalArgumentException("Line is null");
    }

    char ch = Character.toLowerCase(line.charAt(0));
    if (ch == SINGLE_IMAGE_SYMBOL) {                // 1 image, it's put into a strip
      loadSingleImage(line);
    } else if (ch == STRIP_IMAGE_SYMBOL) {          // an images strip
      loadStripImages(line);
    } else if (ch == MATRIX_IMAGE_SYMBOL) {         // a Matrix images accessable by row, col
      loadSpriteSheet(line);
    } else
      throw new IllegalArgumentException(ERR_READING_LINE + line + ", unknown Char: " + ch);
  }

  /**
   * format:
   * o <imgName> <fileName>
   */
  private void loadSingleImage(String line) throws NumberFormatException, IOException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() != 3)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
              " Usage " + SINGLE_IMAGE_SYMBOL + " <imgName> <fileName>");
    else {
      tokens.nextToken();    // skip command label
      String imgName = tokens.nextToken();
      String imgPath = tokens.nextToken();
      imageLib.loadAwtImg(imgName, imgPath);
      imageLib.loadSlickImage(imgName, imgPath);
    }
  }

  /**
   * format:
   * s <imgName> <fileName> <tileWidth> <tileHeight>
   */
  private void loadStripImages(String line) throws NumberFormatException, IOException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() != 5)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
              " Usage: " + STRIP_IMAGE_SYMBOL + " <imgName> <fileName> <tileWidth> <tileHeight>");
    else {
      tokens.nextToken();    // skip command label
      String imgName = tokens.nextToken();
      String imgPath = tokens.nextToken();
      int tileWidth = Integer.parseInt(tokens.nextToken());
      int tileHeight = Integer.parseInt(tokens.nextToken());

      imageLib.loadAwtImg(imgName, imgPath);
      imageLib.loadSlickImageStrip(imgName, tileWidth, tileHeight);
    }
  }

  /**
   * format:
   * m <imgName> <fileName> <tileWidth> <tileHeight>
   */
  private void loadSpriteSheet(String line) throws NumberFormatException, IOException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() != 5)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
              " Usage: " + MATRIX_IMAGE_SYMBOL + " <imgName> <fileName> <tileWidth> <tileHeight>");
    else {
      tokens.nextToken();    // skip command label
      String imgName = tokens.nextToken();
      String imgPath = tokens.nextToken();
      int tileWidth = Integer.parseInt(tokens.nextToken());
      int tileHeight = Integer.parseInt(tokens.nextToken());

      imageLib.loadAwtImg(imgName, imgPath);
      imageLib.loadSlickSpriteSheet(imgName, tileWidth, tileHeight);
    }
  }
}
