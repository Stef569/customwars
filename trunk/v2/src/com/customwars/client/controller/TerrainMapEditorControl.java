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
    Terrain newTerrain = terrainConnector.connectTerrain(t, userChosenTerrain);
    t.setTerrain(newTerrain);
    terrainConnector.turnSurroundingTerrains(t, newTerrain);
  }

  public void removeFromTile(Tile t) {
    t.setTerrain(TerrainFactory.getTerrain(0));
  }

  public void fillMap(Map<Tile> map, int terrainID) {
    map.fillWithTerrain(getTerrain(terrainID));
  }

  private Terrain getTerrain(int terrainID) {
    return TerrainFactory.getBaseTerrains().get(terrainID);
  }

  public boolean isTypeOf(Class c) {
    return c == Terrain.class;
  }
}
