package client.ui.renderer;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.model.map.gameobject.Terrain;
import client.ui.ImageStrip;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class MapRenderer extends TileMapRenderer {
  private ImageStrip terrainStrip;

  public MapRenderer(TileMap<Tile> map) throws SlickException {
    super(map);
  }

  public void loadResources() throws SlickException {
    this.terrainStrip = new ImageStrip("v2/res/image/awTerrains.png", tileSize, 42);
  }

  public void renderTerrain(Graphics g, Terrain terrain, int x, int y, boolean fogged) {
    terrainStrip.getSubImage(terrain.getID()).draw(x, y);
  }
}
