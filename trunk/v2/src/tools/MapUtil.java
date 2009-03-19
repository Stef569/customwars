package tools;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;

/**
 * Utilities for a map including:
 * filling a map with terrains, filling a square with terrains,...
 * Useful in a map editor
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
   * Fill a map with tiles containing terrain, terrain cannot be null
   */
  public static void fillWithTiles(TileMap<Tile> map, Terrain terrain) {
    for (int col = 0; col < map.getCols(); col++) {
      for (int row = 0; row < map.getRows(); row++) {
        map.setTile(new Tile(col, row, terrain));
      }
    }
  }

  public static void fillWithTerrain(TileMap<Tile> map, Terrain terrain) {
    for (Tile t : map.getAllTiles()) {
      t.setTerrain(terrain);
    }
  }

  public static void fillSuare(Location center, int size, TileMap<Tile> map, Terrain terrain) {
    for (Tile t : map.getSquareIterator(center, size)) {
      t.setTerrain(terrain);
    }
  }
}
