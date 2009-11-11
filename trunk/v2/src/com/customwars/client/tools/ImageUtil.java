package com.customwars.client.tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * @author stefan
 */
public class ImageUtil {
  /**
   * Converts an Image to a BufferedImage
   */
  public static BufferedImage convertToBufferedImg(Image im) {
    BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics bg = bi.getGraphics();
    bg.drawImage(im, 0, 0, null);
    bg.dispose();
    return bi;
  }
}
