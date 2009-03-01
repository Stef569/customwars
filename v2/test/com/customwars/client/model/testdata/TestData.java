package com.customwars.client.model.testdata;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Contains test data
 *
 * @author stefan
 */
public class TestData {
  public static final int ARMY_BRANCH_GROUND = 1;

  public static final int MOVE_INF = 0;
  public static final int MOVE_MECH = 1;
  public static final int MOVE_TREAD = 2;
  public static final int MOVE_TIRES = 3;
  public static final int MOVE_AIR = 4;
  public static final int MOVE_NAVAL = 5;

  public static final int VERTICAL_RIVER = 20;
  public static final int PLAIN = 0;

  public static final int INF = 0;
  public static final int MECH = 1;
  public static final int APC = 3;
  public static final int TANK = 4;
  public static final int ROCKETS = 7;
  public static final int ARTILLERY = 9;

  public static final int SMG = 0;
  public static final int CANNON = 1;
  public static final int ROCKET = 2;

  public static final int BASE = 0;
  public static final int FACTORY = 1;
  public static final int AIRPORT = 2;
  public static final int PORT = 3;
  public static final int HQ = 4;

  public static final int CITY_HEAL_RATE = 2;
  public static final int UNIT_MAX_HP = 10;
  public static final int MAX_UNIT_SUPPLIES = 20;

  // Movecosts: INF, MECH, TREAD, TIRES, AIR, NAVAL
  private static int IMP = Terrain.IMPASSIBLE;
  public static List<Integer> plainMoveCosts = Arrays.asList(1, 1, 1, 2, 1, IMP);
  public static List<Integer> riverMoveCosts = Arrays.asList(1, 1, IMP, IMP, 1, IMP);
  public static List<Integer> mountainMoveCosts = Arrays.asList(3, 2, IMP, IMP, 1, IMP);

  // Terrains: The id is the index within the terrain images.
  private static Terrain plain = new Terrain(0, "plain", "", 0, 0, false, plainMoveCosts);
  private static Terrain verticalRiver = new Terrain(20, "River", "", 0, -1, false, riverMoveCosts);
  private static Terrain mountain = new Terrain(17, "Mountain", "", 4, 2, false, mountainMoveCosts);

  // Units
  private static Unit infantry = new Unit(0, "Infantry", "", 3000, 3, 3, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, true, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_INF, 0, 0);
  private static Unit mech = new Unit(1, "Mech", "", 3000, 3, 3, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, true, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_MECH, 0, 0);
  private static Unit apc = new Unit(3, "Apc", "", 8000, 5, 1, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, false, false, true, true, true, Arrays.asList(MOVE_INF, MOVE_MECH), ARMY_BRANCH_GROUND, MOVE_TREAD, 1, 1);
  private static Unit tank = new Unit(4, "Tank", "", 7000, 6, 3, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, false, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);
  private static Unit rocket = new Unit(7, "Rockets", "", 15000, 2, 1, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, false, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);
  private static Unit artillery = new Unit(9, "Artillery", "", 4000, 3, 1, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, false, false, false, false, false, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);

  // Weapons
  private static Weapon smg = new Weapon(0, "smg", "", 1500, 1, 1, Weapon.UNLIMITED_AMMO, false);
  private static Weapon cannon = new Weapon(1, "Cannon", "nonee", 1500, 2, 3, 9, false);
  private static Weapon rockets = new Weapon(2, "rockets", "nonee", 1500, 3, 6, 9, false);

  // City
  private static City base = new City(0, "Base", "", 0, 0, plainMoveCosts, 1, false, Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(ARMY_BRANCH_GROUND), null, 20, CITY_HEAL_RATE, 0, 0);
  private static City factory = new City(1, "Factory", "", 0, 0, plainMoveCosts, 1, false, Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(ARMY_BRANCH_GROUND), 20, CITY_HEAL_RATE, 0, 0);
  private static City hq = new City(4, "HQ", "", 0, 0, plainMoveCosts, 1, false, Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(ARMY_BRANCH_GROUND), 20, CITY_HEAL_RATE, 0, 0);

  public static void storeTestData() {
    clearTestData();
    TerrainFactory.addTerrain(plain);
    TerrainFactory.addTerrain(verticalRiver);
    TerrainFactory.addTerrain(mountain);

    WeaponFactory.addWeapon(smg);
    WeaponFactory.addWeapon(cannon);
    WeaponFactory.addWeapon(rockets);

    infantry.setPrimaryWeapon(smg);
    UnitFactory.addUnit(infantry);

    mech.setPrimaryWeapon(smg);
    UnitFactory.addUnit(mech);

    UnitFactory.addUnit(apc);
    UnitFactory.addUnit(tank);

    rocket.setPrimaryWeapon(rockets);
    UnitFactory.addUnit(artillery);

    rocket.setPrimaryWeapon(rockets);
    UnitFactory.addUnit(rocket);

    CityFactory.addCity(base);
    CityFactory.addCity(factory);
    CityFactory.addCity(hq);
  }

  public static void clearTestData() {
    TerrainFactory.clear();
    WeaponFactory.clear();
    UnitFactory.clear();
    CityFactory.clear();
  }
}
