package com.customwars.client.io.loading;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.ui.slick.ImageStripFont;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.ResourceLoader;

import java.util.Scanner;
import java.util.StringTokenizer;

import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;

/**
 * Load Fonts and add them to the ResourceManager
 */
public class FontParser extends LineParser {
  private static final char IMAGE_STRIP_FONT_SYMBOL = 's';
  private static final char ANGELCODE_FONT_SYMBOL = 'a';
  private static final char TTF_FONT_SYMBOL = 't';
  private static final char USE_CACHED_FONT_SYMBOL = 'r';
  private final ResourceManager resources;
  private final ImageLib imageLib;
  private final String fontPath;

  /**
   * Create a font loader that will load fonts on request see #parseLine (String)
   */
  public FontParser(ResourceManager resources, String fontPath, ImageLib imageLib) {
    super(null);
    this.resources = resources;
    this.fontPath = fontPath;
    this.imageLib = imageLib;
  }

  /**
   * Create a font loader that will load fonts from the fontLoaderFile
   * The fontLoaderFile is expected to be located in the fontPath
   */
  public FontParser(ResourceManager resources, String fontPath, String fontLoaderFileName, ImageLib imageLib) {
    super(ResourceLoader.getResourceAsStream(fontPath + fontLoaderFileName));
    this.resources = resources;
    this.fontPath = fontPath;
    this.imageLib = imageLib;
  }

  /**
   * format:
   * s <fontName> <imgRef> <startCharacter>
   * a <fontName> <fntFile> <imgName>
   * t <fontName> <ttf>
   * r <fontName> <=> <font name to reuse>
   */
  public void parseLine(String line) {
    StringTokenizer tokens = new StringTokenizer(line);
    Scanner cmdScanner = new Scanner(line);

    if (!(tokens.countTokens() >= 4))
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for " + line);
    else {
      char fontType = Character.toLowerCase(cmdScanner.next().charAt(0));
      String fontName = cmdScanner.next();
      Font font = loadFont(line, cmdScanner, fontType);
      resources.addFont(fontName, font);
    }
  }

  private Font loadFont(String line, Scanner cmdScanner, char fontType) {
    Font font;
    switch (fontType) {
      case IMAGE_STRIP_FONT_SYMBOL:
        font = parseImageStripFont(cmdScanner);
        break;
      case ANGELCODE_FONT_SYMBOL:
        font = parseAngelCodeFont(cmdScanner);
        break;
      case TTF_FONT_SYMBOL:
        font = parseTTFFont(cmdScanner);
        break;
      case USE_CACHED_FONT_SYMBOL:
        cmdScanner.next();  // Skip '='
        font = resources.getFont(cmdScanner.next());
        break;
      default:
        throw new IllegalArgumentException(String.format(
          "Don't know about font type %s use %s(Strip) %s(AngelCodeFont) %s(TTF) instead problem line: %s",
          fontType, IMAGE_STRIP_FONT_SYMBOL, ANGELCODE_FONT_SYMBOL, TTF_FONT_SYMBOL, line));
    }
    return font;
  }

  private Font parseImageStripFont(Scanner cmdScanner) {
    String imgName = cmdScanner.next();
    ImageStrip imageStrip = (ImageStrip) imageLib.getSlickImg(imgName);
    char startCharacter = cmdScanner.next().charAt(0);
    return new ImageStripFont(imageStrip, startCharacter);
  }

  private Font parseAngelCodeFont(Scanner cmdScanner) {
    String fntFile = cmdScanner.next();
    String imgName = cmdScanner.next();

    try {
      return new AngelCodeFont(fontPath + fntFile, fontPath + imgName, false);
    } catch (SlickException ex) {
      throw new RuntimeException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  private Font parseTTFFont(Scanner cmdScanner) {
    String ttfFile = cmdScanner.next();
    int fontSize = Integer.valueOf(cmdScanner.next());

    try {
      UnicodeFont unicodeFont = new UnicodeFont(fontPath + ttfFile, fontSize, false, false);
      unicodeFont.getEffects().add(new ColorEffect());
      unicodeFont.addAsciiGlyphs();
      unicodeFont.loadGlyphs();
      unicodeFont.setDisplayListCaching(false);
      return unicodeFont;
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }
}
