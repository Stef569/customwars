package com.customwars.client.controller;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.connector.TerrainConnector;
import com.customwars.client.tools.MapUtil;

public class CityMapEditorControl implements MapEditorControl {
  private final TileMap<Tile> map;
  private final TerrainConnector terrainConnector;

  public CityMapEditorControl(TileMap<Tile> map) {
    this.map = map;
    terrainConnector = new TerrainConnector(map);
  }

  public void addToTile(Tile t, int id, Player player) {
    City city = MapUtil.addCityToMap(map, t, id, player);
    terrainConnector.turnSurroundingTerrains(t, city);
  }

  public void removeFromTile(Tile t) {
    Terrain terrain = TerrainFactory.getTerrain(0);
    t.setTerrain(terrain);
    terrainConnector.turnSurroundingTerrains(t, terrain);
  }

  public void fillMap(Map<Tile> map, int id) {
    // map can't be filled with cities
  }

  public boolean isTypeOf(Class c) {
    return c == City.class;
  }
}
