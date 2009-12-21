package slick;

import com.customwars.client.App;
import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameConfig;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.MapUtil;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Hardcoded game, useful for using in Slick tests
 */
public class HardCodedGame {
  private static Map<Tile> map;

  /**
   * Creates a Game with hard coded values, a default map is loaded.
   * The game is not inited and not started
   */
  public static Game getGame() {
    // 3 Game Players
    Player p_RED = new Player(0, Color.RED, "Stef", 19800, 0, false);
    Player p_BLUE = new Player(1, Color.BLUE, "JSR", 35500, 1, false);
    Player p_GREEN = new Player(2, Color.GREEN, "Kiwi", 25000, 2, false);
    List<Player> players = Arrays.asList(p_RED, p_BLUE, p_GREEN);
    GameConfig gc = new GameConfig();
    gc.setDayLimit(Turn.UNLIMITED);
    gc.setCityFunds(1000);

    return new Game(getMap(), players, gc);
  }

  public static Map<Tile> getMap() {
    int tileSize = App.getInt("plugin.tilesize", 32);
    Terrain plain = TerrainFactory.getTerrain(TestData.PLAIN);
    map = new Map<Tile>(15, 20, tileSize, plain);
    map.setFogOfWarOn(true);
    initMapProperties();
    initTerrains();

    // 3 Map Players, colors are suggestions game players overwrite them
    Player neutral = Player.createNeutralPlayer(Color.GRAY);
    Player p1 = new Player(0, Color.GREEN);
    Player p2 = new Player(1, Color.BLUE);
    Player p3 = new Player(2, Color.YELLOW);

    City greenHQ = MapUtil.addCityToMap(map, 0, 2, TestData.HQ, p1);
    City blueHQ = MapUtil.addCityToMap(map, 8, 5, TestData.HQ, p2);
    p1.setHq(greenHQ);
    p2.setHq(blueHQ);

    MapUtil.addCityToMap(map, 2, 5, TestData.FACTORY, neutral);
    MapUtil.addCityToMap(map, 3, 3, TestData.BASE, neutral);
    MapUtil.addCityToMap(map, 0, 0, TestData.FACTORY, p1);
    MapUtil.addCityToMap(map, 7, 8, TestData.FACTORY, p2);
    MapUtil.addCityToMap(map, 8, 8, TestData.MISSILE_SILO, neutral);
    MapUtil.addCityToMap(map, 2, 13, TestData.PORT, p1);
    MapUtil.addCityToMap(map, 2, 10, TestData.FACTORY, p3);
    MapUtil.addCityToMap(map, 5, 12, TestData.BASE, p3);
    MapUtil.addCityToMap(map, 0, 18, TestData.PORT, p3);
    MapUtil.addCityToMap(map, 1, 4, TestData.AIRPORT, p1);
    MapUtil.addCityToMap(map, 8, 18, TestData.AIRPORT, p3);

    MapUtil.addUnitToMap(map, 0, 5, TestData.INF, p1);
    MapUtil.addUnitToMap(map, 0, 4, TestData.ROCKETS, p1);
    MapUtil.addUnitToMap(map, 0, 8, TestData.MECH, p1);
    MapUtil.addUnitToMap(map, 1, 3, TestData.APC, p1);
    MapUtil.addUnitToMap(map, 1, 7, TestData.APC, p1);
    MapUtil.addUnitToMap(map, 1, 8, TestData.MECH, p1);
    MapUtil.addUnitToMap(map, 2, 6, TestData.INF, p1);
    MapUtil.addUnitToMap(map, 2, 7, TestData.ROCKETS, p1);
    MapUtil.addUnitToMap(map, 5, 6, TestData.INF, p1);
    MapUtil.addUnitToMap(map, 5, 7, TestData.INF, p2);
    MapUtil.addUnitToMap(map, 6, 5, TestData.TANK, p2);
    MapUtil.addUnitToMap(map, 8, 4, TestData.TANK, p2);

    MapUtil.addUnitToMap(map, 4, 2, TestData.INF, p2);
    MapUtil.addUnitToMap(map, 3, 2, TestData.TANK, p2);
    MapUtil.addUnitToMap(map, 5, 2, TestData.ROCKETS, p2);
    MapUtil.addTransporterToMap(map, 6, 2, TestData.APC, Arrays.asList(TestData.MECH), p2);
    MapUtil.addUnitToMap(map, 0, 10, TestData.MECH, p3);
    MapUtil.addUnitToMap(map, 2, 9, TestData.ROCKETS, p3);
    MapUtil.addUnitToMap(map, 2, 10, TestData.TANK, p3);
    MapUtil.addUnitToMap(map, 5, 9, TestData.INF, p3);
    MapUtil.addUnitToMap(map, 8, 9, TestData.INF, p3);

    return map;
  }

  private static void initTerrains() {
    MapUtil.addTerrainToMap(map, 2, 1, TestData.VERTICAL_RIVER);
    MapUtil.addTerrainToMap(map, 2, 2, TestData.VERTICAL_RIVER);
    MapUtil.addTerrainToMap(map, 5, 5, TestData.MOUNTAIN);

    Terrain sea = TerrainFactory.getTerrain(TestData.SEA);
    MapUtil.fillSuare(map, map.getTile(1, 15), 2, sea);
  }

  private static void initMapProperties() {
    map.setMapName("test map");
    map.setAuthor("Joe");
    map.setDescription("A small [15x20] test map containing a couple of units and some cities.");
  }
}
