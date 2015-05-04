package com.customwars.client.controller.mapeditor;

import com.customwars.client.App;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.connector.TerrainConnector;
import com.customwars.client.tools.MapUtil;
import org.apache.log4j.Logger;

import java.awt.Color;

/**
 * Add/Remove cities from a map in map editor mode
 */
public class CityMapEditorControl implements MapEditorControl {
  private static final Logger logger = Logger.getLogger(CityMapEditorControl.class);
  private static final Color NEUTRAL_COLOR = App.getColor("plugin.neutral_color");
  private final Map map;
  private final TerrainConnector terrainConnector;

  public CityMapEditorControl(Map map) {
    this.map = map;
    terrainConnector = new TerrainConnector(map);
  }

  public void addToTile(Tile t, int cityID, Color color) {
    removePreviousCity(t);
    City city = CityFactory.getCity(cityID);
    city = checkCity(color, city);
    color = checkColor(color, city);

    Player mapPlayer = map.getPlayer(color);
    MapUtil.addCityToMap(map, t, city, mapPlayer);
    terrainConnector.turnSurroundingTerrains(t, city);
  }

  private City checkCity(Color color, City cityToAdd) {
    // The neutral HQ cannot be used
    // Use a city instead
    if (NEUTRAL_COLOR.equals(color) && cityToAdd.isHQ()) {
      logger.debug("The use of a neutral HQ is not allowed");
      cityToAdd = CityFactory.getCity(0);
    }

    // A player can only have 1 HQ
    // Overwrite previous HQ with city
    // Place the HQ on chosen location t
    if(cityToAdd.isHQ()) {
      City hq = map.getPlayer(color).getHq();
      if (hq != null) {
        Player player = map.getPlayer(color);
        City city = CityFactory.getCity(0);
        Location hqLocation = hq.getLocation();
        MapUtil.addCityToMap(map, hqLocation, city, player);
      }
    }
    return cityToAdd;
  }

  private Color checkColor(Color color, City city) {
    // Walls, pipes and other types of cities cannot be captured.
    // They default to a neutral owner.
    if (!city.canBeCaptured()) {
      color = NEUTRAL_COLOR;
    }
    return color;
  }

  private void removePreviousCity(Tile t) {
    // if overriding a city, remove it first
    if (t.getTerrain() instanceof City) {
      City city = (City) t.getTerrain();
      Player cityOwner = city.getOwner();
      cityOwner.removeCity(city);
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

  public void fillMap(Map map, int id) {
    // map can't be filled with cities
  }

  public boolean isTypeOf(Class c) {
    return c == City.class;
  }
}
