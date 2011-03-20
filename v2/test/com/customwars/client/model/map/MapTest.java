package com.customwars.client.model.map;

import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.tools.MapUtil;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;

/**
 * Test functions in the Map class
 */
public class MapTest {
  private Map map;

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();
  }

  @Before
  public void beforeEachTest() {
    Terrain plain = TerrainFactory.getTerrain(TestData.PLAIN);
    map = new Map(10, 15, 32, plain);
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testFreeDropLocation() {
    Tile apcLocation = map.getTile(1, 1);

    // Get the adjacent tiles around the apc
    Tile northDropLocation = map.getTile(1, 0);
    Tile eastDropLocation = map.getTile(2, 1);
    Tile southDropLocation = map.getTile(1, 2);
    Tile westDropLocation = map.getTile(0, 1);

    // Create and add a transport in the map
    Unit apc = UnitFactory.getUnit(TestData.APC);
    Player p_blue = new Player(0, Color.blue);
    MapUtil.addUnitToMap(map, apcLocation, apc, p_blue);

    // Add inf to APC
    Unit infInAPC = UnitFactory.getUnit(TestData.INF);
    p_blue.addUnit(infInAPC);
    apc.add(infInAPC);

    // Create and add a hidden tank east of the apc
    Unit hiddenTank = UnitFactory.getUnit(TestData.TANK);
    hiddenTank.setHidden(true);
    MapUtil.addUnitToMap(map, eastDropLocation, hiddenTank, p_blue);

    // Create and add a infantry unit south of the apc
    Unit inf = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, southDropLocation, inf, p_blue);

    // Create and add a friendly transport in the map
    // West of apc 1
    Unit apc2 = UnitFactory.getUnit(TestData.APC);
    MapUtil.addUnitToMap(map, westDropLocation, apc2, p_blue);

    // North is a free adjacent tile since it does not contain any unit
    Assert.assertTrue(map.canDropAtLeast1Unit(apc, northDropLocation));

    // East is taken by a hidden unit so it's free since we can't see that unit yet.
    Assert.assertTrue(map.canDropAtLeast1Unit(apc, eastDropLocation));

    // South drop location is already taken by a unit
    Assert.assertFalse(map.canDropAtLeast1Unit(apc, southDropLocation));

    // Drop into another transport(apc2 on westDropLocation) is not supported
    Assert.assertFalse(map.canDropAtLeast1Unit(apc, westDropLocation));

    // Drop into self is allowed
    Assert.assertTrue(map.canDropAtLeast1Unit(apc, apcLocation));
  }

  @Test
  public void testFreeDropLocationNotTraversable() {
    Tile apcLocation = map.getTile(1, 1);

    // Get the adjacent tiles around the apc
    Tile northDropLocation = map.getTile(1, 0);
    Tile eastDropLocation = map.getTile(2, 1);
    Tile southDropLocation = map.getTile(1, 2);
    Tile westDropLocation = map.getTile(0, 1);

    // Create and add a transport in the map (We allows 2 units in the transport this time)
    Unit apc = UnitFactory.getUnit(TestData.APC);
    Player p_blue = new Player(0, Color.blue);
    MapUtil.addUnitToMap(map, apcLocation, apc, p_blue);

    // Add artillery into APC
    Unit artilleryInAPC = UnitFactory.getUnit(TestData.ARTILLERY);
    p_blue.addUnit(artilleryInAPC);
    apc.add(artilleryInAPC);

    // Add mech into APC
    Unit mechInAPC = UnitFactory.getUnit(TestData.MECH);
    p_blue.addUnit(mechInAPC);
    apc.add(mechInAPC);

    // Add an ocean north of the apc
    northDropLocation.setTerrain(TerrainFactory.getTerrain("ocean"));

    // Add a river east of the apc
    eastDropLocation.setTerrain(TerrainFactory.getTerrain("river"));

    // North is a not free since none of the units in the transport can be dropped on it.
    Assert.assertFalse(map.isFreeDropLocation(apc, artilleryInAPC, northDropLocation));
    Assert.assertFalse(map.isFreeDropLocation(apc, mechInAPC, northDropLocation));
    Assert.assertFalse(map.canDropAtLeast1Unit(apc, northDropLocation));

    // artillery cannot be dropped into the river east, but a mech can swim
    Assert.assertFalse(map.isFreeDropLocation(apc, artilleryInAPC, eastDropLocation));
    Assert.assertTrue(map.isFreeDropLocation(apc, mechInAPC, eastDropLocation));

    // The apc can drop at least 1 unit east(the mech).
    Assert.assertTrue(map.canDropAtLeast1Unit(apc, eastDropLocation));

    // 3 plains - 1 ocean gives 3 open drop locations.
    Assert.assertEquals(3, map.getFreeDropLocations(apc).size());
  }

  @Test
  public void testNormalisedMap() {
    // Add some players to the map. The ID's way off
    Player p1 = new Player(205, Color.BLACK);
    Unit inf1 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, map.getTile(0, 0), inf1, p1);

    Player p2 = new Player(280, Color.BLUE);
    Unit inf2 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, map.getTile(1, 0), inf2, p2);

    Player p3 = new Player(999, Color.GREEN);
    Unit inf3 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, map.getTile(2, 0), inf3, p3);
    Assert.assertEquals(3, map.getNumPlayers());

    map.normalise();
    Assert.assertEquals(3, map.getNumPlayers());

    Player pBlack = map.getPlayer(Color.BLACK);
    Player pBlue = map.getPlayer(Color.BLUE);
    Player pGreen = map.getPlayer(Color.GREEN);

    // The player ID's have been changed to 0,1,2
    // The order in which this is done is unknown thought
    Assert.assertTrue(pBlack.getId() < 3);
    Assert.assertEquals(1, pBlack.getArmyCount());

    Assert.assertTrue(pBlue.getId() < 3);
    Assert.assertEquals(1, pBlue.getArmyCount());

    Assert.assertTrue(pGreen.getId() < 3);
    Assert.assertEquals(1, pGreen.getArmyCount());
  }

  @Test
  public void testSurroundedByEnemies() {
    Tile unitLocation = map.getTile(1, 1);

    // Get the adjacent tiles around the inf
    Tile northTile = map.getTile(1, 0);
    Tile eastTile = map.getTile(2, 1);
    Tile southTile = map.getTile(1, 2);
    Tile westTile = map.getTile(0, 1);

    // 2 players with different teams
    Player p_blue = new Player(0, Color.blue, "blue", 0, 0, false);
    Player p_red = new Player(1, Color.red, "red", 0, 1, false);

    // Create and add an inf to the map
    Unit inf = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, unitLocation, inf, p_blue);
    Assert.assertFalse(map.isSurroundedByEnemyUnits(inf));

    // Create and add a tank NORTH of the inf
    Unit tank = UnitFactory.getUnit(TestData.TANK);
    MapUtil.addUnitToMap(map, northTile, tank, p_red);
    Assert.assertFalse(map.isSurroundedByEnemyUnits(inf));

    // Create and add a hidden tank EAST of the apc
    Unit hiddenTank = UnitFactory.getUnit(TestData.TANK);
    hiddenTank.setHidden(true);
    MapUtil.addUnitToMap(map, eastTile, hiddenTank, p_red);
    Assert.assertFalse(map.isSurroundedByEnemyUnits(inf));

    // Create and add an apc unit SOUTH of the inf
    Unit apc = UnitFactory.getUnit(TestData.APC);
    southTile.setFogged(true);
    MapUtil.addUnitToMap(map, southTile, apc, p_red);
    Assert.assertFalse(map.isSurroundedByEnemyUnits(inf));

    // Create and add an apc unit WEST of the apc
    Unit apc2 = UnitFactory.getUnit(TestData.APC);
    MapUtil.addUnitToMap(map, westTile, apc2, p_red);
    Assert.assertTrue(map.isSurroundedByEnemyUnits(inf));
  }
}
