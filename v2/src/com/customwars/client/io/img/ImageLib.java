package com.customwars.client.io.img;

import com.customwars.client.io.img.awt.AwtImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.io.img.slick.SpriteSheet;
import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import tools.ColorUtil;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores references to Slick Images
 * Slick images are loaded from awt images,
 * The awt Images should be loaded first into the awtImageLib and then passed to this class constructor
 *
 * Every request for an image trought the load... methods
 * will return a cached Image if slickImgName has already been loaded
 *
 * Normally you would call load... once and then use one of the get... methods to return the correct image.
 *
 * @author stefan
 */
public class ImageLib {
  private static final Logger logger = Logger.getLogger(ImageLib.class);
  private Map<String, Image> slickImgCache;
  private AwtImageLib awtImageLib;

  public ImageLib(AwtImageLib awtImageLib) {
    this.awtImageLib = awtImageLib;
    this.slickImgCache = new HashMap<String, Image>();
    SlickImageFactory.setImageLib(awtImageLib);
  }

  public void loadSlickImage(String imgName) {
    loadSlickImage(imgName, imgName);
  }

  /**
   * load and add the slick image to the cache keyed by the slickImgName
   *
   * @param slickImgName the image name to be used as key for the Slick image
   * @param awtImgName   the awt image name key to retrieve the awtImg, we need
   *                     the awtImg to be able to create a new Slick Image.
   */
  public void loadSlickImage(String slickImgName, String awtImgName) {
    if (!slickImgCache.containsKey(slickImgName)) {
      Image slickImg = SlickImageFactory.createSlickImg(slickImgName, awtImgName);
      addSlickImg(slickImgName, slickImg);
    }
  }

  public void loadSlickImageStrip(String imgName, int tileWidth, int tileHeight) {
    loadSlickImageStrip(imgName, imgName, tileWidth, tileHeight);
  }

  public void loadSlickImageStrip(String slickImgName, String awtImgName, int tileWidth, int tileHeight) {
    if (!slickImgCache.containsKey(slickImgName)) {
      ImageStrip slickImg = SlickImageFactory.createSlickImgStrip(slickImgName, awtImgName, tileWidth, tileHeight);
      addSlickImg(slickImgName, slickImg);
    }
  }

  public void loadSlickSpriteSheet(String imgName, int tileWidth, int tileHeight) {
    loadSlickSpriteSheet(imgName, imgName, tileWidth, tileHeight);
  }

  public void loadSlickSpriteSheet(String slickImgName, String awtImgName, int tileWidth, int tileHeight) {
    if (!slickImgCache.containsKey(slickImgName)) {
      SpriteSheet slickImg = SlickImageFactory.createSpriteSheet(slickImgName, awtImgName, tileWidth, tileHeight);
      addSlickImg(slickImgName, slickImg);
    }
  }


  public void clearImages() {
    slickImgCache.clear();
  }

  public void addSlickImg(String imgName, Image img) {
    if (imgName != null && img != null && !slickImgCache.containsKey(imgName))
      slickImgCache.put(imgName, img);
  }

  public void recolorImg(Color recolorTo, String imgName, int darkPerc) {
    recolorImg(recolorTo, imgName, "", imgName, 0);
  }

  public void recolorImg(Color color, String imgName, String prefix, int darkPerc) {
    recolorImg(color, imgName, prefix, imgName, darkPerc);
  }

  public void recolorImg(Color recolorTo, String imgName, String prefix, String imgFilterName, int darkPerc) {
    String colorToName = ColorUtil.toString(recolorTo);
    Color baseColor = awtImageLib.getBaseColor(imgFilterName);
    String baseColorName = ColorUtil.toString(baseColor);
    String baseImgName = imgName + "_" + baseColorName;
    String storeImgName = appendValidStringValue(imgName, prefix, prefix + "_") + colorToName;

    awtImageLib.recolorImg(recolorTo, imgFilterName, baseImgName, storeImgName, darkPerc);
  }

  /**
   * Append only if <tt>value<tt> contains a valid String value
   */
  public String appendValidStringValue(String originalString, String value, String append) {
    if (value != null && value.trim().length() != 0) {
      originalString += append;
    }
    return originalString;
  }


  public boolean isSlickImgLoaded(String slickImgName) {
    return slickImgCache.containsKey(slickImgName);
  }

  public Image getSlickImg(String imgName) {
    if (!slickImgCache.containsKey(imgName)) {
      throw new RuntimeException("No image found for '" + imgName + "' available names: " + slickImgCache.keySet());
    }
    return slickImgCache.get(imgName);
  }

  public ImageStrip getSlickImgStrip(String imgName) {
    return (ImageStrip) getSlickImg(imgName);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName) {
    return (SpriteSheet) getSlickImg(imgName);
  }

  public int countSlickImages() {
    return slickImgCache.size();
  }

  public Color getBaseColor(String filterName) {
    return awtImageLib.getBaseColor(filterName);
  }
}
