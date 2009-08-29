package com.customwars.client.io.img.slick;

import com.customwars.client.io.img.awt.AwtImageLib;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.BufferedImageUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Creates Slick Images from BufferedImages
 * The buffered Image come from the AwtImageLib
 *
 * If Deferred Loading is on, the Slick images will be returned with a default or null Texture
 * DeferredTexture will set the texture at a later point in time.
 *
 * @author stefan
 */
public class SlickImageFactory {
  private static boolean deferredLoading;
  private static AwtImageLib imageLib;

  public static Image createSlickImg(String slickImgName, String awtImgName) {
    Image img = getEmptyImage();
    setTexture(awtImgName, slickImgName, img);
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
    setTexture(awtImgName, slickImgName, strip);
    return strip;
  }

  public static SpriteSheet createSpriteSheet(String slickImgName, String awtImgName, int tileWidth, int tileHeight) {
    SpriteSheet spriteSheet = new SpriteSheet(tileWidth, tileHeight);
    setTexture(awtImgName, slickImgName, spriteSheet);
    return spriteSheet;
  }

  public static void setTexture(String awtImgName, String imgName, Image img) {
    if (deferredLoading) {
      LoadingList.get().add(new DeferredTexture(awtImgName, imgName, img));
    } else {
      try {
        setTextureNow(img, awtImgName);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static void setTextureNow(Image img, String awtImgName) throws IOException {
    BufferedImage awtImg = imageLib.getAwImg(awtImgName);
    Texture target = BufferedImageUtil.getTexture("", awtImg, GL11.GL_NEAREST);
    img.setTexture(target);
  }

  public static void setDeferredLoading(boolean deferred) {
    deferredLoading = deferred;
  }

  public static boolean isDeferredLoading() {
    return deferredLoading;
  }

  private static class DeferredTexture extends TextureImpl implements DeferredResource {
    private String imgName;     // Name for the Texture
    private String awtImgName;  // Source for the texture
    private Image img;          // Image to set the loaded texture to
    private Texture target;     // The texture we're proxying for

    public DeferredTexture(String awtImgName, String imgName, Image img) {
      this.img = img;
      this.imgName = imgName;
      this.awtImgName = awtImgName;
    }

    public void load() throws IOException {
      BufferedImage awtImg = imageLib.getAwImg(awtImgName);
      target = BufferedImageUtil.getTexture(imgName, awtImg);
      img.setTexture(target);
    }

    /**
     * Check if the target has been obtained already
     */
    private void checkTarget() {
      if (target == null) {
        throw new RuntimeException("Attempt to use deferred texture before loading and resource not found: " + awtImgName);
      }
    }

    public void bind() {
      checkTarget();

      target.bind();
    }

    public float getHeight() {
      checkTarget();

      return target.getHeight();
    }

    public int getImageHeight() {
      checkTarget();
      return target.getImageHeight();
    }

    public int getImageWidth() {
      checkTarget();
      return target.getImageWidth();
    }

    public int getTextureHeight() {
      checkTarget();
      return target.getTextureHeight();
    }

    public int getTextureID() {
      checkTarget();
      return target.getTextureID();
    }

    public String getTextureRef() {
      checkTarget();
      return target.getTextureRef();
    }

    public int getTextureWidth() {
      checkTarget();
      return target.getTextureWidth();
    }

    public float getWidth() {
      checkTarget();
      return target.getWidth();
    }

    public void release() {
      checkTarget();
      target.release();
    }

    public String getDescription() {
      return imgName;
    }
  }

  public static void setImageLib(AwtImageLib imageLib) {
    SlickImageFactory.imageLib = imageLib;
  }
}

