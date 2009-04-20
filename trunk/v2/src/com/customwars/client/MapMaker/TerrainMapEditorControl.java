package com.customwars.client.MapMaker;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import tools.MapUtil;

public class TerrainMapEditorControl implements MapEditorControl {
  private TerrainConnector terrainConnector;

  public TerrainMapEditorControl(TileMap<Tile> map) {
    terrainConnector = new TerrainConnector(map);
  }

  @Override
  public void addToTile(Tile t, int id) {
    Terrain userChosenTerrain = getTerrain(id);
    Terrain bestFittingTerrain = terrainConnector.connectTerrain(t, userChosenTerrain);
    t.setTerrain(bestFittingTerrain);
  }

  public void removeFromTile(Tile t) {
    // Terrains can't be removed, they are overwriten.
  }

  public void fillMap(Map<Tile> map, int terrainID) {
    MapUtil.fillWithTerrain(map, getTerrain(terrainID));
  }

  private Terrain getTerrain(int terrainID) {
    return TerrainFactory.getBaseTerrains().get(terrainID);
  }

  public boolean isTypeOf(Class c) {
    return c == Terrain.class;
  }
}
