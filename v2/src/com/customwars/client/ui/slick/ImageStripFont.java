package com.customwars.client.ui.slick;

import com.customwars.client.io.img.slick.ImageStrip;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * A font implementation that will use the graphics inside a SpriteSheet for its data.
 * This is useful when your font has a fixed width and height for each character as
 * opposed to the more complex AngelCodeFont that allows different sizes and kerning
 * for each character.
 */
public class ImageStripFont implements Font {
  private ImageStrip strip;
  private char startingCharacter;
  private int charWidth;
  private int charHeight;
  private int horizontalCount;
  private int numChars;

  /**
   * Create a new font based on a ImageStrip. The ImageStrip should hold your
   * fixed-width character set in ASCII order. To only get upper-case characters
   * working you would usually set up a SpriteSheet with characters for these values:
   * <pre>
   *   !"#$%&'()*+,-./
   *  0123456789:;<=>?
   *  &#0064;ABCDEFGHIJKLMNO
   *  PQRSTUVWXYZ[\]^_<pre>
   * In this set, ' ' (SPACE) would be the startingCharacter of your characterSet.
   *
   * @param strip             The SpriteSheet holding the font data.
   * @param startingCharacter The first character that is defined in the SpriteSheet.
   */
  public ImageStripFont(ImageStrip strip, char startingCharacter) {
    this.strip = strip;
    this.startingCharacter = startingCharacter;
    horizontalCount = strip.getCols();
    charWidth = strip.getWidth() / horizontalCount;
    charHeight = strip.getHeight();
    numChars = horizontalCount;
  }

  /**
   * @see org.newdawn.slick.Font#drawString(float, float, java.lang.String)
   */
  public void drawString(float x, float y, String text) {
    drawString(x, y, text, Color.white);
  }

  /**
   * @see org.newdawn.slick.Font#drawString(float, float, java.lang.String, org.newdawn.slick.Color)
   */
  public void drawString(float x, float y, String text, Color color) {
    drawString(x, y, text, color, 0, text.length() - 1);
  }

  /**
   * @see Font#drawString(float, float, String, Color, int, int)
   */
  public void drawString(float x, float y, String text, Color color, int startIndex, int endIndex) {
    try {
      byte[] data = text.getBytes("US-ASCII");
      for (int i = 0; i < data.length; i++) {
        int index = data[i] - startingCharacter;
        if (index < numChars) {
          int col = (index % horizontalCount);

          if ((i >= startIndex) || (i <= endIndex)) {
            strip.getSubImage(col).draw(x + (i * charWidth), y, color);
          }
        }
      }
    } catch (UnsupportedEncodingException e) {
      // Should never happen, ASCII is supported pretty much anywhere
      Log.error(e);
    }
  }

  /**
   * @see org.newdawn.slick.Font#getHeight(java.lang.String)
   */
  public int getHeight(String text) {
    return charHeight;
  }

  /**
   * @see org.newdawn.slick.Font#getWidth(java.lang.String)
   */
  public int getWidth(String text) {
    return charWidth * text.length();
  }

  /**
   * @see org.newdawn.slick.Font#getLineHeight()
   */
  public int getLineHeight() {
    return charHeight;
  }
}
