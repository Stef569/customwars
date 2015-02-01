package com.customwars.client.model.ai;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.model.TestData;
import com.customwars.client.model.ai.unit.DefaultUnitAI;
import com.customwars.client.model.ai.unit.UnitOrder;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location2D;
import com.customwars.client.model.map.Map;
import com.customwars.client.script.ScriptManager;
import com.customwars.client.tools.TextMapParser;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UnitAIAttackTest {

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  public Game createGame(Map map) {
    Player p1 = new Player(0, Color.RED, "Stef", 22000, 0, true, new BasicCO("penny"));
    Player p2 = new Player(1, Color.BLUE, "JSR", 30000, 1, false, new BasicCO("penny"));
    List<Player> players = Arrays.asList(p1, p2);
    GameRules gameRules = new GameRules();
    gameRules.setDayLimit(Turn.UNLIMITED);
    gameRules.setCityFunds(1000);
    gameRules.setFogOfWar(false);

    Game game = new Game(map, players, gameRules);
    game.startGame();
    return game;
  }

  private Map createMap(String[][] source) {
    TextMapParser parser = new TextMapParser(source);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    return map;
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testAttackUnitOnHQPriority() throws Exception {
    COFactory.setScriptManager(new ScriptManager());
    String[][] smallMap = new String[][]{
      new String[]{"INFT-P2,HQTR-P1", ""},
      new String[]{"", ""},
      new String[]{"MECH-P1", "CITY-P*"},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);
    City hq = map.getCityOn(0, 0);
    Unit enemyMech = map.getUnitOn(0, 0);
    hq.capture(enemyMech);

    // An infantry of P2 is capturing the HQ!
    // Attacking that infantry with our mech has high priority
    Unit hero = map.getUnitOn(0, 2);
    DefaultUnitAI advisor = new DefaultUnitAI(game, null);
    List<UnitOrder> orders = advisor.findBestUnitOrders();

    Assert.assertEquals(new Location2D(0, 1), orders.get(0).moveDestination);
    Assert.assertEquals(new Location2D(0, 0), orders.get(0).target);
  }

  @Test
  public void testAttackUnitOnHQPriority2() throws Exception {
    COFactory.setScriptManager(new ScriptManager());
    String[][] smallMap = new String[][]{
      new String[]{"INFT-P2,HQTR-P1", ""},
      new String[]{"INFT-P2", ""},
      new String[]{"INFT-P1", "INFT-P2,CITY-P*"},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);

    City hq = map.getCityOn(0, 0);
    Unit enemyMech = map.getUnitOn(0, 0);
    hq.capture(enemyMech);

    // An infantry of P2 is capturing the HQ!
    // Attacking that infantry with our infantry has high priority
    // Problem: We are surrounded by enemies
    // Attack one of the enemies instead
    Unit hero = map.getUnitOn(0, 2);
    DefaultUnitAI advisor = new DefaultUnitAI(game, null);
    List<UnitOrder> orders = advisor.findBestUnitOrders();

    Assert.assertFalse(orders.isEmpty());
    Assert.assertEquals(new Location2D(0, 2), orders.get(0).moveDestination);
    List<Location2D> possibleTargets = Arrays.asList(new Location2D(0, 1), new Location2D(1, 2));
    Assert.assertTrue(possibleTargets.contains(orders.get(0).target));
  }

  @Test
  public void testDirectMechVsMechAttack() throws Exception {
    COFactory.setScriptManager(new ScriptManager());
    String[][] smallMap = new String[][]{
      new String[]{"MECH-P2", "", ""},
      new String[]{"", "", ""},
      new String[]{"MECH-P1", "", ""},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);

    Unit enemyMech = map.getUnitOn(0, 0);
    Unit hero = map.getUnitOn(0, 2);

    DefaultUnitAI advisor = new DefaultUnitAI(game, null);
    List<UnitOrder> orders = advisor.findBestUnitOrders();

    Assert.assertEquals("The unit location", hero.getLocation(), orders.get(0).unitLocation);
    Assert.assertEquals("The unit moves towards the enemy", new Location2D(0, 1), orders.get(0).moveDestination);
    Assert.assertEquals("The unit attacks the enemy", new Location2D(0, 0), orders.get(0).target);
  }

  @Test
  public void testDirectAntiAirVsMechAttack() throws Exception {
    COFactory.setScriptManager(new ScriptManager());
    String[][] smallMap = new String[][]{
      new String[]{"", "TANK-P2", "", "MECH-P2"},
      new String[]{"", "", "", ""},
      new String[]{"AAIR-P1", "", "", ""},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);
    Unit enemyMech = map.getUnitOn(3, 0);
    Unit hero = map.getUnitOn(0, 2);

    DefaultUnitAI advisor = new DefaultUnitAI(game, null);
    List<UnitOrder> orders = advisor.findBestUnitOrders();

    Assert.assertEquals("The unit location", new Location2D(0, 2), orders.get(0).unitLocation);
    Assert.assertEquals("The unit moves towards the enemy", new Location2D(3, 1), orders.get(0).moveDestination);
    Assert.assertEquals("The unit attacks the enemy", new Location2D(3, 0), orders.get(0).target);
  }

  @Test
  public void testInDirectAttack() throws Exception {
    COFactory.setScriptManager(new ScriptManager());
    String[][] smallMap = new String[][]{
      new String[]{"MECH-P2", "", ""},
      new String[]{"", "", ""},
      new String[]{"ARTY-P1", "", ""},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);
    Unit enemyMech = map.getUnitOn(0, 0);
    Unit hero = map.getUnitOn(0, 2);

    DefaultUnitAI advisor = new DefaultUnitAI(game, null);
    List<UnitOrder> orders = advisor.findBestUnitOrders();

    Assert.assertEquals("The unit location", hero.getLocation(), orders.get(0).unitLocation);
    Assert.assertEquals("The unit is indirect", new Location2D(0, 2), orders.get(0).moveDestination);
    Assert.assertEquals("The unit attacks the enemy", new Location2D(0, 0), orders.get(0).target);
  }

  @Test
  public void testDirectAntiAirVsInfantryAttackWithMountains() throws Exception {
    COFactory.setScriptManager(new ScriptManager());
    String[][] smallMap = new String[][]{
      new String[]{"", "TANK-P2", "", ""},
      new String[]{"", "", "MNTN", "MNTN"},
      new String[]{"AAIR-P1", "", "MNTN", "MECH-P2"},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);
    Unit enemyMech = map.getUnitOn(3, 2);
    Unit enemyTank = map.getUnitOn(1, 0);
    Unit hero = map.getUnitOn(0, 2);

    DefaultUnitAI advisor = new DefaultUnitAI(game, null);
    List<UnitOrder> orders = advisor.findBestUnitOrders();

    // The mech on the lower right corner is unreachable by the anti air
    // the tank is not a good target for an anti air
    Assert.assertTrue("There are no attack orders", orders.isEmpty());
  }

}
