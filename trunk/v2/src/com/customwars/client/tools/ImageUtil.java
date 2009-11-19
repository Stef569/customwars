package com.customwars.client.tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

/**
 * Contains various utility functions for java.awt.images
 *
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

  /**
   * Converts a java.awt.Image into an array of pixels
   */
  public static int[] convertToPixels(Image img) {
    int width = img.getWidth(null);
    int height = img.getHeight(null);
    int[] pixel = new int[width * height];

    PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      throw new IllegalStateException("Error: Interrupted Waiting for Pixels");
    }
    if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
      throw new IllegalStateException("Error: Image Fetch Aborted");
    }
    return pixel;
  }
}
