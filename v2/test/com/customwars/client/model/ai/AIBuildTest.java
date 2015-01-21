package com.customwars.client.model.ai;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.map.TextMapParser;
import com.customwars.client.model.TestData;
import com.customwars.client.model.ai.build.BuildPriority;
import com.customwars.client.model.ai.build.BuildStrategy;
import com.customwars.client.model.ai.build.DefaultBuildAdvisor;
import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Map;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AIBuildTest {

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  public Game createGame(Map map) {
    Player redPlayer = new Player(0, Color.RED, "Stef", 6000, 0, true, new BasicCO("penny"));
    Player bluePlayer = new Player(1, Color.BLUE, "JSR", 8000, 1, false, new BasicCO("penny"));
    List<Player> players = Arrays.asList(redPlayer, bluePlayer);

    GameRules gameRules = new GameRules();
    gameRules.setDayLimit(Turn.UNLIMITED);
    gameRules.setCityFunds(1000);
    gameRules.setFogOfWar(false);

    Game game = new Game(map, players, gameRules);
    game.startGame();
    return game;
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testFirstTurnEmptyCitiesInRange() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "CITY-P*", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"CITY-P*", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "HQTR-P2", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "MECH-P2", "", ""}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    Game game = createGame(map);

    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> buildTop10 = getUnitsToBuild(priorities, 10);
    Assert.assertTrue(buildTop10.contains("BIKES"));
    Assert.assertTrue(buildTop10.contains("INFANTRY"));

    City factory1 = map.getCityOn(0, 0);
    List<Fuz.UNIT_TYPE> unitTypes1 = buildStrategy.getCityBuildHints(factory1);
    Assert.assertTrue(unitTypes1.contains(Fuz.UNIT_TYPE.CAPTURE));

    City factory2 = map.getCityOn(0, 1);
    List<Fuz.UNIT_TYPE> unitTypes2 = buildStrategy.getCityBuildHints(factory2);
    Assert.assertTrue(unitTypes2.contains(Fuz.UNIT_TYPE.CAPTURE));
  }

  @Test
  public void testNoCitiesToCaptureMustBuildOffensiveUnits() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "CITY-P1", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "", "", "", "", "MECH-P2", "", "", "", ""},
      new String[]{"MECH-P1", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"APRT-P1", "", "", "", "", "", "TANK-P2", "", "", "", ""},
      new String[]{"CITY-P1", "", "INFT-P2", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "HQTR-P2", "", ""},
      new String[]{"", "", "MECH-P2", "", "", "", "", "", "MECH-P2", "", ""}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    Game game = createGame(map);

    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> buildTop10 = getUnitsToBuild(priorities, 10);
    Assert.assertTrue(buildTop10.contains("ANTI_AIR"));
    Assert.assertTrue(buildTop10.contains("HEAVY_TANK"));
    Assert.assertTrue(buildTop10.contains("MEDIUM_TANK"));
    Assert.assertTrue(buildTop10.contains("BOMBER"));
    Assert.assertTrue(buildTop10.contains("ROCKETS"));
  }

  @Test
  public void testManyEnemyAirplanesMustBuildJetOrAntiAir() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "BMBR-P1", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "", "", "", "", "BMBR-P2", "", "", "", ""},
      new String[]{"MECH-P1", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"APRT-P1", "", "", "", "", "", "FGTR-P2", "", "", "", ""},
      new String[]{"CITY-P1", "", "FGTR-P2", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "BMBR-P2", "", ""},
      new String[]{"", "", "FGTR-P2", "", "", "", "", "", "FGTR-P2", "", ""}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    Game game = createGame(map);

    // There are many air units
    // We don't have any unit yet!
    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);

    // What shall we do?
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> buildTop10 = getUnitsToBuild(priorities, 10);
    Assert.assertTrue(buildTop10.contains("ANTI_AIR"));
    Assert.assertTrue(buildTop10.contains("JET"));
    Assert.assertTrue(buildTop10.contains("MISSILES"));
    String message = "Although the cruiser is very good against airplanes, there is no water/port, so don't give advice to build it.";
    Assert.assertFalse(message, buildTop10.contains("CRUISER"));
  }

  @Test
  public void testManyEnemyTanksNearbyMustBuildMechsAndAntiTank() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "TANK-P2", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "TANK-P2", "", "", "", "TANK-P2", "", "", "", ""},
      new String[]{"MECH-P1", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "TANK-P2", "", "", "", ""},
      new String[]{"CITY-P1", "", "TANK-P2", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "TANK-P2", "", ""},
      new String[]{"", "", "TANK-P2", "", "", "", "", "", "TANK-P2", "", ""}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    Game game = createGame(map);

    // There are many tank units really close!
    // We don't have any unit yet!
    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);

    // What shall we do?
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> buildTop10 = getUnitsToBuild(priorities, 10);
    Assert.assertTrue(buildTop10.contains("HEAVY_TANK"));
    Assert.assertTrue(buildTop10.contains("MEDIUM_TANK"));
    Assert.assertTrue(buildTop10.contains("ROCKETS"));
    Assert.assertTrue(buildTop10.contains("ARTILLERY"));
    Assert.assertTrue(buildTop10.contains("MECH"));
    Assert.assertTrue(buildTop10.contains("ANTI_TANK"));

    String antiAirMessage = "Anti Air is too weak against tanks";
    Assert.assertFalse(antiAirMessage, buildTop10.contains("ANTI_AIR"));

    String infMessage = "Infantry is too weak against tanks";
    Assert.assertFalse(infMessage, buildTop10.contains("INFANTRY"));

    String cruiserMessage = "There is no water/port, so don't give advice to build a cruiser.";
    Assert.assertFalse(cruiserMessage, buildTop10.contains("CRUISER"));

    String jetMessage = "No Jet can be build, there is not airport";
    Assert.assertFalse(jetMessage, buildTop10.contains("JET"));

    String bomberMessage = "No Bomber can be build, there is not airport";
    Assert.assertFalse(bomberMessage, buildTop10.contains("BOMBER"));
  }

  @Test
  public void testExploringInFOW() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"MECH-P1", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"CITY-P1", "", "", "", "", "", "", "", "", "INFT-P2", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "BIKE-P2", ""},
      new String[]{"", "", "", "", "", "", "", "", "INFT-P2", "", "HQTR-P2"}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    Game game = createGame(map);
    // We can't see anything
    map.setFogOfWarOn(true);

    // We need to explore the map!
    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> buildTop3 = getUnitsToBuild(priorities, 3);
    Assert.assertTrue(buildTop3.contains("RECON"));
    Assert.assertTrue(buildTop3.contains("INFANTRY"));
    Assert.assertTrue(buildTop3.contains("BIKES"));

    Assert.assertFalse(buildTop3.contains("ROCKETS"));
    Assert.assertFalse(buildTop3.contains("ARTILLERY"));
    Assert.assertFalse(buildTop3.contains("MECH"));
    Assert.assertFalse(buildTop3.contains("ANTI_TANK"));
    Assert.assertFalse(buildTop3.contains("LIGHT_TANK"));
  }

  @Test
  public void testBuildUnitAlreadyManyAntiAirInTheMap() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "", "", "", "", "", "AAIR-P2", "AAIR-P2", "", ""},
      new String[]{"MECH-P1", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "AAIR-P2", "", "", "", ""},
      new String[]{"CITY-P1", "", "", "", "", "", "", "", "", "INFT-P2", ""},
      new String[]{"", "", "", "", "", "AAIR-P2", "", "", "", "BIKE-P2", ""},
      new String[]{"", "", "", "", "", "AAIR-P2", "", "", "INFT-P2", "", "HQTR-P2"}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    Game game = createGame(map);

    // The enemy makes a lot of infantry
    // Better think twice before making another Anti air.
    // We already have 3 of them, we need diversity!
    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> buildTop6 = getUnitsToBuild(priorities, 6);
    Assert.assertTrue(buildTop6.contains("HEAVY_TANK"));
    Assert.assertTrue(buildTop6.contains("LIGHT_TANK"));
    Assert.assertTrue(buildTop6.contains("ROCKETS"));
  }

  private List<String> getUnitsToBuild(List<BuildPriority> priorities, int amount) {
    if (amount > priorities.size()) {
      amount = priorities.size();
    }

    List<String> unitNames = new ArrayList<String>(amount);

    for (int i = 0; i < amount; i++) {
      BuildPriority buildPriority = priorities.get(i);
      unitNames.add(buildPriority.unitName);
    }

    return unitNames;
  }
}
