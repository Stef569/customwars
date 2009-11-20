package com.customwars.client.io.loading;

import com.customwars.client.App;
import com.customwars.client.io.FileSystemManager;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.tools.FileUtil;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Image;

import java.io.File;
import java.io.IOException;

/**
 * Load and add cursors to the resource Manager
 */
public class CursorLoader implements CWResourceLoader {
  private static final int CURSOR_ANIM_COUNT = 2;
  private static final int CURSOR_ANIM_SPEED = 250;
  private final ResourceManager resources;
  private final String cursorImgPath;

  public CursorLoader(ResourceManager resources, String cursorImgPath) {
    this.resources = resources;
    this.cursorImgPath = cursorImgPath;
  }

  /**
   * Load all the images from the cursorImgPath
   * A cursor images contains CURSOR_ANIM_COUNT horizontal images of the same width
   * Create animation and TileSprite
   * Add the cursor to the cursor collection
   */
  public void load() throws IOException {
    FileSystemManager fsm = new FileSystemManager(cursorImgPath);
    for (File cursorImgFile : fsm.getFiles()) {
      String cursorName = FileUtil.getFileNameWithoutExtension(cursorImgFile);
      Image cursorImg = SlickImageFactory.createImage(cursorImgFile.getPath());
      TileSprite cursor = createCursor(cursorImg);
      resources.addCursor(cursorName, cursor);
    }
  }

  private TileSprite createCursor(Image cursorImg) {
    int cursorWidth = cursorImg.getWidth() / CURSOR_ANIM_COUNT;
    int cursorHeight = cursorImg.getHeight();
    ImageStrip cursorImgs = new ImageStrip(cursorImg, cursorWidth, cursorHeight);
    TileSprite cursor = new TileSprite(cursorImgs, CURSOR_ANIM_SPEED, null, null);

    // Use the cursor image height to calculate the tile effect range ie
    // If the image has a height of 160/32=5 tiles 5/2 rounded to int becomes 2.
    // most cursors have a effect range of 1 ie
    // 40/32=1 and 1/2=0 max(0,1) becomes 1
    int tileSize = App.getInt("plugin.tilesize");
    int cursorEffectRange = Math.max(cursorImg.getHeight() / tileSize / 2, 1);
    cursor.setEffectRange(cursorEffectRange);
    return cursor;
  }
}
