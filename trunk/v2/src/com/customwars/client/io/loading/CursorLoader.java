package com.customwars.client.io.loading;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SlickImageFactory;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.ResourceLoader;

import java.util.Scanner;
import java.util.StringTokenizer;

import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;

/**
 * Parse the cursor load file create and add the cursor to the resource Manager.
 */
public class CursorLoader extends LineParser {
  private final String cursorImgPath;
  private final ResourceManager resources;

  public CursorLoader(ResourceManager resources, String cursorImgPath, String cursorLoaderFile) {
    super(ResourceLoader.getResourceAsStream(cursorImgPath + cursorLoaderFile));
    this.resources = resources;
    this.cursorImgPath = cursorImgPath;
  }

  @Override
  public void parseLine(String line) {
    StringTokenizer tokens = new StringTokenizer(line);
    Scanner cmdScanner = new Scanner(line);

    if (!(tokens.countTokens() == 4))
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for " + line + " 4 required.");
    else {
      String cursorName = cmdScanner.next();
      String imgFileName = cmdScanner.next();
      int tileWidth = cmdScanner.nextInt();
      int duration = cmdScanner.nextInt();
      Image img = SlickImageFactory.createImage(cursorImgPath + imgFileName);
      TileSprite cursor = createCursor(img, tileWidth, duration);
      resources.addCursor(cursorName, cursor);
    }
  }

  private TileSprite createCursor(Image cursorImg, int tileWidth, int duration) {
    int cursorHeight = cursorImg.getHeight();
    ImageStrip cursorImgs = new ImageStrip(cursorImg, tileWidth, cursorHeight);
    TileSprite cursor = new TileSprite(cursorImgs, duration, null, null);

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
