package com.customwars.client.model.map;

import com.customwars.client.model.map.gameobject.Terrain;

/**
 * A Tile contains a Terrain
 * it can be fogged
 *
 * @author stefan
 */
public class Tile extends Location {
  private boolean fogged;
  private Terrain terrain;

  public Tile(int col, int row, Terrain terrain) {
    super(col, row);
    this.terrain = terrain;
  }

  public Tile(int col, int row, Terrain terrain, boolean fogged) {
    this(col, row, terrain);
    this.fogged = fogged;
  }

  public void setTerrain(Terrain terrain) {
    this.terrain = terrain;
  }

  public void setFogged(boolean fogged) {
    this.fogged = fogged;
  }

  public boolean isFogged() {
    return fogged;
  }

  public Terrain getTerrain() {
    return terrain;
  }

  public String toString() {
    String toString = "[(" + col + "," + row + ")";
    if (terrain != null) {
      toString += " Terrain=" + terrain;
    }
    return toString + "]";
  }
}
