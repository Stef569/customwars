package com.customwars.client.io.img.awt;

import com.customwars.client.tools.UCaseMap;
import org.apache.log4j.Logger;
import org.newdawn.slick.util.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Load and Store BufferedImages aka awt images.
 * Images are stored by an image reference
 * String -> BufferedImage
 */
public class AwtImageLib {
  private static final Logger logger = Logger.getLogger(AwtImageLib.class);
  private final Map<String, BufferedImage> imgCache;

  public AwtImageLib() {
    imgCache = new UCaseMap<BufferedImage>();
  }

  /**
   * Load and return the bufferedImage stored at the awtImgPath
   * The Image is cached and can be retrieved by calling #getAwtImg(imgRef)
   *
   * @param imgRef     The key used to lookup images from the cache
   * @param awtImgPath The path to the awt image
   */
  public BufferedImage loadImg(String imgRef, String awtImgPath) {
    try {
      return loadImage(imgRef, awtImgPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private BufferedImage loadImage(String imgRef, String awtImgPath) throws IOException {
    if (!imgCache.containsKey(imgRef)) {
      InputStream in = ResourceLoader.getResourceAsStream(awtImgPath);
      BufferedImage img = ImageIO.read(in);
      addAwtImg(imgRef, img);
    } else {
      logger.warn("Attempting to load " + imgRef + " 2x");
    }
    return imgCache.get(imgRef);
  }

  public void addAwtImg(String imgRef, BufferedImage img) {
    imgCache.put(imgRef, img);
  }

  public boolean isAwtImgLoaded(String imgRef) {
    return imgCache.containsKey(imgRef);
  }

  public BufferedImage getAwtImg(String imgRef) {
    return imgCache.get(imgRef);
  }

  public void clear() {
    imgCache.clear();
  }
}
