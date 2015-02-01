package com.customwars.client.model.ai;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.model.TestData;
import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.routine.CaptureRoutine;
import com.customwars.client.model.ai.unit.routine.RoutineResult;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location2D;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.MapUtil;
import com.customwars.client.tools.TextMapParser;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UnitAICaptureTest {
  private static final String[][] testMapSource = new String[][]{
    new String[]{"", "", "", "", "", "SILO-P*", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "HQTR-P2", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "BASE-P*", "", "INFT-P1", "", "", "", "", "", "", ""},
    new String[]{"", "CITY-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "TAPT-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "MECH-P1", "", ""},
    new String[]{"CITY-P*", "", "", "", "", "", "", "", "HQTR-P1", "", ""},
    new String[]{"MECH-P1,CITY-P*", "BASE-P*", "CITY-P*", "", "", "", "", "", "", "MECH-P1", "", ""}
  };

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  public Game createGame(Map map) {
    Player redPlayer = new Player(0, Color.RED, "Stef", 22000, 0, true, new BasicCO("penny"));
    Player bluePlayer = new Player(1, Color.BLUE, "JSR", 30000, 1, false, new BasicCO("penny"));
    List<Player> players = Arrays.asList(redPlayer, bluePlayer);
    GameRules gameRules = new GameRules();
    gameRules.setDayLimit(Turn.UNLIMITED);
    gameRules.setCityFunds(1000);

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
  public void testCaptureHQPriority() {
    Map map = createMap(testMapSource);
    Game game = createGame(map);

    // A HQ is in reach of the infantry units
    City hq = map.getCityOn(1, 2);
    Unit infantry = map.getUnitOn(3, 3);

    // Ask AI what to do now
    CaptureRoutine capture = new CaptureRoutine(game, infantry);
    RoutineResult result = capture.think();

    // Should be capture hq
    Assert.assertEquals(infantry.getLocation(), result.unitLocation);
    Assert.assertEquals(Fuz.UNIT_ORDER.CAPTURE, result.order);
    Assert.assertEquals(hq.getLocation(), result.moveDestination);
  }

  @Test
  public void testExcludeCitiesWithUnitsOnThem() {
    Map map = createMap(testMapSource);
    Game game = createGame(map);
    Player redPlayer = game.getPlayerByID(0);

      // A HQ is in reach of the bikes units
    City hq = map.getCityOn(1, 2);

    // But a mech unit is already on it!
    MapUtil.addUnitToMap(map, 1, 2, 1, redPlayer);

    // Bikes has x cities in range
    Unit infantry = map.getUnitOn(3, 3);
    City factory = map.getCityOn(1, 3);

    // Ask AI what to do now
    CaptureRoutine capture = new CaptureRoutine(game, infantry);
    RoutineResult result = capture.think();

    // Should be capture Factory
    Assert.assertEquals(infantry.getLocation(), result.unitLocation);
    Assert.assertEquals(Fuz.UNIT_ORDER.CAPTURE, result.order);
    Assert.assertEquals(factory.getLocation(), result.moveDestination);
  }

  @Test
  public void testContinueCapture() {
    Map map = createMap(testMapSource);
    Game game = createGame(map);

    City base = map.getCityOn(0, 8);
    Unit mech = map.getUnitOn(0, 8);

    // Start capturing the city with the mech
    base.capture(mech);

    // Ask Unit AI what he wants to do now
    CaptureRoutine capture = new CaptureRoutine(game, mech);
    RoutineResult result = capture.think();

    // Should be continue capturing on same destination
    Assert.assertEquals(mech.getLocation(), result.unitLocation);
    Assert.assertEquals(Fuz.UNIT_ORDER.CAPTURE, result.order);
    Assert.assertEquals(base.getLocation(), result.moveDestination);
  }

  @Test
  public void testCaptureFactoryPriority() {
    Map map = createMap(testMapSource);
    Game game = createGame(map);

    // A Factory is in reach of the mech unit
    City factory = map.getCityOn(1, 8);

    // mech is on a city
    Unit mech = map.getUnitOn(0, 8);
    City city = map.getCityOn(0, 8);

    // Ask AI what to do now
    CaptureRoutine capture = new CaptureRoutine(game, mech);
    RoutineResult result = capture.think();

    // Should be capture Factory
    Assert.assertEquals(mech.getLocation(), result.unitLocation);
    Assert.assertEquals(Fuz.UNIT_ORDER.CAPTURE, result.order);
    Assert.assertEquals(factory.getLocation(), result.moveDestination);
  }

  @Test
  public void testFireRocketFromSilo() {
    final String[][] smallMap = new String[][]{
      new String[]{"HQTR-P1", "SILO-P*", "", "MECH-P1"},
      new String[]{"", "", "", ""},
      new String[]{"HQTR-P2", "BASE-P*", "", ""},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);

    // A Silo is in reach of the mech unit
    City silo = map.getCityOn(1, 0);

    // mech can launch the silo
    Unit mech = map.getUnitOn(3, 0);

    Assert.assertFalse(silo.canBeCapturedBy(mech));
    Assert.assertTrue(silo.canLaunchRocket(mech));

    // Ask Capture AI what to do now
    CaptureRoutine capture = new CaptureRoutine(game, mech);
    RoutineResult result = capture.think();

    // Should be fire rocket from silo
    Assert.assertEquals("mech is on the silo", new Location2D(1, 0), result.moveDestination);
    Assert.assertEquals("fire missile", Fuz.UNIT_ORDER.FIRE_SILO_ROCKET, result.order);
  }

  @Test
  public void testFireRocketFromSiloExtended() {
    final String[][] smallMap = new String[][]{
      new String[]{"HQTR-P1", "SILO-P*", "", "", "", "", "", "BIKE-P1"},
      new String[]{"", "", "", "", "", "", "", ""},
      new String[]{"HQTR-P2", "BASE-P*", "", "", "", "", "", ""},
    };

    Map map = createMap(smallMap);
    Game game = createGame(map);

    // A Silo is in reach of the bikes unit
    City silo = map.getCityOn(1, 0);

    // mech can launch the silo
    Unit bikes = map.getUnitOn(7, 0);

    Assert.assertFalse(silo.canBeCapturedBy(bikes));
    Assert.assertTrue(silo.canLaunchRocket(bikes));

    // Ask Capture AI what to do now
    CaptureRoutine capture = new CaptureRoutine(game, bikes);

    RoutineResult result = capture.think();
    // Should be fire rocket from silo
    Assert.assertEquals("bike is on the silo", new Location2D(1, 0), result.moveDestination);
    Assert.assertEquals("fire missile", Fuz.UNIT_ORDER.FIRE_SILO_ROCKET, result.order);
  }

}
