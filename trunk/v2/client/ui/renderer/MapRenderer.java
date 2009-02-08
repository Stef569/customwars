package client.ui.renderer;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.model.map.gameobject.Terrain;
import client.ui.ImageStrip;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;

public class MapRenderer extends TileMapRenderer {
  private ImageStrip terrainStrip;

  public MapRenderer(TileMap<Tile> map) throws SlickException {
    super(map);
  }

  public void loadResources() throws SlickException {
    this.terrainStrip = new ImageStrip("v2/res/image/awTerrains.png", tileSize, 42);
  }

  public void renderTerrain(Graphics g, Terrain terrain, int x, int y, boolean fogged) {
    Image terrainImg = terrainStrip.getSubImage(terrain.getID());

    int offset = terrainImg.getHeight() - tileSize;
    g.translate(0, -offset);

    if (fogged) {
      g.drawImage(terrainImg, x, y, Color.lightGray);
    } else {
      g.drawImage(terrainImg, x, y);
    }

    g.translate(0, offset);
  }
}
