package com.customwars.client.controller.mapeditor;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.connector.TerrainConnector;

import java.awt.Color;

/**
 * Add/Remove terrains and fill a map with terrains in map editor mode
 */
public class TerrainMapEditorControl implements MapEditorControl {
  private final TerrainConnector terrainConnector;

  public TerrainMapEditorControl(TileMap<Tile> map) {
    terrainConnector = new TerrainConnector(map);
  }

  public void addToTile(Tile t, int terrainID, Color color) {
    Terrain userChosenTerrain = getTerrain(terrainID);
    addTerrain(t, userChosenTerrain);
  }

  public boolean removeFromTile(Tile t) {
    Terrain plainTerrain = getTerrain(0);
    addTerrain(t, plainTerrain);
    return true;
  }

  public void fillMap(Map map, int terrainID) {
    map.fillWithTerrain(getTerrain(terrainID));
  }

  /**
   * #1 Find the best matching terrain and add the best matching terrain
   * to Tile t.
   * #2 Connect surrounding terrains
   */
  private void addTerrain(Tile t, Terrain terrain) {
    Terrain bestMatch = terrainConnector.connectTerrain(t, terrain);
    t.setTerrain(bestMatch);
    terrainConnector.turnSurroundingTerrains(t, bestMatch);
  }

  private Terrain getTerrain(int terrainID) {
    return TerrainFactory.getBaseTerrains().get(terrainID);
  }

  public boolean isTypeOf(Class c) {
    return c == Terrain.class;
  }
}
