package client.ui.renderer;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.model.map.gameobject.Terrain;
import client.ui.ImageStrip;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Image;

/**
 * Renders the minimap
 * each terrainID has an image in the minimap, each plugin can provide their own terrain set
 * so we can't use the terrain ID to grab a specific terrain img.
 * @author stefan
 */
public class MiniMapRenderer extends TileMapRenderer {
  private ImageStrip terrainStrip;

  public MiniMapRenderer(TileMap<Tile> map) throws SlickException {
    super(map);
  }

  public void loadResources() throws SlickException {
    terrainStrip = new ImageStrip("v2/res/image/miniMap.png", 4, 4);
  }

  public void renderTerrain(Graphics g, Terrain terrain, int col, int row, boolean fogged) {
    Image terrainImg = terrainStrip.getSubImage(terrain.getID());
    g.drawImage(terrainImg, col * 4, row * 4);
  }
}
