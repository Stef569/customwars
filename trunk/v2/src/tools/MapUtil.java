package tools;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;

import java.util.List;

/**
 * Utilities for a map useful in a map editor
 * Each function accepts a Location or x,y coordinates as the position to add a gameobject to the map
 *
 * @author stefan
 */
public class MapUtil {
  /**
   * This is a static utility class. It cannot be constructed.
   */
  private MapUtil() {
  }

  /**
   * Fill the center and each tile in a square around the center with the given terrain
   *
   * @param center  The center of the square
   * @param radius  Amount of tiles from the center
   * @param map     The map on which we want to fill a square on
   * @param terrain The terrain to fill the square with
   */
  public static void fillSuare(TileMap<Tile> map, Tile center, int radius, Terrain terrain) {
    center.setTerrain(terrain);
    for (Tile t : map.getSquareIterator(center, radius)) {
      t.setTerrain(terrain);
    }
  }

  public static City addCityToMap(TileMap<Tile> map, Location location, int cityID, Player owner) {
    return addCityToMap(map, location.getCol(), location.getRow(), cityID, owner);
  }

  public static City addCityToMap(TileMap<Tile> map, int col, int row, int cityID, Player owner) {
    Tile t = map.getTile(col, row);
    City city = CityFactory.getCity(cityID);
    owner.addCity(city);
    city.setLocation(t);
    t.setTerrain(city);
    return city;
  }

  public static Unit addUnitToMap(TileMap<Tile> map, Location location, int unitID, Player owner) {
    return addUnitToMap(map, location.getCol(), location.getRow(), unitID, owner);
  }

  public static Unit addUnitToMap(TileMap<Tile> map, int col, int row, int unitID, Player owner) {
    Tile t = map.getTile(col, row);
    Unit unit = UnitFactory.getUnit(unitID);
    owner.addUnit(unit);
    t.add(unit);
    return unit;
  }

  public static Unit addTransporterToMap(TileMap<Tile> map, Location location, int transportID, List<Integer> unitsInTransport, Player owner) {
    return addTransporterToMap(map, location.getCol(), location.getRow(), transportID, unitsInTransport, owner);
  }

  public static Unit addTransporterToMap(TileMap<Tile> map, int col, int row, int transportID, List<Integer> unitsInTransport, Player owner) {
    Unit unit = addUnitToMap(map, col, row, transportID, owner);

    for (Integer unitID : unitsInTransport) {
      Unit unitInTransport = UnitFactory.getUnit(unitID);
      unitInTransport.setOwner(owner);
      Unit apc = (Unit) map.getTile(col, row).getLastLocatable();
      apc.add(unitInTransport);
    }
    return unit;
  }

  public static void addTerrainToMap(TileMap<Tile> map, Location location, int terrainID) {
    addTerrainToMap(map, location.getCol(), location.getRow(), terrainID);
  }

  public static void addTerrainToMap(TileMap<Tile> map, int col, int row, int terrainID) {
    Terrain terrain = TerrainFactory.getTerrain(terrainID);
    map.getTile(col, row).setTerrain(terrain);
  }
}
