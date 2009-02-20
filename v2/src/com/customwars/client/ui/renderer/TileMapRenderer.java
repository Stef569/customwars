package com.customwars.client.ui.renderer;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Graphics;

/**
 * Provide an easy way to render a map to the screen
 *
 * @author Stefan
 */
public abstract class TileMapRenderer {
  private int x, y;
  private boolean renderTerrain = true;
  private TileMap<Tile> map;
  int tileSize;

  public TileMapRenderer() {
  }

  public TileMapRenderer(TileMap<Tile> map) {
    setMap(map);
  }

  public void update(int elapsedTime) {
  }

  public void render(Graphics g) {
    if (map == null) return;

    g.translate(x, y);
    for (Tile t : map.getAllTiles()) {
      Terrain terrain = t.getTerrain();
      boolean fogged = t.isFogged();

      if (renderTerrain) {
        renderTerrain(g, terrain, t.getCol(), t.getRow(), fogged);
      }
    }
    g.translate(-x, -y);
  }

  public abstract void renderTerrain(Graphics g, Terrain terrain, int col, int row, boolean fogged);

  /**
   * Converts pixel coordinates into a Tile within the map
   *
   * @param x pixel position on screen x(where x is relative to the map 0,0 coordinates)
   * @param y pixel position on screen y(where y is relative to the map 0,0 coordinates)
   * @return The tile at the pixel location, or null if x,y is not valid.
   */
  public Tile pixelsToTile(int x, int y) {
    if (map == null) return null;
    if (x < 0 || y < 0) return null;

    int col = x / tileSize;
    int row = y / tileSize;
    return map.getTile(col, row);
  }

  /**
   * Sets the map to be rendered
   * if the map is null nothing is rendered
   */
  public void setMap(TileMap<Tile> map) {
    this.map = map;
    if (map != null) {
      this.tileSize = map.getTileSize();
    }
  }

  public void setRenderTerrain(boolean renderTerrain) {
    this.renderTerrain = renderTerrain;
  }

  /**
   * The location to render this tile map
   */
  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

