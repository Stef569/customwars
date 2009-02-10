package com.customwars.client.ui.renderer;

import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.gameobject.Terrain;
import com.customwars.client.ui.ImageStrip;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MapRenderer extends TileMapRenderer {
  private ImageStrip terrainStrip;
  private SpriteManager spriteManager;

  public MapRenderer(TileMap<Tile> map) throws SlickException {
    super(map);
    spriteManager = new SpriteManager();
  }

  public void moveCursor(int x, int y) {
    moveCursor(pixelsToTile(x, y));
  }

  public void moveCursor(Location location) {
    spriteManager.moveCursorTo(location);
  }

  public void update(int elapsedTime) {
    spriteManager.update(elapsedTime);
  }

  public void renderTerrain(Graphics g, Terrain terrain, int col, int row, boolean fogged) {
    Image terrainImg = terrainStrip.getSubImage(terrain.getID());

    int heightOffset = terrainImg.getHeight() - tileSize;
    int x = col * tileSize;
    int y = row * tileSize;

    g.translate(0, -heightOffset);
    if (fogged) {
      g.drawImage(terrainImg, x, y, Color.lightGray);
    } else {
      g.drawImage(terrainImg, x, y);
    }
    g.translate(0, heightOffset);
  }

  public void renderOnTop(Graphics g) {
    spriteManager.renderCursor(g);
  }

  public void setTerrainStrip(ImageStrip terrainStrip) {
    this.terrainStrip = terrainStrip;
  }

  public void addCursorSprite(String cursorName, TileSprite cursorSprite) {
    spriteManager.addCursor(cursorName, cursorSprite);
  }

  public void activedCursor(String cursorName) {
    spriteManager.setActiveCursor(cursorName);
  }

  public Location getCursorLocation() {
    if (spriteManager.isCursorSet()) {
      return spriteManager.getCursorLocation();
    } else {
      return map.getTile(0, 0);
    }
  }

  public void addCursor(String cursorName, TileSprite cursor) {
    spriteManager.addCursor(cursorName, cursor);
  }
}
