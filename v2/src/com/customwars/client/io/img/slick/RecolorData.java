package com.customwars.client.io.img.slick;

import com.customwars.client.tools.ColorUtil;
import com.customwars.client.tools.ImageUtil;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecolorData {
  private final Map<Integer, Integer> knownColors;          // Key=known Color value from the image, Val=index position
  private final Map<Color, List<Color>> replacementColors;  // Key=ReplacementColor, Val=replacement colors
  private final Color baseColor;
  private Image baseImg;

  public RecolorData(Color baseColor) {
    this.baseColor = baseColor;
    this.replacementColors = new HashMap<Color, List<Color>>();
    this.knownColors = new HashMap<Integer, Integer>();
  }

  public void setBaseImg(Image baseImg) {
    this.baseImg = baseImg;
  }

  public void addKnownColors(Collection<Color> colors) {
    int knownColorIndex = 0;
    for (Color color : colors) {
      int rgb = color.getRGB();
      addKnownColor(rgb, knownColorIndex++);
    }
  }

  public void addKnownColors(int... rgbs) {
    int knownColorIndex = 0;
    for (int rgb : rgbs) {
      addKnownColor(rgb, knownColorIndex++);
    }
  }

  private void addKnownColor(int rgb, int index) {
    knownColors.put(rgb, index);
  }

  public void addReplacementColors(Color color, int... rgbValues) {
    List<Color> colorList = new ArrayList<Color>();
    for (Integer rgb : rgbValues) {
      colorList.add(new Color(rgb));
    }
    addReplacementColors(color, colorList);
  }

  public void addReplacementColors(Color color, List<Color> colorLst) {
    knownColorsSizeCheck(colorLst);
    replacementColors.put(color, colorLst);
  }

  private void knownColorsSizeCheck(List<Color> replacementColors) {
    if (replacementColors.size() != knownColors.size()) {
      throw new IllegalArgumentException("The replacement colors list(" + replacementColors.size() + ") does not have the same size as the knownColors(" + knownColors.size() + ").");
    }
  }

  /**
   * Retrieve the replacement color for the given rgb value
   * If the rgb value cannot be replaced then the rgb value is returned
   */
  public int getReplacementColor(int rgb, Color recolorTo) {
    if (!replacementColors.containsKey(recolorTo)) {
      String replacementColorName = ColorUtil.toString(recolorTo);
      throw new IllegalArgumentException("No replacement color found for " + replacementColorName);
    }

    List<Color> replaceColors = replacementColors.get(recolorTo);
    if (knownColors.containsKey(rgb)) {
      // Get the position of the knownColor
      // We will use this index to get the replacementColor
      int knownColorPos = knownColors.get(rgb);
      return replaceColors.get(knownColorPos).getRGB();
    } else {
      return rgb;
    }
  }

  public Color getBaseColor() {
    return baseColor;
  }

  public String getBaseColorName() {
    return ColorUtil.toString(baseColor);
  }

  public boolean canRecolor() {
    return baseImg != null;
  }

  /**
   * @return If this ImageFilterData can recolor to color
   */
  public boolean canRecolorTo(Color color) {
    return replacementColors.containsKey(color) || baseColor.equals(color);
  }

  /**
   * @return All the pixels from the base Image in 1 array, each integer represents rgb data
   */
  public int[] getBaseImgPixels() {
    return ImageUtil.convertToPixels(baseImg);
  }

  public int getBaseImgWidth() {
    return baseImg.getWidth(null);
  }

  public int getBaseImgHeight() {
    return baseImg.getHeight(null);
  }

  /**
   * @return a set of all colors that are supported by this ImageFilterData
   *         This are the replacementColors + the baseColor
   */
  public Collection<Color> getReplacementColors() {
    Set<Color> replaceColors = new HashSet<Color>();
    replaceColors.add(baseColor);

    for (Color color : replacementColors.keySet()) {
      replaceColors.add(color);
    }

    return Collections.unmodifiableSet(replaceColors);
  }

}
