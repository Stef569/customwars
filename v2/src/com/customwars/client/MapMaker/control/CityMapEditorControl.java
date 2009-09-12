package com.customwars.client.MapMaker.control;

import com.customwars.client.MapMaker.TerrainConnector;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import tools.MapUtil;

public class CityMapEditorControl implements MapEditorControl {
  private TileMap<Tile> map;
  private TerrainConnector terrainConnector;

  public CityMapEditorControl(TileMap<Tile> map) {
    this.map = map;
    terrainConnector = new TerrainConnector(map);
  }

  public void addToTile(Tile t, int id, Player player) {
    City city = MapUtil.addCityToMap(map, t, id, player);
    terrainConnector.turnSurroundingTerrains(t, city);
  }

  public void removeFromTile(Tile t) {
    t.setTerrain(TerrainFactory.getTerrain(0));
  }

  public void fillMap(Map<Tile> map, int id) {
    // map can't be filled with cities
  }

  public boolean isTypeOf(Class c) {
    return c == City.class;
  }
}
