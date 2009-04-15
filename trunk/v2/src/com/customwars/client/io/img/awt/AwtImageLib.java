package com.customwars.client.io.img.awt;

import org.apache.log4j.Logger;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.util.ResourceLoader;
import tools.ColorUtil;
import tools.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Load, Store and Recolor BufferedImages aka awt images.
 *
 * @author stefan
 */
public class AwtImageLib {
  private static final Logger logger = Logger.getLogger(AwtImageLib.class);
  private Map<String, BufferedImage> bufferedImgCache;  // Contains the base images that can be recolored by a filter
  private static Map<String, ImgFilter> imgFilters;     // Can recolor a base image from base color to replacement color
  private static Set<Color> supportedColors;            // The colors supported by each filter

  public AwtImageLib() {
    bufferedImgCache = new HashMap<String, BufferedImage>();
    imgFilters = new HashMap<String, ImgFilter>();
    supportedColors = new HashSet<Color>();
  }

  public void loadImg(String awtImgPath, String imgName) {
    String key = imgName.toUpperCase();
    if (LoadingList.isDeferredLoading()) {
      LoadingList.get().add(new DeferredAwtImgLoader(awtImgPath, key));
    } else {
      try {
        loadImage(awtImgPath, key);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Load the BufferedImage from the awtImgPath
   * add it to the cache keyed by the imgName
   */
  private void loadImage(String awtImgPath, String imgName) throws IOException {
    if (!bufferedImgCache.containsKey(imgName)) {
      addAwtImg(imgName, ImageIO.read(ResourceLoader.getResourceAsStream(awtImgPath)));
    }
  }

  //----------------------------------------------------------------------------
  // Recolor
  //----------------------------------------------------------------------------
  /**
   * load, Recolor and store awt image
   *
   * if deferredLoading is on, recolor at a later time
   *
   * @param recolorTo    The color to use for the recolored unit img
   * @param darkPerc     percentage to darken the image, 0 = ignore
   * @param filterName   'unit', 'property' the RGBfilter to use
   * @param baseImgName  UNIT_RED the image with the base color aka the source image
   * @param storeImgName UNIT_BLUE the name of the recolored image, key in the BufferedImage Map
   */
  public void recolorImg(Color recolorTo, String filterName, String baseImgName, String storeImgName, int darkPerc) {
    recolorImage(recolorTo, filterName, baseImgName, storeImgName, darkPerc);
  }

  private void recolorImage(Color recolorTo, String filterName, String baseImgName, String storeImgName, int darkPerc) {
    // If the img is already loaded return
    if (isAwtImgLoaded(storeImgName)) {
      return;
    }

    ImgFilter filter = imgFilters.get(filterName.toUpperCase());
    if (filter == null)
      throw new IllegalArgumentException(
              "No ImgFilter found for " + filterName.toUpperCase() + " available filters: " + imgFilters.keySet());

    BufferedImage awtImg = getAwImg(baseImgName);
    filter.setReplacementColor(recolorTo);
    filter.setDarkenPercentage(darkPerc);
    BufferedImage recoloredImage = recolorImg(awtImg, filter);
    addAwtImg(storeImgName, recoloredImage);
  }

  /**
   * Recolor awtImage with the specified filter
   */
  public BufferedImage recolorImg(BufferedImage awtImg, ImgFilter filter) {
    FilteredImageSource imgProducer = new FilteredImageSource(awtImg.getSource(), filter);
    return recolorImg(imgProducer);
  }

  private BufferedImage recolorImg(ImageProducer imgProducer) {
    return ImageUtil.convertToBufferedImg(Toolkit.getDefaultToolkit().createImage(imgProducer));
  }

  //----------------------------------------------------------------------------
  public void clear() {
    bufferedImgCache.clear();
  }

  public void addAwtImg(String imgName, BufferedImage img) {
    String key = imgName.toUpperCase();
    if (!bufferedImgCache.containsKey(key)) {
      if (img != null)
        bufferedImgCache.put(key, img);
    } else {
      throw new IllegalArgumentException("imgName " + key + " is already cached");
    }
  }

  public static void addImgFilter(String filterName, ImgFilter filter) {
    if (filterName != null && filter != null)
      imgFilters.put(filterName.toUpperCase(), filter);
  }

  public void buildColorsFromImgFilters() {
    for (ImgFilter imgFilter : imgFilters.values()) {
      Set<java.awt.Color> replacementColors = imgFilter.getReplacementColors();

      for (Color c : replacementColors) {
        addColor(c);
      }
    }
  }

  public void addColor(Color c) {
    if (supportedByAllImageFilters(c)) {
      supportedColors.add(c);
    } else {
      logger.warn("Color " + ColorUtil.toString(c) + " is not supported by all Image filters.");
    }
  }

  private boolean supportedByAllImageFilters(Color c) {
    boolean supportedByAllImgFilters = true;
    for (ImgFilter imgFilter : imgFilters.values()) {
      if (!imgFilter.canRecolorTo(c)) {
        supportedByAllImgFilters = false;
      }
    }
    return supportedByAllImgFilters;
  }

  //----------------------------------------------------------------------------
  // Getters
  //----------------------------------------------------------------------------
  public boolean isAwtImgLoaded(String awtImgName) {
    return bufferedImgCache.containsKey(awtImgName.toUpperCase());
  }

  public BufferedImage getAwImg(String awtImgName) {
    String key = awtImgName.toUpperCase();
    if (!bufferedImgCache.containsKey(key))
      throw new IllegalArgumentException("No awt image found for '" + key + "' available names " + bufferedImgCache.keySet());

    return bufferedImgCache.get(key);
  }

  public boolean contains(String awtImgName) {
    return bufferedImgCache.containsKey(awtImgName);
  }

  public int size() {
    return bufferedImgCache.size();
  }

  public Color getBaseColor(String filterName) {
    String key = filterName.toUpperCase();
    if (!imgFilters.containsKey(key)) {
      throw new IllegalArgumentException("No img filter found for '" + key + "' available names " + imgFilters.keySet());
    }
    return imgFilters.get(key).getBaseColor();
  }

  public Set<Color> getSupportedColors() {
    return Collections.unmodifiableSet(supportedColors);
  }

  public Set<String> getStoredImgNames() {
    return bufferedImgCache.keySet();
  }

  private class DeferredAwtImgLoader implements DeferredResource {
    private String imgPath;
    private String imgName;

    public DeferredAwtImgLoader(String imgPath, String imgName) {
      this.imgPath = imgPath;
      this.imgName = imgName;
    }

    public void load() throws IOException {
      loadImage(imgPath, imgName);
    }

    public String getDescription() {
      return imgName;
    }
  }
}
