package com.customwars.client.io.img;

import com.customwars.client.io.img.awt.AwtImageLib;
import com.customwars.client.io.img.slick.RecolorManager;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.tools.UCaseMap;
import org.newdawn.slick.Image;

import java.util.Map;

/**
 * Load and cache Slick Images, The Images are mapped by a upper case string value.
 * eg. UNIT_RED -> red unit slick image instance
 *
 * Every request for an image throught the get methods
 * will return a cached Image if the imgRef has already been loaded
 */
public class ImageLib {
  private final Map<String, Image> slickImgCache;
  private final AwtImageLib awtImageLib;
  private final RecolorManager recolorManager;

  public ImageLib() {
    awtImageLib = new AwtImageLib();
    recolorManager = new RecolorManager(this, awtImageLib);
    slickImgCache = new UCaseMap<Image>();
  }

  public void loadSlickImage(String imgRef, String imgPath) {
    Image strip = SlickImageFactory.createImage(imgPath);
    addSlickImg(imgRef, strip);
  }

  public void loadSlickImageStrip(String imgRef, String imgPath, int tileWidth, int tileHeight) {
    Image strip = SlickImageFactory.createImageStrip(imgPath, tileWidth, tileHeight);
    addSlickImg(imgRef, strip);
  }

  public void loadSlickSpriteSheet(String imgRef, String imgPath, int tileWidth, int tileHeight) {
    Image sheet = SlickImageFactory.createSpriteSheet(imgPath, tileWidth, tileHeight);
    addSlickImg(imgRef, sheet);
  }

  public void addSlickImg(String imgRef, Image img) {
    slickImgCache.put(imgRef, img);
  }

  /**
   * Remove the awt images sources that were used to recolor to slick Images
   */
  public void clearImageSources() {
    awtImageLib.clear();
  }

  public Image getSlickImg(String imgRef) {
    return slickImgCache.get(imgRef);
  }

  public int countSlickImages() {
    return slickImgCache.size();
  }

  public RecolorManager getRecolorManager() {
    return recolorManager;
  }
}
