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
 * Scaled terrain Images are retrieved from an terrain minimap Image
 * Units and cities are manually rendered
 */
public class MiniMapRenderer implements Renderable {
  private static final int TILE_SIZE = 4;
  private ImageStrip terrainMiniMap;
  private Map<Tile> map;
  private Point location;

  public MiniMapRenderer() {
    this(null);
  }

  public MiniMapRenderer(Map<Tile> map) {
    this.location = new Point();
    this.map = map;
  }

  public void setTerrainMiniMap(ImageStrip terrainMiniMap) {
    this.terrainMiniMap = terrainMiniMap;
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
  }

  public void render(Graphics g) {
    if (map != null) {
      for (Tile t : map.getAllTiles()) {
        int x = location.x + t.getCol() * TILE_SIZE;
        int y = location.y + t.getRow() * TILE_SIZE;
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
    renderSquare(x, y, g, color);
  }

  private void renderSquare(int x, int y, Graphics g, Color color) {
    Color prevColor = g.getColor();
    g.setColor(color);
    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
    g.setColor(Color.black);

    // Draw border from right top -> right bottom
    // and from left bottom -> right bottom
    g.drawLine(x + TILE_SIZE - 1, y, x + TILE_SIZE - 1, y + TILE_SIZE - 1);
    g.drawLine(x, y + TILE_SIZE - 1, x + TILE_SIZE - 1, y + TILE_SIZE - 1);
    g.setColor(prevColor);
  }

  private void renderUnit(int x, int y, Graphics g, Unit unit) {
    Color color = ColorUtil.convertToSlickColor(unit.getOwner().getColor());
    drawCircle(x, y, g, color);
  }

  private void drawCircle(int x, int y, Graphics g, Color color) {
    Color prevColor = g.getColor();
    g.setColor(color);
    Shape circle = new Circle(x + TILE_SIZE / 2, y + TILE_SIZE / 2, TILE_SIZE / 2);
    g.fill(circle);
    g.setColor(Color.white);
    g.drawLine(x + 1, y + 1, x + 1, y + 1);
    g.setColor(prevColor);
  }

  public void setLocation(int x, int y) {
    this.location.setLocation(x, y);
  }

  public int getWidth() {
    return map != null ? map.getCols() * TILE_SIZE : 0;
  }

  public int getHeight() {
    return map != null ? map.getRows() * TILE_SIZE : 0;
  }

  public Dimension getSize() {
    return new Dimension(getWidth(), getHeight());
  }
}