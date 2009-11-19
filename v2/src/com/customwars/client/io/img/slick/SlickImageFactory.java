package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.awt.AwtImageLib;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Creates Slick Images from BufferedImages
 * The buffered Image are stored by a string reference in the AwtImageLib
 *
 * @author stefan
 */
public class SlickImageFactory {
  private final AwtImageLib awtImageLib;

  public SlickImageFactory(AwtImageLib awtImageLib) {
    this.awtImageLib = awtImageLib;
  }

  public ImageStrip createImgStripFromAwtSource(String imgRef, int tileWidth, int tileHeight) {
    Image img = createImageFromAwtSource(imgRef);
    return new ImageStrip(img, tileWidth, tileHeight);
  }

  public SpriteSheet createSpriteSheetFromAwtSource(String imgRef, int tileWidth, int tileHeight) {
    Image img = createImageFromAwtSource(imgRef);
    return new SpriteSheet(img, tileWidth, tileHeight);
  }

  public Image createImageFromAwtSource(String imgRef) {
    Texture texture = convertToTexture(imgRef);
    return new Image(texture);
  }

  private Texture convertToTexture(String imgRef) {
    try {
      BufferedImage awtImg = awtImageLib.getAwtImg(imgRef);
      return BufferedImageUtil.getTexture(imgRef, awtImg, GL11.GL_NEAREST);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Image createImage(String imgPath) {
    try {
      return new Image(imgPath);
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImageStrip createImageStrip(String imgPath, int tileWidth, int tileHeight) {
    try {
      return new ImageStrip(imgPath, tileWidth, tileHeight);
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }

  public static SpriteSheet createSpriteSheet(String imgPath, int tileWidth, int tileHeight) {
    try {
      return new SpriteSheet(imgPath, tileWidth, tileHeight);
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the concrete Slick Image based on the base Image class
   *
   * @param imgSource The source for a new Image of type baseImg
   * @param baseImg   The image type that should be constructed
   * @return A new Slick Image of the same image type as baseImg containing the image data from imgSource
   */
  public static Image createSlickImg(Image imgSource, Image baseImg) {
    if (baseImg instanceof SpriteSheet) {
      SpriteSheet baseImageSpriteSheet = (SpriteSheet) baseImg;
      int tileWidth = baseImageSpriteSheet.getWidth() / baseImageSpriteSheet.getHorizontalCount();
      int tileHeight = baseImageSpriteSheet.getHeight() / baseImageSpriteSheet.getVerticalCount();
      return new SpriteSheet(imgSource, tileWidth, tileHeight);
    } else if (baseImg instanceof ImageStrip) {
      ImageStrip baseImageStrip = (ImageStrip) baseImg;
      int tileWidth = baseImageStrip.getTileWidth();
      int tileHeight = baseImageStrip.getTileHeight();
      return new ImageStrip(imgSource, tileWidth, tileHeight);
    } else if (imgSource != null) {
      return imgSource;
    } else {
      throw new IllegalArgumentException("Image type " + baseImg + " is not supported");
    }
  }
}
