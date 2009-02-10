package model.map.gameobject;

import com.customwars.client.model.map.gameobject.Terrain;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import testData.HardCodedGame;

/**
 * Test the 2 methods in Terrain
 *
 * @author Stefan
 */
public class TerrainTest {
  private Terrain plain, river;

  @Before
  public void beforeEachTest() {
    plain = HardCodedGame.plain;
    river = HardCodedGame.verticalRiver;
  }

  @Test
  public void testCanBeTraverseBy() {
    // A plain can be traversed by all move types except Naval
    Assert.assertEquals(true, plain.canBeTraverseBy(HardCodedGame.MOVE_INF));
    Assert.assertEquals(true, plain.canBeTraverseBy(HardCodedGame.MOVE_MECH));
    Assert.assertEquals(true, plain.canBeTraverseBy(HardCodedGame.MOVE_TREAD));
    Assert.assertEquals(true, plain.canBeTraverseBy(HardCodedGame.MOVE_TIRES));
    Assert.assertEquals(true, plain.canBeTraverseBy(HardCodedGame.MOVE_AIR));
    Assert.assertEquals(false, plain.canBeTraverseBy(HardCodedGame.MOVE_NAVAL));

    // A river can be traversed by inf, mech and air move types only
    Assert.assertEquals(true, river.canBeTraverseBy(HardCodedGame.MOVE_INF));
    Assert.assertEquals(true, river.canBeTraverseBy(HardCodedGame.MOVE_MECH));
    Assert.assertEquals(false, river.canBeTraverseBy(HardCodedGame.MOVE_TREAD));
    Assert.assertEquals(false, river.canBeTraverseBy(HardCodedGame.MOVE_TIRES));
    Assert.assertEquals(true, river.canBeTraverseBy(HardCodedGame.MOVE_AIR));
    Assert.assertEquals(false, river.canBeTraverseBy(HardCodedGame.MOVE_NAVAL));
  }

  @Test
  public void testInfMoveCost() {
    Byte[] plainMoveCosts = HardCodedGame.plainMoveCosts;
    int infMoveCost = plainMoveCosts[HardCodedGame.MOVE_INF];
    Assert.assertEquals(infMoveCost, plain.getMoveCost(HardCodedGame.MOVE_INF));
  }
}
