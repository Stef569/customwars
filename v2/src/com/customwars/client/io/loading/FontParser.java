package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.ui.slick.ImageStripFont;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Load Fonts to the ResourceManager
 */
public class FontParser extends LineParser {
  private final ResourceManager resources;
  private final ImageLib imageLib;
  private final String fontPath;
  private static final char IMAGE_STRIP_FONT_SYMBOL = 's';
  private static final char ANGELCODE_FONT_SYMBOL = 'a';

  public FontParser(ResourceManager resources, String fontPath, String fontLoaderFileName, ImageLib imageLib) {
    super(ResourceLoader.getResourceAsStream(fontPath + fontLoaderFileName));
    this.resources = resources;
    this.fontPath = fontPath;
    this.imageLib = imageLib;
  }

  /**
   * format:
   * S  fontName imgName startCharacter
   * A fontName fntFile imgName
   */
  public void parseLine(String line) {
    StringTokenizer tokens = new StringTokenizer(line);
    Scanner cmdScanner = new Scanner(line);

    if (!(tokens.countTokens() >= 4))
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for " + line);
    else {
      char fontType = Character.toLowerCase(cmdScanner.next().charAt(0));
      String fontName = cmdScanner.next();

      Font font;
      switch (fontType) {
        case IMAGE_STRIP_FONT_SYMBOL:
          font = parseImageStripFont(cmdScanner);
          break;
        case ANGELCODE_FONT_SYMBOL:
          font = parseAngelCodeFont(cmdScanner);
          break;
        default:
          throw new IllegalArgumentException("Don't know about font type " + fontType + " use s(Strip) or a(AngelCodeFont) instead, Problem line: " + line);
      }
      resources.addFont(fontName, font);
    }
  }

  private Font parseImageStripFont(Scanner cmdScanner) {
    String imgName = cmdScanner.next();
    ImageStrip imageStrip = (ImageStrip) imageLib.getSlickImg(imgName);
    char startCharacter = cmdScanner.next().charAt(0);
    return new ImageStripFont(imageStrip, startCharacter);
  }

  public Font parseAngelCodeFont(Scanner cmdScanner) {
    String fntFile = cmdScanner.next();
    String imgName = cmdScanner.next();

    try {
      return new AngelCodeFont(fontPath + fntFile, fontPath + imgName);
    } catch (SlickException ex) {
      throw new RuntimeException(ex);
    }
  }
}
