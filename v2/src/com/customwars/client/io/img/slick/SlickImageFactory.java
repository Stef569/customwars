package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.awt.AwtImageLib;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Creates Slick Images from BufferedImages
 * The buffered Image come from the AwtImageLib
 *
 * @author stefan
 */
public class SlickImageFactory {
  private static AwtImageLib imageLib;

  public static Image createSlickImg(String slickImgName, String awtImgName) {
    Image img = getEmptyImage();
    setTexture(awtImgName, img);
    return img;
  }

  private static Image getEmptyImage() {
    try {
      return new Image(0, 0);
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImageStrip createSlickImgStrip(String slickImgName, String awtImgName, int tileWidth, int tileHeight) {
    ImageStrip strip = new ImageStrip(tileWidth, tileHeight);
    setTexture(awtImgName, strip);
    return strip;
  }

  public static SpriteSheet createSpriteSheet(String slickImgName, String awtImgName, int tileWidth, int tileHeight) {
    SpriteSheet spriteSheet = new SpriteSheet(tileWidth, tileHeight);
    setTexture(awtImgName, spriteSheet);
    return spriteSheet;
  }

  public static void setTexture(String awtImgName, Image img) {
    try {
      BufferedImage awtImg = imageLib.getAwImg(awtImgName);
      Texture target = BufferedImageUtil.getTexture("", awtImg, GL11.GL_NEAREST);
      img.setTexture(target);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setImageLib(AwtImageLib imageLib) {
    SlickImageFactory.imageLib = imageLib;
  }
}

