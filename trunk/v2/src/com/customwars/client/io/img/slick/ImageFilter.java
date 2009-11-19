package com.customwars.client.io.img.slick;

import com.customwars.client.tools.ColorUtil;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;

import java.awt.Color;

/**
 * Filter an image by replacing known pixels with replacement pixels
 */
public class ImageFilter {
  private final RecolorData data;
  private final int darkenPercentage;   // The percentage that each px should be darkened to(0-100)
  private final Color recolorTo;

  public ImageFilter(RecolorData data, int darkenPercentage, Color recolorTo) {
    this.data = data;
    this.darkenPercentage = darkenPercentage;
    this.recolorTo = recolorTo;
  }

  public Image recolor() {
    if (canRecolor() && canRecolorTo(recolorTo)) {
      ImageBuffer buffer = new ImageBuffer(data.getBaseImgWidth(), data.getBaseImgHeight());
      int[] pixels = data.getBaseImgPixels();

      // For each pixel in the base Image
      for (int x = 0; x < data.getBaseImgWidth(); x++) {
        for (int y = 0; y < data.getBaseImgHeight(); y++) {
          int pixel = pixels[x + y * data.getBaseImgWidth()];
          int newPixel = filterRGB(pixel);

          // Convert rgb to 4 values, code copied from inside the java.awt.Color class
          int alpha = (newPixel >> 24) & 0xff;
          int red = (newPixel >> 16) & 0xFF;
          int green = (newPixel >> 8) & 0xFF;
          int blue = (newPixel) & 0xFF;
          buffer.setRGBA(x, y, red, green, blue, alpha);
        }
      }
      return buffer.getImage();
    } else {
      return null;
    }
  }

  public boolean canRecolor() {
    return !data.getBaseColor().equals(recolorTo) || canDarken();
  }

  public int filterRGB(int rgb) {
    int filteredRGB = isReplaceNeeded() ? data.getReplacementColor(rgb, recolorTo) : rgb;
    return canDarken() ? darkenColor(filteredRGB, -darkenPercentage) : filteredRGB;
  }

  private boolean isReplaceNeeded() {
    return !data.getBaseColor().equals(recolorTo);
  }

  public boolean canDarken() {
    return darkenPercentage > 0;
  }

  /**
   * Darkens the rgb given and returns it
   */
  public static int darkenColor(int rgb, int brightness) {

    // Get the individual colors
    int r = (rgb >> 16) & 0xff;
    int g = (rgb >> 8) & 0xff;
    int b = (rgb >> 0) & 0xff;

    // Apply brightness
    r += (brightness * r) / 100;
    b += (brightness * b) / 100;
    g += (brightness * g) / 100;

    // Check the boundaries
    r = Math.min(Math.max(0, r), 255);
    g = Math.min(Math.max(0, g), 255);
    b = Math.min(Math.max(0, b), 255);

    // Return the result
    return (rgb & 0xff000000) | (r << 16) | (g << 8) | (b);
  }

  /**
   * @return If this IamegeFilter can be recolored to the given color
   */
  private boolean canRecolorTo(Color color) {
    return data.canRecolorTo(color);
  }

  public String getBaseColorName() {
    return ColorUtil.toString(data.getBaseColor());
  }

  public boolean isBaseColor(Color color) {
    return data.getBaseColor().equals(color);
  }
}
