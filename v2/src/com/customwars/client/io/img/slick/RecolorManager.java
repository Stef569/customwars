package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.awt.AwtImageLib;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.tools.UCaseMap;
import org.apache.log4j.Logger;
import org.newdawn.slick.Image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles recoloring of Slick Images, We need to use java.awt images to recolor because it is not possible to
 * grab pixels from a Slick Image once created. This problem is solved by first loading and storing
 * the base awt image into the awtImageLib then let the SlickImageFactory convert the awt image
 * to a Slick Image. Now we have the base Image stored as awt and as slick Image.
 *
 * One of the recolor functions will use the awt base Image and recolor it using an ImageFilter
 * The imageFilter.recolor method returns a Slick Image in the chosen color, this Image can then be used to
 * construct a SpriteSheet or ImageStrip based on the base Image type.
 *
 * Usage:
 * 1. add recolor Data, this contains the pixels that should be replaced
 * 2. set the base image for recoloring
 * 3. call one of the recolor methods
 *
 * Recolored images are stored in the slickImageLib
 */
public class RecolorManager {
  private static final Logger logger = Logger.getLogger(RecolorManager.class);
  private final Map<String, RecolorData> recolorDatas;
  private final Set<Color> supportedColors;
  private final ImageLib slickImageLib;
  private final AwtImageLib awtImageLib;
  private final SlickImageFactory slickImageFactory;

  public RecolorManager(ImageLib slickImageLib, AwtImageLib awtImageLib) {
    this.slickImageLib = slickImageLib;
    this.awtImageLib = awtImageLib;
    supportedColors = new HashSet<Color>();
    recolorDatas = new UCaseMap<RecolorData>();
    slickImageFactory = new SlickImageFactory(awtImageLib);
  }

  public void addRecolorData(String filterRef, RecolorData recolorData) {
    recolorDatas.put(filterRef, recolorData);
    buildColorsFromRecolorData();
  }

  private void buildColorsFromRecolorData() {
    supportedColors.clear();

    for (RecolorData imgFilterData : recolorDatas.values()) {
      Collection<Color> replacementColors = imgFilterData.getReplacementColors();

      for (Color replacementColor : replacementColors) {
        addColor(replacementColor);
      }
    }
  }

  private void addColor(Color color) {
    if (colorSupportedByAllImageFilters(color)) {
      supportedColors.add(color);
    } else {
      logger.warn("Color " + ColorUtil.toString(color) + " is not supported by all Image filters.");
    }
  }

  private boolean colorSupportedByAllImageFilters(Color color) {
    boolean supportedByAllImgFilters = true;
    for (RecolorData recolorData : recolorDatas.values()) {
      if (!recolorData.canRecolorTo(color)) {
        supportedByAllImgFilters = false;
      }
    }
    return supportedByAllImgFilters;
  }

  /**
   * Set the base Image for recoloring
   *
   * @param imgRef  The name of the image, used as a reference to retrieve the slick image later on
   * @param imgPath The location of the image
   */
  public void setBaseRecolorImage(String imgRef, String imgPath) {
    String baseImgRef = loadAwtImg(imgRef, imgPath);
    Image slickImg = slickImageFactory.createImageFromAwtSource(baseImgRef);
    slickImageLib.addSlickImg(baseImgRef, slickImg);
  }

  /**
   * Set the base Image(as an ImageStrip) for recoloring
   *
   * @param imgRef  The name of the image, used as a reference to retrieve the slick image later on
   * @param imgPath The location of the image
   */
  public void setBaseRecolorImageStrip(String imgRef, String imgPath, int tileWidth, int tileHeight) {
    String baseImgRef = loadAwtImg(imgRef, imgPath);
    Image slickImg = slickImageFactory.createImgStripFromAwtSource(baseImgRef, tileWidth, tileHeight);
    slickImageLib.addSlickImg(baseImgRef, slickImg);
  }

  /**
   * Set the base Image(as an SpriteSheet) for recoloring
   *
   * @param imgRef  The name of the image, used as a reference to retrieve the slick image later on
   * @param imgPath The location of the image
   */
  public void setBaseRecolorSpriteSheet(String imgRef, String imgPath, int tileWidth, int tileHeight) {
    String baseImgRef = loadAwtImg(imgRef, imgPath);
    Image slickImg = slickImageFactory.createSpriteSheetFromAwtSource(baseImgRef, tileWidth, tileHeight);
    slickImageLib.addSlickImg(baseImgRef, slickImg);
  }

  /**
   * Load the base awt image from the image path
   * Store the awt image as imgRef_BaseColorName
   *
   * @param imgRef  The String to reference this awt image to without color eg "unit"
   * @param imgPath The location of the awt image
   * @return The new image reference name
   */
  private String loadAwtImg(String imgRef, String imgPath) {
    RecolorData data = getRecolorData(imgRef);
    String baseImgRef = imgRef + '_' + data.getBaseColorName();

    BufferedImage img = awtImageLib.loadImg(baseImgRef, imgPath);
    data.setBaseImg(img);
    return baseImgRef;
  }

  /**
   * Retrieve the base Image stored as imgRef and recolor/store the recolored img as imgRef_Color eg
   * Recoloring the unit base image(UNIT_RED) to BLUE will store a new image as UNIT_BLUE
   *
   * @param imgRef The image reference name without the color
   * @param colors The colors that we want to recolor to
   */
  public void recolor(String imgRef, Iterable<Color> colors) {
    recolor(imgRef, "", colors, 0);
  }

  /**
   * Retrieve the base Image stored as imgRef and recolor/darken and store the recolored img as imgRef_Color eg
   * Recoloring the unit base image(UNIT_RED) to BLUE will store a new image as UNIT_BLUE
   *
   * @param imgRef         The image reference name without the color
   * @param colors         The colors that we want to recolor to
   * @param darkPercentage The percentage to darken the recolored images to(0-100)
   */
  public void recolor(String imgRef, Iterable<Color> colors, int darkPercentage) {
    recolor(imgRef, "", colors, darkPercentage);
  }

  /**
   * Retrieve the base Image stored as imgRef and recolor/store the recolored img as imgRef_Color_suffix eg
   * Recoloring the unit base image(UNIT_RED) to BLUE darken 5% with suffix DARKER
   * will store a new image as UNIT_BLUE_DARKER
   *
   * @param imgRef         The image reference name without the color
   * @param suffix         The suffix to append to the new image reference name
   * @param colors         The colors that we want to recolor to
   * @param darkPercentage The percentage to darken the recolored images to(0-100)
   */
  public void recolor(String imgRef, String suffix, Iterable<Color> colors, int darkPercentage) {
    RecolorData recolorData = getRecolorData(imgRef);

    for (Color color : colors) {
      if (recolorData.canRecolorTo(color)) {
        recolor(imgRef, suffix, color, darkPercentage, recolorData);
      } else {
        logger.warn("Cannot recolor to " + ColorUtil.toString(color));
      }
    }
  }

  private RecolorData getRecolorData(String filterRef) {
    return recolorDatas.get(filterRef);
  }

  private void recolor(String imgRef, String suffix, Color recolorTo, int darkPercentage, RecolorData recolorData) {
    String colorToName = ColorUtil.toString(recolorTo);
    String recoloredImgRef = imgRef + '_' + colorToName;

    if (StringUtil.hasContent(suffix)) {
      recoloredImgRef += '_' + suffix;
    }

    ImageFilter filter = new ImageFilter(recolorData, darkPercentage, recolorTo);
    if (filter.canRecolor()) {
      Image recoloredImageSource = filter.recolor();
      Image baseImage = slickImageLib.getSlickImg(imgRef + '_' + recolorData.getBaseColorName());

      assert recoloredImageSource != null;

      // Need to determine the concrete image type to add
      // The recolored Image type is based on the base Image type
      // If the base Image is a spritesheet then the recolored image needs to be stored as a spritesheet too
      Image recoloredSlickImage = SlickImageFactory.createSlickImg(recoloredImageSource, baseImage);
      slickImageLib.addSlickImg(recoloredImgRef, recoloredSlickImage);
    }
  }

  public Color getBaseColor(String filterName) {
    if (!recolorDatas.containsKey(filterName)) {
      throw new IllegalArgumentException("No recolor data found for '" + filterName + "' available names " + recolorDatas.keySet());
    }
    return recolorDatas.get(filterName).getBaseColor();
  }

  public Set<Color> getSupportedColors() {
    return Collections.unmodifiableSet(supportedColors);
  }
}
