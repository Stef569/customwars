package com.customwars.client.ui.renderer;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.ColorUtil;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Render a scaled version of a Map
 * Scaled terrain Images are retrieved from the minimap Image
 * The minimap image should contain sub images of equal width and height
 * The tilesize is based on the minimap image.
 *
 * Units are rendered as circles
 * Cities are rendered as squares
 */
public class MiniMapRenderer implements Renderable {
  private static final int HQ_ALPHA_STEP = 7;
  private static final int HQ_MAX_ALPHA = 255;
  private static final int HQ_MIN_ALPHA = 0;
  private ImageStrip terrainMiniMap;
  private Map<Tile> map;
  private int tileSize;
  private Point location;
  private int hqAlpha;

  public MiniMapRenderer() {
    this(null);
  }

  public MiniMapRenderer(Map<Tile> map) {
    this.location = new Point();
    this.map = map;
  }

  public void setTerrainMiniMap(ImageStrip terrainMiniMap) {
    this.terrainMiniMap = terrainMiniMap;
    this.tileSize = terrainMiniMap.getTileWidth();
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
  }

  public void update() {
    if (hqAlpha >= HQ_MAX_ALPHA) {
      hqAlpha -= HQ_ALPHA_STEP;
    } else if (hqAlpha < HQ_MIN_ALPHA) {
      hqAlpha += HQ_ALPHA_STEP;
    }
  }

  public void render(Graphics g) {
    if (map != null) {
      for (Tile t : map.getAllTiles()) {
        int x = location.x + t.getCol() * tileSize;
        int y = location.y + t.getRow() * tileSize;
        renderTile(x, y, g, t);
      }
    }
  }

  private void renderTile(int x, int y, Graphics g, Tile tile) {
    Terrain terrain = tile.getTerrain();

    if (terrain instanceof City) {
      City city = (City) terrain;
      renderCity(x, y, g, city);
    } else {
      renderTerrain(x, y, g, terrain);
    }
    Unit unit = map.getUnitOn(tile);
    if (unit != null) {
      renderUnit(x, y, g, unit);
    }
  }

  private void renderTerrain(int x, int y, Graphics g, Terrain terrain) {
    Image terrainImg = terrainMiniMap.getSubImage(terrain.getID());
    g.drawImage(terrainImg, x, y);
  }

  private void renderCity(int x, int y, Graphics g, City city) {
    Color color = ColorUtil.convertToSlickColor(city.getOwner().getColor());
    renderSquare(x, y, g, color, city.isHQ());
  }

  private void renderSquare(int x, int y, Graphics g, Color color, boolean animate) {
    Color prevColor = g.getColor();
    g.setColor(color);
    g.fillRect(x, y, tileSize, tileSize);

    // Draw from right top -> right bottom
    // and from left bottom -> right bottom
    if (animate) {
      g.setColor(new Color(0, 0, 0, hqAlpha));
    } else {
      g.setColor(Color.black);
    }
    g.drawLine(x + tileSize - 1, y, x + tileSize - 1, y + tileSize - 1);
    g.drawLine(x, y + tileSize - 1, x + tileSize - 1, y + tileSize - 1);
    g.setColor(prevColor);
  }

  private void renderUnit(int x, int y, Graphics g, Unit unit) {
    Color color = ColorUtil.convertToSlickColor(unit.getOwner().getColor());
    drawCircle(x, y, g, color);
  }

  private void drawCircle(int x, int y, Graphics g, Color color) {
    Color prevColor = g.getColor();
    g.setColor(color);
    Shape circle = new Circle(x + tileSize / 2, y + tileSize / 2, tileSize / 2);
    g.fill(circle);
    g.setColor(Color.white);
    g.drawLine(x + 1, y + 1, x + 1, y + 1);
    g.setColor(prevColor);
  }

  public void setLocation(int x, int y) {
    this.location.setLocation(x, y);
  }

  public int getWidth() {
    return map != null ? map.getCols() * tileSize : 0;
  }

  public int getHeight() {
    return map != null ? map.getRows() * tileSize : 0;
  }

  public Dimension getSize() {
    return new Dimension(getWidth(), getHeight());
  }
}