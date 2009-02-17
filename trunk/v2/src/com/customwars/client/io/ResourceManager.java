package com.customwars.client.io;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.ImageConfigParser;
import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles and hides all kind of resources through 1 object
 * Images and Animations can be retrieved
 *
 * @author stefan
 */
public class ResourceManager {
  private static final Logger logger = Logger.getLogger(ResourceManager.class);
  private static final String IMAGE_LOADER_FILE = "res/image/imageLoader.txt";
  private ImageConfigParser imageConfigParser;
  private ImageLib imageLib;

  public ResourceManager(ImageLib imageLib) {
    this.imageLib = imageLib;
    imageConfigParser = new ImageConfigParser(imageLib);
  }

  public void loadImages() throws IOException {
    logger.info("Reading file " + IMAGE_LOADER_FILE);
    InputStream in = ResourceLoader.getResourceAsStream(IMAGE_LOADER_FILE);
    imageConfigParser.loadConfigFile(in);
    imageLib.buildColorsFromImgFilters();
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

  public Color getBaseColor(String filterName) {
    return imageLib.getBaseColor(filterName);
  }
}
