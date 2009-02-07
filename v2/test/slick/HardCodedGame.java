package test.slick;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.model.map.gameobject.Terrain;
import org.apache.log4j.Logger;

/**
 * Hardcoded game, useful for testing
 */
public class HardCodedGame {
  private static final Logger logger = Logger.getLogger(HardCodedGame.class);
  private static TileMap<Tile> map = new TileMap<Tile>(10, 10, 32);

  // Hardcoded game
  // Movecosts: INF, MECH, TREAD, TIRES, AIR, NAVAL, TRANSPORT
  private static Byte[] plainMoveCosts = new Byte[]{1, 1, 1, 2, 1};
  private static Byte[] riverMoveCosts = new Byte[]{1, 1, Terrain.IMPASSIBLE, Terrain.IMPASSIBLE, 1};

  public static TileMap<Tile> getMap() {
    Terrain plain = new Terrain(0, "plain", "", (byte) 0, (byte) 0, plainMoveCosts);
    Terrain verticalRiver = new Terrain(20, "River", "", (byte) 0, (byte) -1, riverMoveCosts);

    fillWithTerrain(plain);
    map.getTile(0, 3).setTerrain(verticalRiver);
    map.getTile(0, 1).setTerrain(verticalRiver);
    map.getTile(0, 2).setTerrain(verticalRiver);
    return map;
  }

  private static void fillWithTerrain(Terrain terrain) {
    for (int col = 0; col < map.getCols(); col++) {
      for (int row = 0; row < map.getRows(); row++) {
        Tile t = new Tile(col, row, terrain);
        map.setTile(col, row, t);
      }
    }
  }
}
