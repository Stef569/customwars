package com.customwars.client.model.ai;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.map.TextMapParser;
import com.customwars.client.model.TestData;
import com.customwars.client.model.ai.unit.DefaultUnitAdvisor;
import com.customwars.client.model.ai.unit.UnitOrder;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location2D;
import com.customwars.client.model.map.Map;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SimpleUnitAdvisorTest {
  private static final String[][] testMapSource = new String[][]{
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "HQTR-P2", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "BASE-P*", "", "INFT-P1", "", "", "", "", "", "", ""},
    new String[]{"", "CITY-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "TAPT-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"CITY-P*", "", "", "", "", "", "", "", "HQTR-P1", "", ""},
    new String[]{"MECH-P1,BASE-P*", "CITY-P*", "", "", "", "", "", "", "", "", "RCKT-P1"}
  };

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

    Game game = new Game(map, players, gameRules);
    game.startGame();
    return game;
  }

  private Map createMap(String[][] testMapSource) {
    TextMapParser parser = new TextMapParser(testMapSource);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    return map;
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testMechUnitAdvisor() throws Exception {
    Map map = createMap(testMapSource);
    Game game = createGame(map);
    Unit mech = map.getUnitOn(0, 8);

    // Mech is surrounded by cities, capture the factory aka base
    DefaultUnitAdvisor advisor = new DefaultUnitAdvisor(game, mech);
    UnitOrder order = advisor.createBestOrder();

    Assert.assertEquals(new Location2D(0, 8), order.moveDestination);
  }

  @Test
  public void testRocketsUnitAdvisor() throws Exception {
    Map map = createMap(testMapSource);
    Game game = createGame(map);
    Unit rockets = map.getUnitOn(10, 8);

    // Rockets is just standing there, move to HQ
    DefaultUnitAdvisor advisor = new DefaultUnitAdvisor(game, rockets);
    UnitOrder order = advisor.createBestOrder();

    Assert.assertEquals(new Location2D(8, 8), order.moveDestination);
  }

  @Test
  public void testUnitMovementTowardsHQWithSeaInBetween() {
    final String[][] testMapSource = new String[][]{
      new String[]{"HQTR-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"MECH-P1", "MECH-P1", "MECH-P1", "MECH-P1", "", "", "", "", "", "", ""},
      new String[]{"SEAS", "SEAS", "SEAS", "", "", "", "", "", "", "", ""},
      new String[]{"SEAS", "SEAS", "SEAS", "", "", "", "", "", "", "", ""},
      new String[]{"SEAS", "SEAS", "SEAS", "", "", "", "", "", "", "", ""},
      new String[]{"SEAS", "SEAS", "SEAS", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"HQTR-P2", "", "", "", "", "", "", "", "", "", ""}
    };

    TextMapParser parser = new TextMapParser(testMapSource);
    Map map = parser.parseMap();
    Game game = createGame(map);
    Unit mech4 = map.getUnitOn(3, 1);

    // Ask AI what to do now
    DefaultUnitAdvisor advisor = new DefaultUnitAdvisor(game, mech4);
    UnitOrder order = advisor.createBestOrder();

    Assert.assertEquals(new Location2D(3, 1), order.unitLocation);
    Assert.assertEquals(new Location2D(3, 3), order.moveDestination);

    // todo All the mechs can move, if the mech on the right starts to move
  }
}
