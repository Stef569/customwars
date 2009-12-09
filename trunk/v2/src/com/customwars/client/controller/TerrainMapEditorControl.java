package com.customwars.client.controller;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.connector.TerrainConnector;

public class TerrainMapEditorControl implements MapEditorControl {
  private final TerrainConnector terrainConnector;

  public TerrainMapEditorControl(TileMap<Tile> map) {
    terrainConnector = new TerrainConnector(map);
  }

  public void addToTile(Tile t, int id, Player player) {
    Terrain userChosenTerrain = getTerrain(id);
    addTerrain(t, userChosenTerrain);
  }

  public void removeFromTile(Tile t) {
    Terrain plainTerrain = getTerrain(0);
    addTerrain(t, plainTerrain);
  }

  public void fillMap(Map<Tile> map, int terrainID) {
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
