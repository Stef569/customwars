package com.customwars.client.model.gameobject;

import com.customwars.client.model.TestData;
import com.customwars.client.model.map.path.Mover;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Test the 2 methods in Terrain
 *
 * @author Stefan
 */
public class TerrainTest {
  private static final byte LEVEL_HEIGHT = 0;
  private static final byte NO_DEFENSE_BONUS = 0;
  private Terrain plain, river;

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();
  }

  @Before
  public void beforeEachTest() {
    plain = TerrainFactory.getTerrain(TestData.PLAIN);
    river = TerrainFactory.getTerrain(TestData.VERTICAL_RIVER);
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testCanBeTraverseBy() {
    // A plain can be traversed by all move types except Naval
    Assert.assertEquals(true, plain.canBeTraverseBy(TestData.MOVE_INF));
    Assert.assertEquals(true, plain.canBeTraverseBy(TestData.MOVE_MECH));
    Assert.assertEquals(true, plain.canBeTraverseBy(TestData.MOVE_TREAD));
    Assert.assertEquals(true, plain.canBeTraverseBy(TestData.MOVE_TIRES));
    Assert.assertEquals(true, plain.canBeTraverseBy(TestData.MOVE_AIR));
    Assert.assertEquals(false, plain.canBeTraverseBy(TestData.MOVE_NAVAL));

    // A river can be traversed by inf, mech and air move types only
    Assert.assertEquals(true, river.canBeTraverseBy(TestData.MOVE_INF));
    Assert.assertEquals(true, river.canBeTraverseBy(TestData.MOVE_MECH));
    Assert.assertEquals(false, river.canBeTraverseBy(TestData.MOVE_TREAD));
    Assert.assertEquals(false, river.canBeTraverseBy(TestData.MOVE_TIRES));
    Assert.assertEquals(true, river.canBeTraverseBy(TestData.MOVE_AIR));
    Assert.assertEquals(false, river.canBeTraverseBy(TestData.MOVE_NAVAL));
  }

  @Test
  public void testInfMoveCost() {
    List<Integer> plainMoveCosts = TestData.plainMoveCosts;
    int infMoveCost = plainMoveCosts.get(TestData.MOVE_INF);
    Assert.assertEquals(infMoveCost, plain.getMoveCost(TestData.MOVE_INF));
  }


  @Test
  public void testTankMovingOverRiverTerrain() {
    Mover tank = UnitFactory.getUnit(TestData.TANK);

    Assert.assertEquals(tank + " can move over river terrain?", false, river.canBeTraverseBy(tank.getMovementType()));
    Assert.assertEquals(Terrain.IMPASSIBLE, river.getMoveCost(tank.getMovementType()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createTerrainWithNullMoveCosts() {
    List<Integer> moveCosts = Arrays.asList(0, 2, null, 0);
    new Terrain(0, "", "", "", NO_DEFENSE_BONUS, LEVEL_HEIGHT, false, 0, moveCosts);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createTerrainWithNegativeMoveCosts() {
    List<Integer> moveCosts = Arrays.asList(0, 2, -50);
    new Terrain(0, "", "", "", NO_DEFENSE_BONUS, LEVEL_HEIGHT, false, 0, moveCosts);
  }
}
