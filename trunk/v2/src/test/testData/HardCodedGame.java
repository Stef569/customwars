package test.testData;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Player;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.DefaultMoveStrategy;
import com.customwars.client.model.rules.CityRules;
import com.customwars.client.model.rules.MapRules;
import com.customwars.client.model.rules.UnitRules;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Hardcoded game data, useful for testing
 */
public class HardCodedGame {
  public static final int ARMY_BRANCH_GROUND = 1;

  public static final int MOVE_INF = 0;
  public static final int MOVE_MECH = 1;
  public static final int MOVE_TREAD = 2;
  public static final int MOVE_TIRES = 3;
  public static final int MOVE_AIR = 4;
  public static final int MOVE_NAVAL = 5;

  // Movecosts: INF, MECH, TREAD, TIRES, AIR, NAVAL
  private static int IMP = Terrain.IMPASSIBLE;
  public static List<Integer> plainMoveCosts = Arrays.asList(1, 1, 1, 2, 1, IMP);
  public static List<Integer> riverMoveCosts = Arrays.asList(1, 1, IMP, IMP, 1, IMP);
  public static List<Integer> mountainMoveCosts = Arrays.asList(3, 2, IMP, IMP, 1, IMP);

  // Terrains: The id is the index within the terrain images.
  public static Terrain plain = new Terrain(0, "plain", "", 0, 0, false, plainMoveCosts);
  public static Terrain verticalRiver = new Terrain(20, "River", "", 0, -1, false, riverMoveCosts);
  public static Terrain mountain = new Terrain(17, "Mountain", "", 4, 2, false, mountainMoveCosts);

  // Units
  public static Unit infantry = new Unit(0, "Infantry", "", 3000, 3, 5, 20, 20, 0, true, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_INF, 0, 0);
  public static Unit mech = new Unit(1, "Mech", "", 3000, 3, 3, 20, 20, 0, true, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_MECH, 0, 0);
  public static Unit artillery = new Unit(9, "Artillery", "", 4000, 4, 1, 20, 20, 0, false, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);

  // Weapons
  public static Weapon SMG = new Weapon(0, "SMG", "", 1500, 1, 1, Weapon.UNLIMITED_AMMO, false);
  public static Weapon CANNON = new Weapon(1, "Cannon", "", 1500, 2, 5, 3, false);

  // City
  public static City city = new City(0, "City", "", 0, 0, plainMoveCosts, 1, false, null, null, null, 20, 1, 0, 0);

  // Players
  public static Player p_RED = new Player(0, Color.RED, false, null, "Stef", Integer.MAX_VALUE, 0, false);
  public static Player p_BLUE = new Player(0, Color.BLUE, false, null, "JSR", 8500, 0, false);
  public static Player p_GREEN = new Player(0, Color.GREEN, false, null, "Ben", 500, 1, false);

  private static Map<Tile> map = new Map<Tile>(10, 10, 32, -5, true);

  // Rules
  private static MapRules mapRules = new MapRules(map);
  private static UnitRules unitRules = new UnitRules(map);
  private static CityRules cityRules = new CityRules();

  public static Map<Tile> getMap() {
    fillWithTerrain(plain);
    map.getTile(0, 3).setTerrain(verticalRiver);
    map.getTile(0, 1).setTerrain(verticalRiver);
    map.getTile(0, 2).setTerrain(verticalRiver);
    map.getTile(2, 2).add(getMech());
    map.getTile(2, 3).add(getMech());

    City city = getCity();
    city.setLocation(map.getTile(4, 4));
    map.getTile(4, 4).setTerrain(city);
    map.getTile(5, 5).setFogged(true);
    map.getTile(6, 5).setFogged(true);
    map.getTile(4, 5).setFogged(true);
    map.getTile(6, 6).setTerrain(mountain);
    map.getTile(6, 6).add(getInf());
    map.getTile(8, 4).add(getArtillery());
    map.getTile(6, 7).setTerrain(mountain);
    initRules();
    initMapProperties();
    return map;
  }

  private static void initMapProperties() {
    map.addProperty("NAME", "test map");
    map.addProperty("VERSION", "1.0");
    map.addProperty("CREATOR", "Joe");
    map.addProperty("DESCRIPTION", "A small [10x10] test map containing a couple of units and some cities.");
  }

  private static void fillWithTerrain(Terrain terrain) {
    for (int col = 0; col < map.getCols(); col++) {
      for (int row = 0; row < map.getRows(); row++) {
        Tile t = new Tile(col, row, terrain);
        map.setTile(col, row, t);
      }
    }
  }

  private static void initRules() {
    map.setRules(mapRules);
    for (Tile t : map.getAllTiles()) {
      Unit unit = map.getUnitOn(t);
      initUnit(unit);

      City city = map.getCityOn(t);
      initCity(city);
    }
  }

  public static Unit getInf() {
    Unit infCopy = new Unit(infantry);
    infCopy.setOwner(p_BLUE);
    initUnit(infCopy);
    return infCopy;
  }

  public static Unit getMech() {
    Unit mechUnit = new Unit(mech);
    mechUnit.setOwner(p_RED);
    mechUnit.setPrimaryWeapon(SMG);
    initUnit(mechUnit);
    return mechUnit;
  }

  public static Unit getArtillery() {
    Unit infUnit = new Unit(artillery);
    infUnit.setOwner(p_GREEN);
    infUnit.setPrimaryWeapon(CANNON);
    initUnit(infUnit);
    return infUnit;
  }

  public static City getCity() {
    City cityCopy = new City(city);
    cityCopy.setOwner(p_BLUE);
    initCity(cityCopy);
    return cityCopy;
  }

  private static void initUnit(Unit unit) {
    if (unit != null) {
      unit.setRules(unitRules);
      unit.setMoveStrategy(new DefaultMoveStrategy(unit, map));
    }
  }

  private static void initCity(City city) {
    if (city != null) {
      city.setRules(cityRules);
    }
  }
}
