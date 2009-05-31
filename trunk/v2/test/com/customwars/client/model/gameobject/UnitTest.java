package com.customwars.client.model.gameobject;

import com.customwars.client.action.CWAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import slick.HardCodedGame;

import java.awt.Color;


public class UnitTest {
  private Player player1;
  private Map<Tile> map;
  private InGameContext inGameContext;

  @Before
  public void beforeEachTest() {
    player1 = new Player(0, Color.RED, false, null, "John", 1500, 0, false);
    inGameContext = new InGameContext();
    Game game = HardCodedGame.getGame();
    inGameContext.setGame(game);
    inGameContext.setControllerManager(new ControllerManager(inGameContext));
    map = game.getMap();
  }

  @Test
  /**
   * Unit joins the target
   *
   * The join conditions are:
   * Our unit is on the same tile as the target, both units are of the same type
   * The two units must have the same owner
   * The target unit must have 50% or less HP
   */
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
}
