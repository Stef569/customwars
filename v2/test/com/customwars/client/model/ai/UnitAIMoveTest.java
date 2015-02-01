package com.customwars.client.model.ai;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.map.TextMapParser;
import com.customwars.client.model.TestData;
import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.routine.CaptureRoutine;
import com.customwars.client.model.ai.unit.routine.MoveRoutine;
import com.customwars.client.model.ai.unit.routine.RoutineResult;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.MapUtil;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class UnitAIMoveTest {
  private static final String[][] testMapSource = new String[][]{
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "HQTR-P2", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "BASE-P*", "", "INFT-P1", "", "", "", "", "", "", ""},
    new String[]{"", "CITY-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "TAPT-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "MECH-P1", "", ""},
    new String[]{"CITY-P*", "", "", "", "", "", "", "", "HQTR-P1", "", ""},
    new String[]{"MECH-P1,CITY-P*", "BASE-P*", "CITY-P*", "", "", "", "", "", "", "MECH-P1", ""}
  };

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  @Before
  public void beforeEachTest() {
    createGame(createMap(testMapSource));
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

  private Map createMap(String[][] mapSource) {
    TextMapParser parser = new TextMapParser(mapSource);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    return map;
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testMoveTowardsHQ() {
    Map map = createMap(testMapSource);
    Game game = createGame(map);

    // A free city is in reach of the infantry unit
    City city = map.getCityOn(1, 4);
    Unit infantry = map.getUnitOn(3, 3);

    // Ask AI what to do now
    MoveRoutine move = new MoveRoutine(game, infantry);
    RoutineResult result = move.think();

    // The first city in the list is a city @ 1,4
    Assert.assertEquals(infantry.getLocation(), result.unitLocation);
    Assert.assertEquals(Fuz.UNIT_ORDER.MOVE, result.order);
    Assert.assertEquals(city.getLocation(), result.moveDestination);
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

}
