package com.customwars.client.model.gameobject;

import com.customwars.client.model.testdata.TestData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test the 2 methods in Terrain
 *
 * @author Stefan
 */
public class TerrainTest {
  private Terrain plain, river;

  @Before
  public void beforeEachTest() {
    plain = TerrainFactory.getTerrain(TestData.PLAIN);
    river = TerrainFactory.getTerrain(TestData.VERTICAL_RIVER);
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
}
