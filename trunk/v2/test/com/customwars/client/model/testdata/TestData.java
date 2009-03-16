package com.customwars.client.model.testdata;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.customwars.client.model.map.UnitFight;

import java.util.Arrays;
import java.util.List;

/**
 * Contains test data
 * The ids for Unit,Terrain,City are the indexes within the corresponding image.
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

  public static final int PLAIN = 0;
  public static final int VERTICAL_RIVER = 20;

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

  // Terrains
  private static Terrain plain = new Terrain(0, "plain", "", 0, 0, false, plainMoveCosts);
  private static Terrain verticalRiver = new Terrain(20, "River", "", 0, -1, false, riverMoveCosts);
  private static Terrain mountain = new Terrain(17, "Mountain", "", 4, 2, false, mountainMoveCosts);

  // Units
  private static Unit infantry = new Unit(0, "Infantry", "", 3000, 3, 3, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, 0, true, false, false, false, false, true, null, ARMY_BRANCH_GROUND, MOVE_INF, 0, 0);
  private static Unit mech = new Unit(1, "Mech", "", 3000, 3, 3, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, 0, true, false, false, false, false, true, null, ARMY_BRANCH_GROUND, MOVE_MECH, 0, 0);
  private static Unit apc = new Unit(3, "Apc", "", 8000, 5, 1, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 3, 0, false, false, true, true, true, true, Arrays.asList(MOVE_INF, MOVE_MECH), ARMY_BRANCH_GROUND, MOVE_TREAD, 1, 1);
  private static Unit tank = new Unit(4, "Tank", "", 7000, 6, 3, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, 0, false, false, false, false, false, true, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);
  private static Unit rocket = new Unit(7, "Rockets", "", 15000, 2, 1, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, 0, false, false, false, false, true, false, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);
  private static Unit artillery = new Unit(9, "Artillery", "", 4000, 3, 1, UNIT_MAX_HP, MAX_UNIT_SUPPLIES, 0, 0, false, false, false, false, false, true, null, ARMY_BRANCH_GROUND, MOVE_TREAD, 0, 0);

  // Weapons
  private static Weapon smg = new Weapon(0, "smg", "", 1500, 1, 1, Weapon.UNLIMITED_AMMO, false);
  private static Weapon tankCannon = new Weapon(1, "Tank Cannon", "nonee", 1500, 1, 1, 9, false);
  private static Weapon cannon = new Weapon(2, "Cannon", "nonee", 1500, 2, 3, 9, false);
  private static Weapon rockets = new Weapon(3, "rockets", "nonee", 1500, 3, 6, 9, false);

  // City
  private static City base = new City(0, "Base", "", 0, 0, plainMoveCosts, 1, true, Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(INF, MECH), null, 20, CITY_HEAL_RATE, 0, 0);
  private static City factory = new City(1, "Factory", "", 0, 0, plainMoveCosts, 1, true, Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(INF, MECH), Arrays.asList(ARMY_BRANCH_GROUND), 20, CITY_HEAL_RATE, 0, 0);
  private static City hq = new City(4, "HQ", "", 0, 0, plainMoveCosts, 1, true, Arrays.asList(ARMY_BRANCH_GROUND), Arrays.asList(INF, MECH), null, 20, CITY_HEAL_RATE, 0, 0);

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

    tank.setPrimaryWeapon(tankCannon);
    UnitFactory.addUnit(tank);

    rocket.setPrimaryWeapon(rockets);
    UnitFactory.addUnit(artillery);

    rocket.setPrimaryWeapon(rockets);
    UnitFactory.addUnit(rocket);

    CityFactory.addCity(base);
    CityFactory.addCity(factory);
    CityFactory.addCity(hq);

    UnitFight.setBaseDMG(initBaseDmg());
    UnitFight.setAltDMG(initAltDmg());
  }

  public static void clearTestData() {
    TerrainFactory.clear();
    WeaponFactory.clear();
    UnitFactory.clear();
    CityFactory.clear();
  }

  private static int[][] initBaseDmg() {
    int[][] baseDMG = new int[32][32];
    //Setup primary weapon base damage
    //                         0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
    //                       INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
    baseDMG[0] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[1] = new int[]{-1, -1, 55, 15, 85, 65, 85, 70, 85, 75, -1, -1, -1, -1, -1, -1, -1, -1, 15, 5, 55, -1, -1, -1, -1, 65, 65, 55, -1, -1, -1, 30};
    baseDMG[2] = new int[]{-1, -1, 55, 15, 85, 65, 85, 70, 85, 75, 10, 5, 1, 1, -1, -1, -1, -1, 15, 10, 55, 10, 1, -1, -1, 65, 65, 55, -1, -1, 5, 20};
    baseDMG[3] = new int[]{-1, -1, 85, 55, 105, 105, 105, 105, 105, 105, 35, 30, 10, 10, -1, -1, -1, -1, 45, 25, 85, 35, 10, -1, -1, 95, 95, 85, -1, -1, 25, 30};
    baseDMG[4] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[5] = new int[]{105, 105, 25, 10, 60, 45, 55, 50, 55, 50, -1, -1, -1, -1, 105, 105, 65, 75, 5, 1, 25, -1, -1, 75, 120, 55, 55, 25, 115, 45, -1, 30};
    baseDMG[6] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 115, 115, 100, 100, -1, -1, -1, -1, -1, 100, 120, -1, -1, -1, 115, 65, -1, -1};
    baseDMG[7] = new int[]{90, 85, 70, 45, 80, 75, 80, 75, 80, 70, 55, 50, 60, 40, -1, -1, -1, -1, 40, 15, 70, 55, 45, -1, -1, 80, 80, 70, -1, -1, 50, 5};
    baseDMG[8] = new int[]{95, 90, 80, 55, 90, 85, 90, 80, 85, 80, 60, 60, 85, 55, -1, -1, -1, -1, 50, 25, 80, 60, 60, -1, -1, 85, 85, 80, -1, -1, 60, 15};
    baseDMG[9] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[10] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[11] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 25, 25, 90, 5, -1, -1, -1, -1, -1, -1, -1, 25, 5, -1, -1, 65, 65, -1, -1, -1, 25, -1};
    baseDMG[12] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 95, 25, 55, 55, -1, -1, -1, -1, -1, -1, -1, 95, 75, -1, -1, -1, -1, -1, -1, -1, 95, -1};
    baseDMG[13] = new int[]{95, 90, 80, 55, 90, 85, 90, 80, 85, 80, 95, 95, 95, 50, -1, -1, -1, -1, 50, 25, 80, 95, 60, -1, -1, 95, 95, 80, -1, -1, 95, 20};
    baseDMG[14] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[15] = new int[]{-1, -1, 55, 25, 55, 25, 65, 65, 65, 60, 25, 25, 25, 25, 95, 65, -1, -1, 20, 10, 55, 25, 25, -1, -1, 65, 65, 55, -1, -1, 55, 25};
    baseDMG[16] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 100, 100, 55, 100, -1, -1, -1, -1, -1, 85, 120, -1, -1, -1, 120, 65, -1, -1};
    baseDMG[17] = new int[]{110, 110, 105, 95, 105, 95, 105, 105, 105, 105, 95, 50, 95, 75, -1, -1, -1, -1, 90, 35, 105, 95, 75, -1, -1, 105, 105, 105, -1, -1, 85, 35};
    baseDMG[18] = new int[]{-1, -1, 105, 75, 125, 115, 125, 115, 125, 125, 40, 30, 15, 15, -1, -1, -1, -1, 55, 35, 105, 40, 15, -1, -1, 125, 125, 105, -1, -1, 30, 35};
    baseDMG[19] = new int[]{-1, -1, 180, 125, 195, 195, 195, 195, 195, 195, 75, 65, 45, 45, -1, -1, -1, -1, 115, 65, 180, 105, 45, -1, -1, 195, 195, 180, -1, -1, 60, 45};
    baseDMG[20] = new int[]{95, 90, 80, 55, 90, 85, 90, 80, 85, 80, 60, 60, 85, 55, 105, 105, 65, 75, 50, 25, 80, 60, 60, 75, 120, 85, 85, 80, 105, 45, 60, 15};
    baseDMG[21] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[22] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 115, 115, 100, 100, -1, -1, -1, -1, -1, 100, 120, -1, -1, -1, 115, 65, -1, -1};
    baseDMG[23] = new int[]{90, 90, 75, 70, 85, 50, 85, 75, 85, 85, 65, 35, 55, 45, 95, 85, 45, 70, 60, 15, 80, 65, 45, 55, 120, 85, 85, 80, 85, 45, 65, 30};
    baseDMG[24] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[25] = new int[]{-1, -1, 95, 75, 95, 95, 80, 80, 80, 95, 50, 35, 35, 15, -1, -1, -1, -1, 55, 30, 70, 50, 15, -1, -1, 95, 95, 95, -1, -1, 35, 30};
    baseDMG[26] = new int[]{95, 90, 75, 50, 85, 80, 85, 80, 85, 75, 55, 50, 50, 40, -1, -1, -1, -1, 45, 20, 75, 55, 45, -1, -1, 85, 85, 75, -1, -1, 50, 10};
    baseDMG[27] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[28] = new int[]{50, 45, 40, 25, 45, 40, 45, 40, 45, 40, 30, 30, 45, 25, 50, 50, 35, 40, 20, 10, 40, 30, 30, 45, 100, 45, 45, 40, 55, 22, 30, 5};
    baseDMG[29] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    baseDMG[30] = new int[]{-1, -1, 85, 55, 105, 105, 105, 105, 105, 105, 55, 90, 25, 25, -1, -1, -1, -1, 45, 25, 100, 55, 25, -1, -1, 95, 95, 105, -1, -1, 55, 35};
    baseDMG[31] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    return baseDMG;
  }

  private static int[][] initAltDmg() {
    int[][] altDMG = new int[32][32];
    //Setup secondary weapon base damage
    //                         0     1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31
    //                       INF  MECH  TANK MEDTK RECON  AAIR MISSL  ARTY   RKT   APC LANDR CRUSR SUBMR BSHIP TCOPT BCOPT FGHTR BOMBR NEOTK MGTNK PRUNR BBOAT CARRI STELH BBOMB BCRFT ACRFT SRUNR ZEPPN SPYPL DESTR OOZIM
    altDMG[0] = new int[]{55, 45, 5, 1, 12, 5, 26, 15, 25, 14, -1, -1, -1, -1, 30, 7, -1, -1, 1, 1, 5, -1, -1, -1, -1, 25, 25, -1, -1, -1, -1, 20};
    altDMG[1] = new int[]{65, 55, 6, 1, 18, 6, 35, 32, 35, 20, -1, -1, -1, -1, 35, 9, -1, -1, 1, 1, 6, -1, -1, -1, -1, 35, 35, -1, -1, -1, -1, 20};
    altDMG[2] = new int[]{75, 70, 6, 1, 40, 6, 30, 45, 55, 45, -1, -1, -1, -1, 40, 10, -1, -1, 1, 1, 6, -1, -1, -1, -1, 35, 35, -1, -1, -1, -1, 20};
    altDMG[3] = new int[]{105, 95, 8, 1, 45, 7, 35, 45, 55, 45, -1, -1, -1, -1, 45, 12, -1, -1, 1, 1, 8, -1, -1, -1, -1, 45, 45, -1, -1, -1, -1, 20};
    altDMG[4] = new int[]{70, 65, 6, 1, 55, 4, 28, 45, 55, 45, -1, -1, -1, -1, 35, 10, -1, -1, 1, 1, 6, -1, -1, -1, -1, 35, 35, -1, -1, -1, -1, 20};
    altDMG[5] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[6] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[7] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[8] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[9] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[10] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[11] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 105, 105, 85, 100, -1, -1, -1, -1, -1, 100, 120, -1, -1, -1, -1, -1, -1, -1};
    altDMG[12] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[13] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[14] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[15] = new int[]{75, 75, 6, 1, 30, 6, 35, 25, 35, 25, -1, -1, -1, -1, 95, 65, -1, -1, 1, 1, 6, -1, -1, -1, -1, 35, 35, -1, -1, -1, -1, 20};
    altDMG[16] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[17] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[18] = new int[]{125, 115, 10, 1, 65, 17, 55, 65, 75, 65, -1, -1, -1, -1, 55, 22, -1, -1, 1, 1, 10, -1, -1, -1, -1, 65, 65, -1, -1, -1, -1, 20};
    altDMG[19] = new int[]{135, 125, 10, 1, 65, 17, 55, 65, 75, 65, -1, -1, -1, -1, 55, 22, -1, -1, 1, 1, 10, -1, -1, -1, -1, 75, 75, -1, -1, -1, -1, 30};
    altDMG[20] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[21] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[22] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[23] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[24] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[25] = new int[]{85, 75, 6, 1, 35, 5, 25, 35, 45, 35, -1, -1, -1, -1, 35, 8, -1, -1, 1, 1, 6, -1, -1, -1, -1, 35, 35, -1, -1, -1, -1, 20};
    altDMG[26] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[27] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[28] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[29] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    altDMG[30] = new int[]{105, 95, 8, 1, 45, 7, 35, 45, 55, 45, -1, -1, -1, -1, 45, 12, -1, -1, 1, 1, 8, -1, -1, -1, -1, 45, 45, -1, -1, -1, -1, 20};
    altDMG[31] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    return altDMG;
  }
}
