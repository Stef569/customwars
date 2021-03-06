package com.customwars.client.model.gameobject;

import com.customwars.client.action.CWAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.CWGameController;
import com.customwars.client.model.GameController;
import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import slick.HardCodedGame;

import java.awt.Color;


public class UnitTest {
  private Player player1;
  private Map map;
  private InGameContext inGameContext;

  @BeforeClass
  public static void beforeAllTests() {
    TestData.storeTestData();
  }

  @Before
  public void beforeEachTest() {
    player1 = new Player(0, Color.RED, "John", 1500, 0, false);
    Game game = HardCodedGame.getGame();
    map = game.getMap();
    inGameContext = new InGameContext();
    ControllerManager controllerManager = new ControllerManager(inGameContext);
    inGameContext.registerObj(Game.class, game);
    inGameContext.registerObj(ControllerManager.class, controllerManager);
    inGameContext.registerObj(GameController.class, new CWGameController(game, controllerManager));
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void joinTest() {
    Unit unit = UnitFactory.getUnit(TestData.INF);
    Unit target = UnitFactory.getUnit(TestData.INF);
    Tile t = map.getTile(0, 0);

    // Same Owner
    player1.addUnit(unit);
    player1.addUnit(target);

    // Same Location
    t.add(unit);
    t.add(target);

    // Target has less then 50% hp
    final int UNIT_HP = 1;
    final int TARGET_HP = 4;
    unit.setHp(UNIT_HP * 10);
    target.setHp(TARGET_HP * 10);

    CWAction joinAction = new JoinAction(unit, target);
    joinAction.invoke(inGameContext);

    // Only 1 unit remains after the join, the target unit
    Assert.assertEquals(1, t.getLocatableCount());
    Assert.assertEquals(target, t.getLastLocatable());
    Assert.assertFalse("player1 no longer contains the unit", player1.containsUnit(unit));
    Assert.assertTrue("player1 still contains the target", player1.containsUnit(target));

    int expectedHP = UNIT_HP + TARGET_HP;
    Assert.assertEquals(expectedHP, target.getHp());
  }

  @Test
  public void testDirect() {
    Unit inf = UnitFactory.getUnit("infantry");
    Unit tank = UnitFactory.getUnit("light_tank");
    Unit artillery = UnitFactory.getUnit("artillery");
    Unit apc = UnitFactory.getUnit("apc");
    Unit rockets = UnitFactory.getUnit("rockets");

    Assert.assertTrue(inf.isDirect());
    Assert.assertFalse(inf.isInDirect());
    Assert.assertTrue(tank.isDirect());
    Assert.assertFalse(tank.isInDirect());
    Assert.assertFalse(artillery.isDirect());
    Assert.assertTrue(artillery.isInDirect());
    Assert.assertFalse(apc.isDirect());
    Assert.assertFalse(apc.isInDirect());
    Assert.assertFalse(rockets.isDirect());
    Assert.assertTrue(rockets.isInDirect());
  }

  @Test
  public void testGetters() {
    // Some unit getters are special since they have co hook.
    // If the unit does not has an owner/co then don't throw an exception.
    UnitFactory.getUnit("infantry").getPrice();
    UnitFactory.getUnit("infantry").getMovePoints();
    UnitFactory.getUnit("infantry").getCaptureRate();
  }

  @Test
  public void testWillBeDestroyed() {
    Unit unit = UnitFactory.getUnit("infantry");

    Assert.assertFalse(unit.willBeDestroyedAfterTakingDamage(2));
    Assert.assertFalse(unit.willBeDestroyedAfterTakingDamage(4));
    Assert.assertFalse(unit.willBeDestroyedAfterTakingDamage(5));
    Assert.assertFalse(unit.willBeDestroyedAfterTakingDamage(6));
    Assert.assertFalse(unit.willBeDestroyedAfterTakingDamage(9));
    Assert.assertTrue(unit.willBeDestroyedAfterTakingDamage(10));
  }

  @Test
  public void testApcUnitCopy() {
    // Create the units
    Unit apc = UnitFactory.getUnit(TestData.APC);
    Unit infantry = UnitFactory.getUnit(TestData.INF);

    // load infantry into apc
    apc.add(infantry);

    // Add the apc to the map
    map.getTile(0, 0).add(apc);

    // Check transport values
    Assert.assertTrue(infantry.isInTransport());
    Assert.assertNotNull(infantry.getLocation());
    Assert.assertFalse(apc.isInTransport());
    Assert.assertTrue(apc.hasUnitsInTransport());

    // Create a copy of the apc
    Unit apcCopy = new Unit(apc);

    // Recheck transport values
    Assert.assertTrue(infantry.isInTransport());
    Assert.assertNotNull(infantry.getLocation());
    Assert.assertFalse(apcCopy.isInTransport());
    Assert.assertTrue(apcCopy.hasUnitsInTransport());
    Assert.assertTrue("The unit is located inside the apc unit", apcCopy.getUnitInTransport(0).isInTransport());
  }
}
