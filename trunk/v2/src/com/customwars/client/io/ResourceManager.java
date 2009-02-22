package com.customwars.client.io;

import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.AnimationParser;
import com.customwars.client.io.loading.ImageFilterParser;
import com.customwars.client.io.loading.ImageParser;
import com.customwars.client.io.loading.ModelLoader;
import org.apache.log4j.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

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
  private ModelLoader modelLoader = new ModelLoader();

  private ImageLib imageLib;
  private AnimLib animLib;
  private String imgPath, dataPath;

  public ResourceManager() {
    this(new ImageLib(), new AnimLib());
  }

  /**
   * @param imageLib The cache to load the image to, null -> don't load images on load()
   * @param animLib  The cache to load the animations to
   */
  public ResourceManager(ImageLib imageLib, AnimLib animLib) {
    this.imageLib = imageLib;
    this.animLib = animLib;
  }

  public void load() throws IOException {
    logger.info("Loading resources");
    modelLoader.setModelResPath(dataPath);
    modelLoader.loadModel();

    if (imageLib != null) {
      loadColors();
      loadImages();
      loadAnimations();
    }
  }

  private void loadColors() throws IOException {
    ImageFilterParser imgFilterParser = new ImageFilterParser();
    InputStream in = ResourceLoader.getResourceAsStream(dataPath + COLORS_FILE);
    imgFilterParser.loadConfigFile(in);
    imageLib.buildColorsFromImgFilters();
  }

  public void loadImages() throws IOException {
    logger.info("Reading file " + imgPath + IMAGE_LOADER_FILE);
    ImageParser imgParser = new ImageParser(imageLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + IMAGE_LOADER_FILE);
    imgParser.loadConfigFile(in);
  }

  private void loadAnimations() throws IOException {
    AnimationParser animParser = new AnimationParser(imageLib, animLib);
    InputStream in = ResourceLoader.getResourceAsStream(imgPath + ANIM_LOADER_FILE);
    animParser.loadConfigFile(in);
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

  public SpriteSheet getSlickSpriteSheet(String imgName) {
    return (SpriteSheet) imageLib.getSlickImg(imgName);
  }

  public int countSlickImages() {
    return imageLib.countSlickImages();
  }

  public Animation getAnim(String animName) {
    return animLib.getAnim(animName);
  }

  public Collection<Animation> getAllAnims() {
    return animLib.getAllAnims();
  }

  public Color getBaseColor(String filterName) {
    return imageLib.getBaseColor(filterName);
  }
}
