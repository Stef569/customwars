package slick;

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
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.testdata.TestData;
import tools.MapUtil;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Hardcoded game, useful for using in Slick tests
 */
public class HardCodedGame {
  // Players
  public static Player p_RED = new Player(0, Color.RED, false, null, "Stef", Integer.MAX_VALUE, 0, false);
  public static Player p_BLUE = new Player(1, Color.BLUE, false, null, "JSR", 8500, 0, false);
  public static Player p_GREEN = new Player(2, Color.GREEN, false, null, "Ben", 500, 1, false);
  public static Player p_GRAY = new Player(Color.GRAY, null, -1);

  private static Map<Tile> map = new Map<Tile>(10, 10, 32, 4, true);

  /**
   * Creates a Game with hard coded values, a default map is loaded.
   * The Game is started
   */
  public static Game getStartedGame() {
    List<Player> players = Arrays.asList(p_RED, p_BLUE, p_GREEN, p_GRAY);
    GameConfig gc = new GameConfig();
    gc.setTurnLimit(500);

    Game game = new Game(getMap(), players, gc);
    game.startGame();
    return game;
  }

  public static Map<Tile> getMap() {
    Map<Tile> map = new Map<Tile>(10, 15, 32, 3, true);
    initMapProperties();
    MapUtil.fillWithTiles(map, TerrainFactory.getTerrain(TestData.PLAIN));
    map.getTile(2, 1).setTerrain(TerrainFactory.getTerrain(TestData.VERTICAL_RIVER));

    // Map Players, colors are suggestions game players overwrite them
    Player p_GRAY = new Player(Player.NEUTRAL_PLAYER_ID, Color.GRAY, true, null);
    Player p_GREEN = new Player(0, Color.GREEN, false, null);
    Player p_BLUE = new Player(1, Color.BLUE, false, null);

    City blueHQ = addCityToMap(map, 8, 5, TestData.HQ, p_BLUE);
    City greenHQ = addCityToMap(map, 0, 2, TestData.HQ, p_GREEN);
    p_BLUE.setHq(blueHQ);
    p_GREEN.setHq(greenHQ);

    addCityToMap(map, 2, 5, TestData.FACTORY, p_GRAY);
    addCityToMap(map, 3, 3, TestData.BASE, p_GRAY);
    addCityToMap(map, 0, 0, TestData.FACTORY, p_GREEN);
    addCityToMap(map, 7, 8, TestData.FACTORY, p_BLUE);

    addUnitToMap(map, 5, 6, TestData.INF, p_BLUE);
    addUnitToMap(map, 5, 7, TestData.INF, p_BLUE);
    addUnitToMap(map, 6, 5, TestData.TANK, p_BLUE);
    addUnitToMap(map, 8, 4, TestData.TANK, p_BLUE);

    addUnitToMap(map, 4, 2, TestData.INF, p_BLUE);
    addUnitToMap(map, 3, 2, TestData.TANK, p_BLUE);
    addUnitToMap(map, 5, 2, TestData.ROCKET, p_BLUE);
    addUnitToMap(map, 6, 2, TestData.APC, p_BLUE);
    return map;
  }

  private static void initMapProperties() {
    map.addProperty("NAME", "test map");
    map.addProperty("VERSION", "1.0");
    map.addProperty("CREATOR", "Joe");
    map.addProperty("DESCRIPTION", "A small [10x10] test map containing a couple of units and some cities.");
  }

  private static City addCityToMap(TileMap<Tile> map, int col, int row, int cityID, Player owner) {
    Tile t = map.getTile(col, row);
    City city = CityFactory.getCity(cityID);
    owner.addCity(city);
    t.setTerrain(city);
    city.setLocation(t);
    return city;
  }

  private static void addUnitToMap(TileMap<Tile> map, int col, int row, int unitID, Player owner) {
    Tile t = map.getTile(col, row);
    Unit unit = UnitFactory.getUnit(unitID);
    owner.addUnit(unit);
    t.add(unit);
  }
}
