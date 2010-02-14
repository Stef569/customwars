package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.sprite.SpriteManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.Collection;
import java.util.List;

/**
 * <p>A Map has 2 layers that need to be rendered:
 * <ol>
 * <li>The background(terrains and cities)</li>
 * <li>The sprites on the terrain(units)</li>
 * </ol>
 * Both can be enabled/disabled by {@link #setRenderSprites(boolean)} and {@link #setRenderTerrain(boolean)}</p>
 * <p/>
 * <p>The Rendering order is from <tt>left to right, top to bottom</tt>:
 * <ol>
 * <li>Terrains and Cities</li>
 * <li>Units</li>
 * <li>The active unit</li>
 * <li>The active cursor</li>
 * </ol></p>
 * <p/>
 * <p>Cities and units can have a image that is larger then the tile size, these images need to be translated upwards.
 * So that the bottom of the image is positioned on the tile bottom. A side effect is that units can be partly hidden
 * when they are 1 tile above a city. Because the city image overlaps the unit image</p>
 *
 * @author Stefan
 */
public class MapRenderer implements Renderable {
  // Control
  private boolean renderTerrain = true;

  // Graphics
  private SpriteManager spriteManager;
  private ImageStrip terrainStrip;
  private final Color fogColor = Color.lightGray;
  private MapEffectsRenderer effectsRenderer;

  // Data
  private Map<Tile> map;
  private int tileSize;
  private Unit activeUnit;

  public MapRenderer(Map<Tile> map) {
    this(map, new SpriteManager(map));
  }

  public MapRenderer(Map<Tile> map, SpriteManager spriteManager) {
    this.map = map;
    this.tileSize = map.getTileSize();
    this.spriteManager = spriteManager;
    this.effectsRenderer = new MapEffectsRenderer(this, map);
  }

  public void loadResources(ResourceManager resources) {
    spriteManager.loadResources(resources);
    spriteManager.loadSprites();
    effectsRenderer.loadResources(resources);
    terrainStrip = resources.getSlickImgStrip("terrains");
  }

  public void update(int elapsedTime) {
    spriteManager.update(elapsedTime);
    effectsRenderer.update(elapsedTime);
  }

  public void render(Graphics g) {
    renderMap(g);
    effectsRenderer.render(g);
    spriteManager.renderUnit(g, activeUnit);
    spriteManager.renderCursor(g);

    // Dying units are not located in the map anymore
    // they need to be rendered manually
    spriteManager.renderDyingUnits(g);
  }

  private void renderMap(Graphics g) {
    for (Tile t : map.getAllTiles()) {
      int x = t.getCol() * tileSize;
      int y = t.getRow() * tileSize;

      // Don't render outside of the GUI as it is an expensive operation
      if (GUI.canFitToScreen(x, y))
        renderTile(g, t);
    }
  }

  private void renderTile(Graphics g, Tile tile) {
    Terrain terrain = tile.getTerrain();

    if (terrain instanceof City) {
      City city = (City) terrain;
      spriteManager.renderCity(g, city);
    } else {
      if (renderTerrain) {
        renderTerrain(g, tile);
      }
    }

    Unit unit = map.getUnitOn(tile);
    if (unit != null && !unit.isHidden()) {
      spriteManager.renderUnit(g, unit);
    }
  }

  private void renderTerrain(Graphics g, Tile tile) {
    Image terrainImg = terrainStrip.getSubImage(tile.getTerrain().getID());

    if (tile.isFogged()) {
      renderImgOnTile(g, terrainImg, tile, fogColor);
    } else {
      renderImgOnTile(g, terrainImg, tile);
    }
  }

  void renderImgOnTile(Graphics g, Image img, Location location) {
    renderImgOnTile(g, img, location, null);
  }

  void renderImgOnTile(Graphics g, Image img, Location location, Color color) {
    int tileWidth, tileHeight;
    int imgWidthOffset, imgHeightOffset;

    // If the image is higher then the tile size, then store the excess height
    if (img.getHeight() > tileSize) {
      tileHeight = tileSize;
      imgHeightOffset = img.getHeight() - tileSize;
    } else {
      tileHeight = img.getHeight();
      imgHeightOffset = 0;
    }

    // If the image is wider then the tileSize, then center it
    if (img.getWidth() > tileSize) {
      tileWidth = tileSize;
      imgWidthOffset = (img.getWidth() - tileSize) / 2;
    } else {
      tileWidth = img.getWidth();
      imgWidthOffset = 0;
    }

    int px = (location.getCol() * tileWidth) - imgWidthOffset;
    int py = (location.getRow() * tileHeight) - imgHeightOffset;

    g.drawImage(img, px, py, color);
  }

  public void removeUnit(Unit unit) {
    if (activeUnit == unit) {
      activeUnit = null;
    }
    spriteManager.removeUnitSprite(unit);
  }

  public void removeCity(City city) {
    spriteManager.removeCitySprite(city);
  }

  public void setActiveUnit(Unit activeUnit) {
    this.activeUnit = activeUnit;
    effectsRenderer.setActiveUnit(activeUnit);
  }

  public void setTerrainStrip(ImageStrip terrainStrip) {
    this.terrainStrip = terrainStrip;
  }

  public void setRenderTerrain(boolean renderTerrain) {
    this.renderTerrain = renderTerrain;
  }

  public void setRenderSprites(boolean renderSprites) {
    spriteManager.setRenderSprites(renderSprites);
  }

  public boolean isRenderingTerrain() {
    return renderTerrain;
  }

  public boolean isRenderingSprites() {
    return spriteManager.isRenderingSprites();
  }

  /**
   * @see #spriteManager#getCursorLocation
   */
  public Tile getCursorLocation() {
    return (Tile) spriteManager.getCursorLocation();
  }

  /**
   * @return The effect range in tiles around the active cursor
   */
  public int getCursorEffectRange() {
    return spriteManager.getCursorEffectRange();
  }

  /**
   * @see #effectsRenderer#removeZones
   */
  public void removeZones() {
    effectsRenderer.removeZones();
  }

  /**
   * @see #effectsRenderer#setRenderArrowHead
   * @see #effectsRenderer#setArrowPath
   */
  public void showArrows(boolean showArrows) {
    effectsRenderer.setRenderArrowHead(showArrows);
    effectsRenderer.setRenderArrowPath(showArrows);
  }

  /**
   * @see #effectsRenderer#showArrowHead
   */
  public void showArrowPath(boolean showArrowPath) {
    effectsRenderer.setRenderArrowPath(showArrowPath);
  }

  /**
   * @see #effectsRenderer#setRenderArrowHead
   */
  public void showArrowHead(boolean showArrowHead) {
    effectsRenderer.setRenderArrowHead(showArrowHead);
  }

  /**
   * @see #effectsRenderer#setMoveZone
   */
  public void setMoveZone(Collection<Location> moveZone) {
    effectsRenderer.setMoveZone(moveZone);
  }

  /**
   * @see #effectsRenderer#setDropLocations
   */
  public void setDropLocations(List<Location> dropLocations, Location transportLocation) {
    effectsRenderer.setDropLocations(dropLocations, transportLocation);
  }

  /**
   * @see #effectsRenderer#setExplosionArea
   */
  public void setExplosionArea(Collection<Location> explosionArea) {
    effectsRenderer.setExplosionArea(explosionArea);
  }

  /**
   * @see #effectsRenderer#setAttackZone
   */
  public void setAttackZone(Collection<Location> attackZone) {
    effectsRenderer.setAttackZone(attackZone);
  }

  /**
   * @see #effectsRenderer#showMoveZone
   */
  public void showMoveZone() {
    effectsRenderer.showMoveZone();
  }

  /**
   * @see #effectsRenderer#removeMoveZone
   */
  public void removeMoveZone() {
    effectsRenderer.removeMoveZone();
  }

  /**
   * @see #effectsRenderer#showAttackZone
   */
  public void showAttackZone() {
    effectsRenderer.showAttackZone();
  }

  /**
   * @see #effectsRenderer#removeAttackZone
   */
  public void removeAttackZone() {
    effectsRenderer.removeAttackZone();
  }

  /**
   * Check if this map can be rendered in the center of the parent component
   * Where the parent component dimensions are given as width and height parameters
   *
   * @param width  width of the parent components this map is rendered in
   * @param height height of the parent component this map is rendered in
   * @return If the map can be rendered in the center of the parent components
   */
  public boolean canCenterMap(int width, int height) {
    return map.getWidth() < width && map.getHeight() < height;
  }

  public SpriteManager getSpriteManager() {
    return spriteManager;
  }
}