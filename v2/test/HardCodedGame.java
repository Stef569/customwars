package test;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.model.map.gameobject.Terrain;

/**
 * Hardcoded game data, useful for testing
 */
public class HardCodedGame {
  public static final int MOVE_INF = 0;
  public static final int MOVE_MECH = 1;
  public static final int MOVE_TREAD = 2;
  public static final int MOVE_TIRES = 3;
  public static final int MOVE_AIR = 4;
  public static final int MOVE_NAVAL = 5;

  // Movecosts: INF, MECH, TREAD, TIRES, AIR, NAVAL
  public static Byte[] plainMoveCosts = new Byte[]{1, 1, 1, 2, 1, Terrain.IMPASSIBLE};
  public static Byte[] riverMoveCosts = new Byte[]{1, 1, Terrain.IMPASSIBLE, Terrain.IMPASSIBLE, 1, Terrain.IMPASSIBLE};
  public static Byte[] mountainMoveCosts = new Byte[]{3, 2, Terrain.IMPASSIBLE, Terrain.IMPASSIBLE, 1, Terrain.IMPASSIBLE};

  // The id is the index within the terrain images.
  public static Terrain plain = new Terrain(0, "plain", "", (byte) 0, (byte) 0, false, plainMoveCosts);
  public static Terrain verticalRiver = new Terrain(20, "River", "", (byte) 0, (byte) -1, false, riverMoveCosts);
  public static Terrain mountain = new Terrain(17, "Mountain", "", (byte) 4, (byte) 2, false, mountainMoveCosts);
  private static TileMap<Tile> map = new TileMap<Tile>(10, 10, 32);

  public static TileMap<Tile> getMap() {
    fillWithTerrain(plain);
    map.getTile(0, 3).setTerrain(verticalRiver);
    map.getTile(0, 1).setTerrain(verticalRiver);
    map.getTile(0, 2).setTerrain(verticalRiver);
    map.getTile(5, 5).setFogged(true);
    map.getTile(6, 5).setFogged(true);
    map.getTile(4, 5).setFogged(true);
    map.getTile(6, 6).setTerrain(mountain);
    map.getTile(6, 7).setTerrain(mountain);
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
