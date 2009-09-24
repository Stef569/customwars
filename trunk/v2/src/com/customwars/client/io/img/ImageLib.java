package com.customwars.client.io.img;

import com.customwars.client.io.img.awt.AwtImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.io.img.slick.SpriteSheet;
import org.newdawn.slick.Image;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import tools.ColorUtil;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores references to Slick Images, mapped by a upper case string value.
 * Slick images are loaded from awt images,
 * The awt Images should be loaded first by
 * loading them into the awtImageLib and then passed to this class constructor
 * or by using the loadAwtImg method
 *
 * Every request for an image trought the load methods
 * will return a cached Image if the imgName has already been loaded.
 *
 * @author stefan
 */
public class ImageLib {
  private Map<String, Image> slickImgCache;
  private AwtImageLib awtImageLib;

  public ImageLib(AwtImageLib awtImageLib) {
    this.awtImageLib = awtImageLib;
    this.slickImgCache = new HashMap<String, Image>();
    SlickImageFactory.setImageLib(awtImageLib);
  }

  public ImageLib() {
    this(new AwtImageLib());
  }

  public void loadAwtImg(String imgName, String imgPath) {
    awtImageLib.loadImg(imgPath, imgName);
  }

  public void loadSlickImage(String imgName) {
    loadSlickImage(imgName, imgName);
  }

  /**
   * load and add the slick image to the cache keyed by the imgName
   *
   * @param imgName    the image name to be used as key for the Slick image
   * @param awtImgName the awt image name key to retrieve the awtImg, we need
   *                   the awtImg to be able to create a new Slick Image.
   */
  public void loadSlickImage(String imgName, String awtImgName) {
    if (!slickImgCache.containsKey(imgName)) {
      Image slickImg = SlickImageFactory.createSlickImg(imgName, awtImgName);
      addSlickImg(imgName, slickImg);
    }
  }

  private void loadSlickImageStrip(String storeImgName, ImageStrip strip) {
    loadSlickImageStrip(storeImgName, strip.getTileWidth(), strip.getTileHeight());
  }

  public void loadSlickImageStrip(String imgName, int tileWidth, int tileHeight) {
    loadSlickImageStrip(imgName, imgName, tileWidth, tileHeight);
  }

  public void loadSlickImageStrip(String imgName, String awtImgName, int tileWidth, int tileHeight) {
    if (!slickImgCache.containsKey(imgName)) {
      ImageStrip slickImg = SlickImageFactory.createSlickImgStrip(imgName, awtImgName, tileWidth, tileHeight);
      addSlickImg(imgName, slickImg);
    }
  }

  private void loadSlickSpriteSheet(String imgName, SpriteSheet sheet) {
    loadSlickSpriteSheet(imgName, sheet.getTileWidth(), sheet.getTileHeight());
  }

  public void loadSlickSpriteSheet(String imgName, int tileWidth, int tileHeight) {
    loadSlickSpriteSheet(imgName, imgName, tileWidth, tileHeight);
  }

  public void loadSlickSpriteSheet(String imgName, String awtImgName, int tileWidth, int tileHeight) {
    if (!slickImgCache.containsKey(imgName.toUpperCase())) {
      SpriteSheet slickImg = SlickImageFactory.createSpriteSheet(imgName, awtImgName, tileWidth, tileHeight);
      addSlickImg(imgName, slickImg);
    }
  }

  public void clearImages() {
    slickImgCache.clear();
  }

  /**
   * Release the awt image resources
   */
  public void clearImageSources() {
    awtImageLib.clear();
  }

  public void addSlickImg(String slickImgName, Image img) {
    String key = slickImgName.toUpperCase();
    if (!slickImgCache.containsKey(key)) {
      if (img != null)
        slickImgCache.put(key, img);
    } else {
      throw new IllegalArgumentException("slick image " + key + " is already cached");
    }
  }

  /**
   * Recolor imgName to recolorTo
   * imgName is also the imgFilter name
   * The image is not darkened
   * The image will be stored in the format imgName_COLOR
   */
  public void recolorImg(Color recolorTo, String imgName) {
    recolorImg(recolorTo, imgName, "", imgName, 0);
  }

  /**
   * Recolor imgName to recolorTo
   * imgName is also the imgFilter name
   * Darken the image with darkPercentage(0-100)
   * The image will be stored in the format imgName_COLOR_suffix
   */
  public void recolorImg(Color recolorTo, String imgName, String suffix, int darkPercentage) {
    recolorImg(recolorTo, imgName, suffix, imgName, darkPercentage);
  }

  public void recolorImg(Color recolorTo, String imgName, String suffix, String imgFilterName, int darkPercentage) {
    String colorToName = ColorUtil.toString(recolorTo);
    Color baseColor = awtImageLib.getBaseColor(imgFilterName);
    String baseColorName = ColorUtil.toString(baseColor);
    String baseImgName = imgName + "_" + baseColorName;
    String storeImgName = imgName + "_" + colorToName;

    if (suffix != null && suffix.trim().length() > 0) {
      storeImgName += "_" + suffix;
    }

    if (LoadingList.isDeferredLoading()) {
      LoadingList.get().add(
        new DeferredAwtImgRecolorer(recolorTo, imgFilterName, baseImgName, storeImgName, darkPercentage)
      );
    } else {
      recolorImgNow(recolorTo, imgFilterName, baseImgName, storeImgName, darkPercentage);
    }
  }

  private void recolorImgNow(Color recolorTo, String imgFilterName, String baseImgName, String storeImgName, int darkPercentage) {
    String key = storeImgName.toUpperCase();

    if (isSlickImgLoaded(key)) {
      return;
    }

    awtImageLib.recolorImg(recolorTo, imgFilterName, baseImgName, key, darkPercentage);
    createSlickImg(baseImgName, storeImgName);
  }

  /**
   * Get the base slick image
   * get the tileWidth/tileHeight
   * create slick img
   *
   * This method ignores deferred loading because
   * 1. recoloring already is deferred @see DeferredImgRecoloring
   * 2. With deferredloading the SlickImageFactory will load the image data later
   * by adding a DeferredResource to the <b>end</b> of the loadingList.
   *
   * But the methods after recolorImg need the image data!
   * so we force SlickImageFactory to load the image data instantly.
   */
  private void createSlickImg(String baseImgName, String storeImgName) {
    Image img = getSlickImg(baseImgName);
    boolean origValue = SlickImageFactory.isDeferredLoading();
    SlickImageFactory.setDeferredLoading(false);

    if (img instanceof SpriteSheet) {
      SpriteSheet sheet = (SpriteSheet) img;
      loadSlickSpriteSheet(storeImgName, sheet);
    } else if (img instanceof ImageStrip) {
      ImageStrip strip = (ImageStrip) img;
      loadSlickImageStrip(storeImgName, strip);
    } else {
      loadSlickImage(storeImgName, baseImgName);
    }
    SlickImageFactory.setDeferredLoading(origValue);
  }

  public boolean isSlickImgLoaded(String slickImgName) {
    return slickImgCache.containsKey(slickImgName.toUpperCase());
  }

  public ImageStrip getSlickImgStrip(String imgName) {
    return (ImageStrip) getSlickImg(imgName);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName, Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName) {
    return (SpriteSheet) getSlickImg(imgName);
  }

  public Image getSlickImg(String imgName) {
    String key = imgName.toUpperCase();
    if (!slickImgCache.containsKey(key)) {
      throw new RuntimeException("No image found for '" + key + "' available: " + slickImgCache.keySet());
    }
    return slickImgCache.get(key);
  }

  public int countSlickImages() {
    return slickImgCache.size();
  }

  public Color getBaseColor(String filterName) {
    return awtImageLib.getBaseColor(filterName);
  }

  public void buildColorsFromImgFilters() {
    awtImageLib.buildColorsFromImgFilters();
  }

  public Set<Color> getSupportedColors() {
    return awtImageLib.getSupportedColors();
  }

  public String getStoredImgNames() {
    return slickImgCache.keySet().toString();
  }

  private class DeferredAwtImgRecolorer implements DeferredResource {
    private final Color recolorTo;
    private final String imgFilterName;
    private final String baseImgName;
    private final String storeImgName;
    private final int darkPercentage;

    public DeferredAwtImgRecolorer(Color recolorTo, String imgFilterName, String baseImgName, String storeImgName, int darkPercentage) {
      this.recolorTo = recolorTo;
      this.imgFilterName = imgFilterName;
      this.baseImgName = baseImgName;
      this.storeImgName = storeImgName;
      this.darkPercentage = darkPercentage;
    }

    public void load() throws IOException {
      recolorImgNow(recolorTo, imgFilterName, baseImgName, storeImgName, darkPercentage);
    }

    public String getDescription() {
      return "recoloring " + baseImgName + " storing as " + storeImgName;
    }
  }
}
