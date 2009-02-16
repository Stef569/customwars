package com.customwars.client.ui.sprite;

import com.customwars.client.model.map.Location;
import org.apache.log4j.Logger;
import org.newdawn.slick.Graphics;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all sprites in the game
 * cursor, units, properties
 *
 * @author stefan
 */
public class SpriteManager {
  private static final Logger logger = Logger.getLogger(SpriteManager.class);
  private Map<String, TileSprite> cursorSprites;
  private TileSprite activeCursor;

  public SpriteManager() {
    cursorSprites = new HashMap<String, TileSprite>();
  }

  public void update(int elapsedTime) {
    activeCursor.update(elapsedTime);
  }

  public void render(Graphics g) {
    if (isCursorSet()) {
      activeCursor.render(g);
    }
  }

  public void moveCursorTo(Location location) {
    if (isCursorSet()) {
      activeCursor.setLocation(location);
    }
  }

  /**
   * changes the activeCursor to the cursor mapped by cursorName
   *
   * @param cursorName case incensitive name of the cursor ie Select, SELECT both return the same cursor
   */
  public void setActiveCursor(String cursorName) {
    if (cursorSprites.containsKey(cursorName.toUpperCase())) {
      activeCursor = cursorSprites.get(cursorName);
    } else {
      logger.warn(cursorName + " is not available, cursors:" + cursorSprites.keySet());
    }
  }

  public void addCursor(String name, TileSprite cursorSprite) {
    cursorSprite.setRenderInCenter(true);
    this.cursorSprites.put(name.toUpperCase(), cursorSprite);
  }

  public Location getCursorLocation() {
    return activeCursor.getLocation();
  }

  public boolean isCursorSet() {
    return activeCursor != null;
  }
}
