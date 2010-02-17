package com.customwars.client.controller.mapeditor;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.connector.TerrainConnector;
import com.customwars.client.tools.MapUtil;

import java.awt.Color;

/**
 * Add/Remove cities from a map in map editor mode
 */
public class CityMapEditorControl implements MapEditorControl {
  private final Map<Tile> map;
  private final TerrainConnector terrainConnector;

  public CityMapEditorControl(Map<Tile> map) {
    this.map = map;
    terrainConnector = new TerrainConnector(map);
  }

  public void addToTile(Tile t, int cityID, Color color) {
    removePreviousCity(t);
    City city = CityFactory.getCity(cityID);

    if (city.isSpecialNeutralCity()) {
      color = Color.GRAY;
    }

    Player mapPlayer = map.getPlayer(color);
    MapUtil.addCityToMap(map, t, city, mapPlayer);
    terrainConnector.turnSurroundingTerrains(t, city);
  }

  private void removePreviousCity(Tile t) {
    // if overriding a city, remove it first
    if (t.getTerrain() instanceof City) {
      City city = (City) t.getTerrain();
      Player cityOwner = city.getOwner();
      cityOwner.removeCity(city);

      if (city.isHQ()) {
        cityOwner.setHq(null);
      }
    }
  }

  public boolean removeFromTile(Tile t) {
    if (t.getTerrain() instanceof City) {
      removePreviousCity(t);
      Terrain terrain = TerrainFactory.getTerrain(0);
      t.setTerrain(terrain);
      terrainConnector.turnSurroundingTerrains(t, terrain);
      return true;
    } else {
      return false;
    }
  }

  public void fillMap(Map<Tile> map, int id) {
    // map can't be filled with cities
  }

  public boolean isTypeOf(Class c) {
    return c == City.class;
  }
}
