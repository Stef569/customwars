package com.customwars.client.ui.renderer;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.gameobject.Terrain;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Renders each terrain in a map, each terrainID has an image in the terrainStrip
 *
 * @author stefan
 */
public class TerrainRenderer extends TileMapRenderer {
  private ImageStrip terrainStrip;
  private Color fogColor = Color.lightGray;

  public TerrainRenderer(TileMap<Tile> map) {
    super(map);
  }

  public void renderTerrain(Graphics g, Terrain terrain, int col, int row, boolean fogged) {
    Image terrainImg = terrainStrip.getSubImage(terrain.getID());
    int tileWidth, tileHeight;
    int widthOffset, heightOffset;

    // If the image is higher then the tile size, then store the excess height
    if (terrainImg.getHeight() > tileSize) {
      tileHeight = tileSize;
      heightOffset = terrainImg.getHeight() - tileSize;
    } else {
      tileHeight = terrainImg.getHeight();
      heightOffset = 0;
    }

    // If the image is wider then the tileSize, then center it
    if (terrainImg.getWidth() > tileSize) {
      tileWidth = tileSize;
      widthOffset = (terrainImg.getWidth() - tileSize) / 2;
    } else {
      tileWidth = terrainImg.getWidth();
      widthOffset = 0;
    }

    int x = (col * tileWidth) - widthOffset;
    int y = (row * tileHeight) - heightOffset;

    if (fogged) {
      g.drawImage(terrainImg, x, y, fogColor);
    } else {
      g.drawImage(terrainImg, x, y);
    }
  }

  public void setTerrainStrip(ImageStrip terrainStrip) {
    this.terrainStrip = terrainStrip;
  }

  public void setFogColor(Color fogColor) {
    this.fogColor = fogColor;
  }
}
