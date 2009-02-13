package com.customwars.client.ui.renderer;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.gameobject.Terrain;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Renders the minimap
 * each terrainID has an image in the minimap, each plugin can provide their own terrain set
 * so we can't use the terrain ID to grab a specific terrain img.
 *
 * @author stefan
 */
public class MiniMapRenderer extends TileMapRenderer {
  private ImageStrip terrainStrip;

  public MiniMapRenderer(TileMap<Tile> map) throws SlickException {
    super(map);
  }

  public void update(int elapsedTime) {
  }

  public void renderTerrain(Graphics g, Terrain terrain, int col, int row, boolean fogged) {
    Image terrainImg = terrainStrip.getSubImage(terrain.getID());
    g.drawImage(terrainImg, col * 4, row * 4);
  }

  public void setTerrainStrip(ImageStrip terrainStrip) {
    this.terrainStrip = terrainStrip;
  }
}
