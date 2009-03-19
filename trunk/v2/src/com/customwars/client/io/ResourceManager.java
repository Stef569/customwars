package com.customwars.client.io;

import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.AnimationParser;
import com.customwars.client.io.loading.ImageFilterParser;
import com.customwars.client.io.loading.ImageParser;
import com.customwars.client.io.loading.ModelLoader;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.util.ResourceLoader;
import tools.ColorUtil;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

/**
 * Handles and hides all kind of resources through 1 object
 * Images and Animations can be retrieved
 *
 * @author stefan
 */
public class ResourceManager {
  private static final Logger logger = Logger.getLogger(ResourceManager.class);
  private static final String IMAGE_LOADER_FILE = "imageLoader.txt";
  private static final String ANIM_LOADER_FILE = "animLoader.txt";
  private static final String COLORS_FILE = "colors.xml";
  private static final int DARK_PERCENTAGE = 30;
  private static final ModelLoader modelLoader = new ModelLoader();

  private ImageLib imageLib;
  private AnimLib animLib;
  private String imgPath, dataPath;

  public ResourceManager() {
    this(new ImageLib(), new AnimLib());
  }

  /**
   * @param imageLib The cache to load the image to
   * @param animLib  The cache to load the animations to
   */
  public ResourceManager(ImageLib imageLib, AnimLib animLib) {
    this.imageLib = imageLib;
    this.animLib = animLib;
    SlickImageFactory.setDeferredLoading(LoadingList.isDeferredLoading());
  }

  public void loadAll() {
    logger.info("Loading resources");
    loadModel();
    try {
      loadConfig();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load resource " + e);
    }
  }

  public void loadModel() {
    modelLoader.setModelResPath(dataPath);
    modelLoader.loadModel();
  }

  public void loadConfig() throws IOException {
    loadColorsFromFile();
    loadImagesFromFile();
    loadAnimationsFromFile();
  }

  private void loadColorsFromFile() throws IOException {
    ImageFilterParser imgFilterParser = new ImageFilterParser();
    InputStream in = ResourceLoader.getResourceAsStream(dataPath + COLORS_FILE);
    imgFilterParser.loadConfigFile(in);
    imageLib.buildColorsFromImgFilters();
  }

  private void loadImagesFromFile() throws IOException {
    logger.info("Reading file " + imgPath + IMAGE_LOADER_FILE);
    ImageParser imgParser = new ImageParser(imageLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + IMAGE_LOADER_FILE);
    imgParser.loadConfigFile(in);
  }

  private void loadAnimationsFromFile() throws IOException {
    AnimationParser animParser = new AnimationParser(imageLib, animLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + ANIM_LOADER_FILE);
    animParser.loadConfigFile(in);
  }

  public void recolor(Color... colors) {
    recolorImages(colors);
    createRecoloredAnimations(colors);
  }

  private void recolorImages(Color... colors) {
    for (Color color : colors) {
      checkIsColorSupported(color);
      imageLib.recolorImg(color, "unit");
      imageLib.recolorImg(color, "city");
    }

    for (Color color : colors) {
      imageLib.recolorImg(color, "unit", "darker", "unit", DARK_PERCENTAGE);
      imageLib.recolorImg(color, "city", "darker", "city", DARK_PERCENTAGE);
    }
  }

  private void createRecoloredAnimations(Color... colors) {
    Color unitBaseColor = getBaseColor("unit");
    Color cityBaseColor = getBaseColor("city");

    for (Color color : colors) {
      checkIsColorSupported(color);
      animLib.createUnitAnimations(unitBaseColor, this, color);
      animLib.createCityAnimations(cityBaseColor, this, color);
    }
  }

  private void checkIsColorSupported(Color color) {
    if (!imageLib.getSupportedColors().contains(color)) {
      throw new IllegalArgumentException(
              "Color " + color + " is not supported, add the color info to " + COLORS_FILE);
    }
  }

  public void clear() {
    imageLib.clearImages();
    modelLoader.clear();
  }

  public void setImgPath(String path) {
    this.imgPath = path;
  }

  public void setDataPath(String path) {
    this.dataPath = path;
  }

  public boolean isSlickImgLoaded(String slickImgName) {
    return imageLib.isSlickImgLoaded(slickImgName);
  }

  public Image getSlickImg(String imgName) {
    return imageLib.getSlickImg(imgName);
  }

  public ImageStrip getSlickImgStrip(String imgName) {
    return (ImageStrip) imageLib.getSlickImg(imgName);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName, Color color, String suffix) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName + "_" + suffix);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName, Color color) {
    String colorName = ColorUtil.toString(color);
    return getSlickSpriteSheet(imgName + "_" + colorName);
  }

  public SpriteSheet getSlickSpriteSheet(String imgName) {
    return (SpriteSheet) imageLib.getSlickImg(imgName);
  }

  public int countSlickImages() {
    return imageLib.countSlickImages();
  }

  public Color getBaseColor(String filterName) {
    return imageLib.getBaseColor(filterName);
  }

  public Set<Color> getSupportedColors() {
    return imageLib.getSupportedColors();
  }

  public Animation getAnim(String animName) {
    return animLib.getAnim(animName);
  }

  public Animation getCityAnim(int cityID, Color color) {
    return getCityAnim(cityID, color, "");
  }

  public Animation getCityAnim(int cityID, Color color, String suffix) {
    return animLib.getCityAnim(cityID, color, suffix);
  }

  public Animation getUnitAnim(int unitID, Color color, String suffix) {
    return animLib.getUnitAnim(unitID, color, suffix);
  }

  public Collection<Animation> getAllAnims() {
    return animLib.getAllAnimations();
  }
}
