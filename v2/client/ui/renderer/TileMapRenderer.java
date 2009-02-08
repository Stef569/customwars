package client.ui.renderer;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.model.map.gameobject.Terrain;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.awt.*;

/**
 * Provide an easy way to render a map to the screen
 *
 * @author Stefan
 */
public abstract class TileMapRenderer {
  private int x, y;                     // The point where we start rendering
  int tileSize;
  private TileMap<Tile> map;
  private boolean renderTerrain = true;

  public TileMapRenderer(TileMap<Tile> map) throws SlickException {
    setMap(map);
  }

  public void render(Graphics g) {
    if (map == null) return;

    g.translate(x,y);
    for (Tile t : map.getAllTiles()) {
      Terrain terrain = t.getTerrain();
      boolean fogged = t.isFogged();

      if (renderTerrain) {
        renderTerrain(g, terrain, t.getCol(), t.getRow(), fogged);
      }
    }
    g.translate(-x,-y);
  }

  public abstract void loadResources() throws SlickException;

  public abstract void renderTerrain(Graphics g, Terrain terrain, int col, int row, boolean fogged);

  public Tile pixelsToTile(Point p) {
    return pixelsToTile(p.x, p.y);
  }

  /**
   * Converts pixel coordinates into a Tile
   *
   * @param x pixel position on screen x(where x is relative to the map 0,0 coordinates)
   * @param y pixel position on screen y(where y is relative to the map 0,0 coordinates)
   * @return The tile at the pixel location, or null if not found
   */
  public Tile pixelsToTile(int x, int y) {
    if (map == null) return null;
    if (x < 0 || y < 0) return null;

    int col = x / tileSize;
    int row = y / tileSize;
    return map.getTile(col, row);
  }

  //----------------------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------------------
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

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

