package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Graphics;

/**
 * A map is rendered in 2 layers
 * The terrain and the sprites on the terrain
 */
public class MapRenderer extends TileMapRenderer {
  private SpriteManager spriteManager;

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
    spriteManager.moveCursorTo(location);
  }

  public void update(int elapsedTime) {
    spriteManager.update(elapsedTime);
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

  public Location getCursorLocation() {
    if (spriteManager.isCursorSet()) {
      return spriteManager.getCursorLocation();
    } else {
      return null;
    }
  }

  public void setMap(TileMap<Tile> map) {
    super.setMap(map);
    spriteManager.setMap(map);
    spriteManager.loadSprites();
  }
}

