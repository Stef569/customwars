package com.customwars.client.action;

import com.customwars.client.action.unit.MoveAnimatedAction;
import com.customwars.client.model.TestData;
import com.customwars.client.model.drop.DropLocationsQueue;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Location2D;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import slick.HardCodedGame;

import java.awt.Color;

public class TestActionCommand {
  private Game game;

  @Before
  public void beforeEachTest() {
    TestData.storeTestData();
    game = HardCodedGame.getGame();
    game.startGame();
  }

  @Test
  public void testActionCommandEncoder() {
    ActionCommandEncoder actionCommandEncoder = new ActionCommandEncoder("my action");
    final Location from = new Location2D(0, 0);
    final Location to = new Location2D(5, 0);
    final Player neutralPlayer = Player.createNeutralPlayer(Color.blue);

    actionCommandEncoder.add(from).add(to);
    Assert.assertEquals("my action 0 0 5 0", actionCommandEncoder.build());

    actionCommandEncoder.add(neutralPlayer);
    Assert.assertEquals("my action 0 0 5 0 -1", actionCommandEncoder.build());

    actionCommandEncoder.add(403);
    Assert.assertEquals("my action 0 0 5 0 -1 403", actionCommandEncoder.build());
  }

  @Test
  public void moveAnimatedActionSingleActionTest() {
    CWAction action = new MoveAnimatedAction(new Location2D(0, 0), new Location2D(5, 0));
    Assert.assertEquals("0 0 5 0", action.getActionCommand());
  }

  @Test
  public void testDropActionCommand() {
    Unit apc = game.getMap().getUnitOn(6, 2);
    Location apcLocation = apc.getLocation();
    DropLocationsQueue dropQueue = new DropLocationsQueue();
    dropQueue.addDropLocation(new Location2D(6, 1), apc.getLastUnitInTransport());
    CWAction loadAction = ActionFactory.buildDropAction(apc, apcLocation, apcLocation, dropQueue);

    // From - To - unit in transport index - drop location
    String expectedActionCommand = "drop 6 2 6 2 0 6 1";
    Assert.assertEquals(expectedActionCommand, loadAction.getActionCommand());
  }

  @Test
  public void testMoveActionCommand() {
    Unit apc = game.getMap().getUnitOn(6, 2);
    CWAction moveAction = ActionFactory.buildMoveAction(apc, new Location2D(6, 0));

    String expectedActionCommand = "move 6 2 6 0";
    Assert.assertEquals(expectedActionCommand, moveAction.getActionCommand());
  }

  @Test
  public void testCaptureActionCommand() {
    Unit inf = game.getMap().getUnitOn(5, 9);
    City city = game.getMap().getCityOn(7, 8);
    CWAction captureAction = ActionFactory.buildCaptureAction(inf, city);

    String expectedActionCommand = "capture 5 9 7 8";
    Assert.assertEquals(expectedActionCommand, captureAction.getActionCommand());
  }

  @Test
  public void testLoadActionCommand() {
    Unit mech = game.getMap().getUnitOn(0, 8);
    Unit apc = game.getMap().getUnitOn(1, 7);
    CWAction loadAction = ActionFactory.buildLoadAction(mech, apc);

    String expectedActionCommand = "load 0 8 1 7";
    Assert.assertEquals(expectedActionCommand, loadAction.getActionCommand());
  }

  @Test
  public void testSupplyActionCommand() {
    Unit apc = game.getMap().getUnitOn(1, 7);
    CWAction supplyAction = ActionFactory.buildSupplyAction(apc, new Location2D(1, 7));

    Assert.assertEquals("supply 1 7 1 7", supplyAction.getActionCommand());
  }

  @Test
  public void testJoinActionCommand() {
    Unit inf1 = game.getMap().getUnitOn(0, 5);
    Unit inf2 = game.getMap().getUnitOn(2, 6);
    CWAction joinAction = ActionFactory.buildJoinAction(inf1, inf2);

    Assert.assertEquals("join 0 5 2 6", joinAction.getActionCommand());
  }

  @Test
  public void testUnitvsUnitAttackActionCommand() {
    Unit rockets = game.getMap().getUnitOn(2, 7);
    Unit inf = game.getMap().getUnitOn(5, 7);
    CWAction attackAction = ActionFactory.buildUnitVsUnitAttackAction(rockets, inf, new Location2D(2, 7));

    Assert.assertEquals("attack_unit 2 7 2 7 5 7", attackAction.getActionCommand());
  }

  @Test
  public void testUnitvsCityAttackActionCommand() {
    Unit rockets = game.getMap().getUnitOn(2, 7);
    City city = game.getMap().getCityOn(0, 0);
    CWAction attackAction = ActionFactory.buildUnitVsCityAttackAction(rockets, city, new Location2D(2, 7));

    Assert.assertEquals("attack_city 2 7 2 7 0 0", attackAction.getActionCommand());
  }

  @Test
  public void testAddUnitActionCommand() {
    Unit unit = UnitFactory.getUnit(0);

    CWAction attackAction = ActionFactory.buildAddUnitToTileAction(unit, game.getPlayerByID(0), new Location2D(2, 13));
    // location - unit name - player ID
    Assert.assertEquals("build_unit 2 13 infantry 0", attackAction.getActionCommand());
  }

  @Test
  public void testLaunchRocketActionCommand() {
    Unit inf = game.getMap().getUnitOn(8, 9);
    City silo = game.getMap().getCityOn(8, 8);

    CWAction launchRocketAction = ActionFactory.buildLaunchRocketAction(inf, silo, new Location2D(0, 0));
    // inf location - silo location - rocket destination
    Assert.assertEquals("launch_rocket 8 9 8 8 0 0", launchRocketAction.getActionCommand());
  }

  @Test
  public void testTransformTerrainActionCommand() {
    Unit apc = game.getMap().getUnitOn(6, 2);
    Terrain plain = TerrainFactory.getTerrain("plain");

    CWAction transformTerrainAction = ActionFactory.buildTransformTerrainAction(apc, new Location2D(6, 1), plain);
    Assert.assertEquals("transform_terrain 6 2 6 1 plain", transformTerrainAction.getActionCommand());
  }

  @Test
  public void testFireFlareActionCommand() {
    Unit flare = game.getMap().getUnitOn(6, 2);

    CWAction fireFlareActionCommand = ActionFactory.buildFireFlareAction(flare, new Location2D(6, 1), new Location2D(0, 0));
    Assert.assertEquals("flare 6 2 6 1 0 0", fireFlareActionCommand.getActionCommand());
  }

  @Test
  public void testConstructCityActionCommand() {
    Unit flare = game.getMap().getUnitOn(6, 2);

    CWAction constructCityAction = ActionFactory.buildConstructCityAction(flare, "villa", new Location2D(0, 0));
    Assert.assertEquals("build_city 6 2 0 0 villa", constructCityAction.getActionCommand());
  }

  @Test
  public void testLoadCOActionCommand() {
    Unit flare = game.getMap().getUnitOn(6, 2);

    CWAction loadCOAction = ActionFactory.buildLoadCOAction(flare, new Location2D(0, 0));
    Assert.assertEquals("load_co 6 2 0 0", loadCOAction.getActionCommand());
  }

  @Test
  public void testProduceUnitActionCommand() {
    Unit apc = game.getMap().getUnitOn(6, 2);

    CWAction produceUnitAction = ActionFactory.buildProduceUnitAction(apc, "infantry");
    Assert.assertEquals("produce 6 2 infantry", produceUnitAction.getActionCommand());
  }

  @Test
  public void testEndGame() {
    CWAction endTurnAction = ActionFactory.buildEndTurnAction();
    Assert.assertEquals("end_turn", endTurnAction.getActionCommand());
  }
}
