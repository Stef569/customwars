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
  private final ResourceManager resources;
  private final ImageLib imageLib;
  private final String fontPath;

  public FontParser(ResourceManager resources, String fontPath, String fontLoaderFileName, ImageLib imageLib) {
    super(ResourceLoader.getResourceAsStream(fontPath + fontLoaderFileName));
    this.resources = resources;
    this.fontPath = fontPath;
    this.imageLib = imageLib;
  }

  /**
   * format:
   * s  <fontName> <imgRef> <startCharacter>
   * a <fontName> <fntFile> <imgName>
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
        case TTF_FONT_SYMBOL:
          font = parseTTFFont(cmdScanner);
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

  @SuppressWarnings("unchecked")
  public Font parseTTFFont(Scanner cmdScanner) {
    String ttfFile = cmdScanner.next();
    String sizeAsTxt = cmdScanner.next();
    int fontSize = Integer.valueOf(sizeAsTxt);

    try {
      UnicodeFont unicodeFont = new UnicodeFont(fontPath + ttfFile, fontSize, false, false);
      unicodeFont.getEffects().add(new ColorEffect());
      unicodeFont.addAsciiGlyphs();
      unicodeFont.loadGlyphs();
      return unicodeFont;
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }
}
