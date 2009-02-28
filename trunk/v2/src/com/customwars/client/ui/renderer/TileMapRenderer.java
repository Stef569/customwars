package com.customwars.client.ui.renderer;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.Point;

/**
 * Render a map to the screen
 * Each terrainID has an image in the terrainStrip
 *
 * @author Stefan
 */
public class TileMapRenderer {
  TileMap<Tile> map;
  private ImageStrip terrainStrip;
  private Color fogColor = Color.lightGray;
  int tileSize;

  public TileMapRenderer() {
  }

  public TileMapRenderer(TileMap<Tile> map) {
    setMap(map);
  }

  public void render(int x, int y, Graphics g) {
    if (map == null) return;

    for (Tile t : map.getAllTiles()) {
      renderTile(g, t, x, y, t.isFogged());
    }
  }

  public void renderTile(Graphics g, Tile tile, int x, int y, boolean fogged) {
    if (tile.getTerrain() instanceof City) return;

    Image terrainImg = terrainStrip.getSubImage(tile.getTerrain().getID());
    int tileWidth, tileHeight;
    int imgWidthOffset, imgHeightOffset;

    // If the image is higher then the tile size, then store the excess height
    if (terrainImg.getHeight() > tileSize) {
      tileHeight = tileSize;
      imgHeightOffset = terrainImg.getHeight() - tileSize;
    } else {
      tileHeight = terrainImg.getHeight();
      imgHeightOffset = 0;
    }

    // If the image is wider then the tileSize, then center it
    if (terrainImg.getWidth() > tileSize) {
      tileWidth = tileSize;
      imgWidthOffset = (terrainImg.getWidth() - tileSize) / 2;
    } else {
      tileWidth = terrainImg.getWidth();
      imgWidthOffset = 0;
    }

    int px = x + (tile.getCol() * tileWidth) - imgWidthOffset;
    int py = y + (tile.getRow() * tileHeight) - imgHeightOffset;

    if (fogged) {
      g.drawImage(terrainImg, px, py, fogColor);
    } else {
      g.drawImage(terrainImg, px, py);
    }
  }

  public void setTerrainStrip(ImageStrip terrainStrip) {
    this.terrainStrip = terrainStrip;
  }

  public Tile pixelsToTile(Point p) {
    return pixelsToTile(p.x, p.y);
  }

  /**
   * Converts pixel coordinates into a Tile within the map
   *
   * @param x pixel position in the game (where x is relative to the map 0,0 coordinates)
   * @param y pixel position in the game (where y is relative to the map 0,0 coordinates)
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

  public void setFogColor(Color fogColor) {
    this.fogColor = fogColor;
  }
}

