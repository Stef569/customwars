package com.customwars.client.io.img.awt;

import org.apache.log4j.Logger;
import tools.ColorUtil;

import java.awt.Color;
import java.awt.image.RGBImageFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ImgFilter provides a way to recolor pixels from a souce image to a new Image
 * It uses known colors from the image and replace colors to replace them.
 * Each known color has a replace color
 * There are multiple replace colors for multiple colors.
 * ie:
 * an image with white, green and transparant pixels
 * known colors : (White, Green)
 *
 * replacementColors:
 * Red (Red, Black)
 * Blue (Blue, Black)
 *
 * ignoredColors:
 * transparant(default)
 *
 * When we set the replacementColor to Blue it will
 * skip the transparant pixels (ignored)
 * replace white pixels with blue (replacement)
 * and green pixels with black (replacement)
 *
 * When we set the replacementColor to Red it will
 * skip transparant pixels (ignored)
 * replace white pixels with Red (replacement)
 * and green pixels with black (replacement)
 *
 * Usage:
 * FilteredImageSource imgFilterSrc = new FilteredImageSource(img.getSource(), imgFilter);
 * Image filteredImg = Toolkit.getDefaultToolkit().createImage(imgFilterSrc);
 *
 * Transparant pixels are ignored by default
 * HashMap<Integer, Integer> knownColors RGB values are used instead of Color objects because
 * They are expensive to create. A Color object for each pixel in an image?
 *
 * @author Stefan
 */
public class ImgFilter extends RGBImageFilter {
  private final static Logger logger = Logger.getLogger("logger");
  private Color baseColor;
  private Color replacementColor;
  private java.util.List<Integer> ignoredPixels;      // Pixels that will not be Filtered
  private Map<Integer, Integer> knownColors;          // Key=known Color value from the image, Val=index position
  private Map<Color, List<Color>> replacementColors;  // Key=ReplacementColor, Val=replacement colors
  private int darkenPercentage;   // The percentage that each px should be darkened

  public ImgFilter(Color baseColor) {
    this.baseColor = baseColor;
    this.replacementColors = new HashMap<Color, List<Color>>();
    this.knownColors = new HashMap<Integer, Integer>();
    this.ignoredPixels = new ArrayList<Integer>();
    initDefaults();
  }

  private void initDefaults() {
    ignoredPixels.add(0);
    ignoredPixels.add(0xFF000000);
  }

  public int filterRGB(int x, int y, int rgb) {
    int filteredRGB = rgb;

    if (replaceNeeded(rgb)) {
      List<Color> replaceColors = replacementColors.get(replacementColor);
      filteredRGB = getReplacementColor(rgb, replaceColors);
    }

    if (canDarken()) {
      return darkenColor(filteredRGB, -darkenPercentage);
    } else {
      return filteredRGB;
    }
  }

  private boolean replaceNeeded(int rgb) {
    return !ignoredPixels.contains(rgb) && !baseColor.equals(replacementColor);
  }

  private int getReplacementColor(int rgb, List<Color> replaceColors) {
    if (!replacementColors.containsKey(replacementColor))
      throw new IllegalArgumentException("No replacementColor found for " + ColorUtil.toString(replacementColor));

    if (knownColors.containsKey(rgb)) {
      // Get the position of the knownColor
      // We will use this index to get the replacementColor
      int knownColorPos = knownColors.get(rgb);
      return replaceColors.get(knownColorPos).getRGB();
    } else {
      logger.warn("Unknown Color: " + Integer.toHexString(rgb));
      return rgb;
    }
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

  //----------------------------------------------------------------------------
  // SETTERS
  // ---------------------------------------------------------------------------
  /**
   * @param baseColor The color of the source image
   */
  public void setBaseColor(Color baseColor) {
    this.baseColor = baseColor;
  }

  /**
   * @param replacementColor The color that we should recolor the base image to
   */
  public void setReplacementColor(Color replacementColor) {
    this.replacementColor = replacementColor;
  }

  /**
   * @param darkenPercentage 0 = Don't darken
   *                         100 = solid black
   */
  public void setDarkenPercentage(int darkenPercentage) {
    if (darkenPercentage > 0 && darkenPercentage < 100) {
      this.darkenPercentage = darkenPercentage;
    }
  }

  //----------------------------------------------------------------------------
  // ADD/REMOVE
  // ---------------------------------------------------------------------------
  public void addIgnoredPixels(int[] rgbs) {
    for (int rgb : rgbs) {
      this.ignoredPixels.add(rgb);
    }
  }

  public void addIgnoredPixels(Color[] colors) {
    for (Color color : colors) {
      this.ignoredPixels.add(color.getRGB());
    }
  }

  public void addKnownColors(Color[] colors) {
    for (int i = 0; i < colors.length; i++) {
      knownColors.put(colors[i].getRGB(), i);
    }
  }

  public void addKnownColors(Integer[] rgbs) {
    for (int i = 0; i < rgbs.length; i++) {
      knownColors.put(rgbs[i], i);
    }
  }

  public void addReplacementColors(Color color, Integer[] rgbValues) {
    List<Color> colorList = new ArrayList<Color>();
    for (Integer rgb : rgbValues) {
      colorList.add(new Color(rgb));
    }
    addReplacementColors(color, colorList);
  }

  public void addReplacementColors(Color color, List<Color> colorLst) {
    knownColorsSizeCheck(colorLst.size());
    replacementColors.put(color, colorLst);
  }

  private void knownColorsSizeCheck(int colorListSize) {
    if (colorListSize != knownColors.size()) {
      throw new IllegalArgumentException("The replacement colors list(" + colorListSize + ") does not have the same size as the knownColors(" + knownColors.size() + ").");
    }
  }

  //----------------------------------------------------------------------------
  // GETTERS
  // ---------------------------------------------------------------------------
  private boolean canDarken() {
    return darkenPercentage > 0;
  }

  public Color getBaseColor() {
    return baseColor;
  }

  /**
   * @return If this ImgFilter can recolor to color
   */
  public boolean canRecolorTo(Color color) {
    return replacementColors.containsKey(color) || baseColor.equals(color);
  }

  /**
   * @return a set of all colors that are supported by this ImgFilter
   *         This are the replacementColors + the baseColor
   */
  public Set<Color> getReplacementColors() {
    Set<Color> replaceColors = new HashSet<Color>();
    replaceColors.add(baseColor);

    for (Color c : replacementColors.keySet()) {
      replaceColors.add(c);
    }

    return Collections.unmodifiableSet(replaceColors);
  }
}