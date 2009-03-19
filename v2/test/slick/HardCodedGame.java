package slick;

import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameConfig;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import tools.MapUtil;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Hardcoded game, useful for using in Slick tests
 */
public class HardCodedGame {
  // 1  neutral and 3 Game Players
  public static Player neutral = new Player(Player.NEUTRAL_PLAYER_ID, Color.GRAY, true, null, "Neutral", 0, -1, false);
  public static Player p_RED = new Player(0, Color.RED, false, null, "Stef", 3800, 0, false);
  public static Player p_BLUE = new Player(1, Color.BLUE, false, null, "JSR", 8500, 1, false);
  public static Player p_GREEN = new Player(2, Color.GREEN, false, null, "Kiwi", 10000, 2, false);
  private static Map<Tile> map;

  /**
   * Creates a Game with hard coded values, a default map is loaded.
   * The game is not inited and not started
   */
  public static Game getGame() {
    List<Player> players = Arrays.asList(p_RED, p_BLUE, neutral, p_GREEN);
    GameConfig gc = new GameConfig();
    gc.setTurnLimit(500);
    gc.setCityfunds(1000);

    return new Game(getMap(), players, gc);
  }

  public static Map<Tile> getMap() {
    map = new Map<Tile>(10, 15, 32, 4);
    map.setFogOfWarOn(true);
    initMapProperties();
    MapUtil.fillWithTiles(map, TerrainFactory.getTerrain(TestData.PLAIN));
    map.getTile(2, 1).setTerrain(TerrainFactory.getTerrain(TestData.VERTICAL_RIVER));

    // 3 Map Players, colors are suggestions game players overwrite them
    Player neutral = new Player(Player.NEUTRAL_PLAYER_ID, Color.GRAY, true, null);
    Player p1 = new Player(0, Color.GREEN, false, null);
    Player p2 = new Player(1, Color.BLUE, false, null);
    Player p3 = new Player(2, Color.YELLOW, false, null);

    City blueHQ = addCityToMap(8, 5, TestData.HQ, p2);
    City greenHQ = addCityToMap(0, 2, TestData.HQ, p1);
    p2.setHq(blueHQ);
    p1.setHq(greenHQ);

    addCityToMap(2, 5, TestData.FACTORY, neutral);
    addCityToMap(3, 3, TestData.BASE, neutral);
    addCityToMap(0, 0, TestData.FACTORY, p1);
    addCityToMap(7, 8, TestData.FACTORY, p2);

    addUnitToMap(0, 5, TestData.INF, p1);
    addUnitToMap(0, 4, TestData.ROCKETS, p1);
    addUnitToMap(0, 8, TestData.MECH, p1);
    addUnitToMap(1, 3, TestData.APC, p1);
    addUnitToMap(1, 7, TestData.APC, p1);
    addUnitToMap(1, 8, TestData.MECH, p1);
    addUnitToMap(2, 6, TestData.INF, p1);
    addUnitToMap(2, 7, TestData.ROCKETS, p1);
    addUnitToMap(5, 6, TestData.INF, p1);
    addUnitToMap(5, 7, TestData.INF, p2);
    addUnitToMap(6, 5, TestData.TANK, p2);
    addUnitToMap(8, 4, TestData.TANK, p2);

    addUnitToMap(4, 2, TestData.INF, p2);
    addUnitToMap(3, 2, TestData.TANK, p2);
    addUnitToMap(5, 2, TestData.ROCKETS, p2);
    addUnitToMap(6, 2, TestData.APC, p2);
    addUnitToMap(0, 10, TestData.MECH, p3);
    addUnitToMap(2, 9, TestData.ROCKETS, p3);
    addUnitToMap(2, 10, TestData.TANK, p3);
    addUnitToMap(5, 9, TestData.INF, p3);
    addCityToMap(2, 10, TestData.FACTORY, p3);
    addCityToMap(5, 12, TestData.BASE, p3);

    return map;
  }

  private static void initMapProperties() {
    map.addProperty("NAME", "test map");
    map.addProperty("VERSION", "1.0");
    map.addProperty("CREATOR", "Joe");
    map.addProperty("DESCRIPTION", "A small [10x10] test map containing a couple of units and some cities.");
  }

  private static City addCityToMap(int col, int row, int cityID, Player owner) {
    Tile t = map.getTile(col, row);
    City city = CityFactory.getCity(cityID);
    city.setOwner(owner);
    t.setTerrain(city);
    city.setLocation(t);
    return city;
  }

  private static void addUnitToMap(int col, int row, int unitID, Player owner) {
    Tile t = map.getTile(col, row);
    Unit unit = UnitFactory.getUnit(unitID);
    unit.setOwner(owner);
    t.add(unit);
  }
}
