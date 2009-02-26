package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Graphics;

/**
 * A map is rendered in 2 layers
 * The terrain and the sprites on the terrain
 */
public class MapRenderer extends TileMapRenderer {
  private SpriteManager spriteManager;
  private Scroller scroller;
  private boolean cursorLocked;

  public MapRenderer(ResourceManager resources) {
    spriteManager = new SpriteManager(resources);
  }

  public void moveCursor(int x, int y) {
    moveCursor(pixelsToTile(x, y));
  }

  public void moveCursor(Direction direction) {
    Location cursorLocation = getCursorLocation();
    Location newLocation = map.getAdjacent(cursorLocation, direction);
    moveCursor(newLocation);
  }

  public void moveCursor(Location location) {
    if (!cursorLocked) {
      spriteManager.moveCursorTo(location);
      scroller.setCursorLocation(spriteManager.getCursorLocation());
    }
  }

  public void update(int elapsedTime) {
    spriteManager.update(elapsedTime);
    scroller.update(elapsedTime);
  }

  public void render(int x, int y, Graphics g) {
    super.render(x, y, g);
    spriteManager.render(x, y, g);
  }

  public void addCursorSprite(String cursorName, TileSprite cursorSprite) {
    spriteManager.addCursor(cursorName, cursorSprite);
  }

  public void activedCursor(String cursorName) {
    spriteManager.setActiveCursor(cursorName);
  }

  public void addCursor(String cursorName, TileSprite cursor) {
    spriteManager.addCursor(cursorName, cursor);
  }

  public void lockCursor(boolean lock) {
    this.cursorLocked = lock;
  }

  public void setMap(TileMap<Tile> map) {
    super.setMap(map);
    spriteManager.setMap(map);
    spriteManager.loadSprites();
  }

  public void setScroller(Scroller scroller) {
    this.scroller = scroller;
  }

  public void setAutoScroll(boolean scroll) {
    scroller.setAutoScroll(scroll);
  }

  public void toggleCursorLock() {
    this.cursorLocked = !cursorLocked;
  }

  public Location getCursorLocation() {
    if (spriteManager.isCursorSet()) {
      return spriteManager.getCursorLocation();
    } else {
      return null;
    }
  }
}

