package com.customwars.client.model.ai.fuzzy;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.map.TextMapParser;
import com.customwars.client.model.TestData;
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
import java.util.Arrays;
import java.util.List;

public class AIBuildDataGeneratorTest {
  private static final String[][] testMapSource = new String[][]{
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "HQTR-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "BASE-P2", "", "INFT-P1", "", "", "", "", "", "", ""},
    new String[]{"", "CITY-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "TAPT-P*", "", "", "", "", "", "", "", "", ""},
    new String[]{"", "", "", "", "", "", "", "", "", "", ""},
    new String[]{"CITY-P*", "", "", "", "", "", "", "", "HQTR-P1", "", ""},
    new String[]{"MECH-P1,BASE-P*", "CITY-P*", "", "", "", "", "", "", "", "", "BASE-P1"}
  };

  private Game game;
  private Map map;
  private Player p1, p2;

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  public void createGame(Map map) {
    this.map = map;
    p1 = new Player(0, Color.RED, "Stef", 22000, 0, true, new BasicCO("penny"));
    p2 = new Player(1, Color.BLUE, "JSR", 30000, 1, false, new BasicCO("penny"));
    List<Player> players = Arrays.asList(p1, p2);
    GameRules gameRules = new GameRules();
    gameRules.setDayLimit(Turn.UNLIMITED);
    gameRules.setCityFunds(1000);

    game = new Game(map, players, gameRules);
    game.startGame();
  }

  private Map createMap() {
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
  public void testGameDataGenerator() throws Exception {
    createGame(createMap());
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    Assert.assertEquals(26, data.visibleLandTiles);
    Assert.assertEquals(Fuz.GAME_PROGRESS.EARLY_GAME, data.gameProgress);
    Assert.assertEquals(Fuz.MAP_SIZE.TINY, data.mapSize);
    Assert.assertEquals(Fuz.MAP_TYPE.PANGEA, data.mapType);
    Assert.assertEquals(Fuz.PLAYER_FINANCE.RICH, data.finance);
  }

  @Test
  public void testConstructionPossibilities() {
    createGame(createMap());
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    Assert.assertTrue(data.constructionPossibilities.contains(Fuz.CONSTRUCTION_POSSIBILITY.LAND));
    Assert.assertFalse(data.constructionPossibilities.contains(Fuz.CONSTRUCTION_POSSIBILITY.SEA));
    Assert.assertFalse(data.constructionPossibilities.contains(Fuz.CONSTRUCTION_POSSIBILITY.AIR));
  }

  @Test
  public void testCityCount() {
    createGame(createMap());
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    Assert.assertEquals(1, data.getCityCount(p1, "FACTORY"));
    Assert.assertEquals(1, data.getCityCount(p1, "HQ"));
    Assert.assertEquals(0, data.getCityCount(p1, "CITY"));
    Assert.assertEquals(0, data.getCityCount(p2, "HQ"));
    Assert.assertEquals(1, data.getCityCount(p2, "FACTORY"));
    Assert.assertEquals(0, data.getCityCount(p2, "CITY"));
  }

  @Test
  public void testDistancesForP1() {
    createGame(createMap());
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    City myFactory = map.getCityOn(10, 8);
    City nearByCity = map.getCityOn(1, 8);
    City enemyHQ = map.getCityOn(1, 2);

    Assert.assertTrue(data.getDistanceBetween(myFactory, nearByCity) == Fuz.DISTANCE.FAR);
    Assert.assertTrue(data.getDistanceBetween(myFactory, enemyHQ) == Fuz.DISTANCE.VERY_FAR);
  }

  @Test
  public void testDistancesForP2() {
    createGame(createMap());
    game.endTurn();
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    City HQ = map.getCityOn(1, 2);
    City myFactory = map.getCityOn(1, 3);
    City nearByCity = map.getCityOn(1, 4);
    City tempAirPort = map.getCityOn(1, 5);

    Assert.assertTrue(data.getDistanceBetween(myFactory, HQ) == Fuz.DISTANCE.VERY_CLOSE);
    Assert.assertTrue(data.getDistanceBetween(myFactory, nearByCity) == Fuz.DISTANCE.VERY_CLOSE);
    Assert.assertTrue(data.getDistanceBetween(myFactory, tempAirPort) == Fuz.DISTANCE.VERY_CLOSE);
  }

  @Test
  public void testDistancesInLargeMap() {
    final String[][] largeMap = new String[][]{
      new String[]{"CITY-P*", "", "", "", "", "", "", "", "", "", "CITY-P*"},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "HQTR-P*", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "BASE-P2", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "CITY-P*", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "TAPT-P*", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"CITY-P*", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"CITY-P*", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", "CITY-P*"},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "MECH-P1", "INFT-P1"},
      new String[]{"CITY-P*", "", "", "", "", "", "", "", "", "BASE-P1", "HQTR-P1"},
    };

    TextMapParser parser = new TextMapParser(largeMap);
    Map map = parser.parseMap();
    createGame(map);
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    City factory = map.getCityOn(9, 29);
    City neutralCityLeftCorner = map.getCityOn(0, 29);
    City middleRight = map.getCityOn(10, 20);
    City neutralCityLeftMiddle = map.getCityOn(0, 7);
    City topLeft = map.getCityOn(0, 0);

    Assert.assertEquals(Fuz.DISTANCE.FAR, data.getDistanceBetween(factory, neutralCityLeftCorner));
    Assert.assertEquals(Fuz.DISTANCE.VERY_FAR, data.getDistanceBetween(factory, middleRight));
    Assert.assertEquals(Fuz.DISTANCE.UNREACHABLE, data.getDistanceBetween(factory, neutralCityLeftMiddle));
    Assert.assertEquals(Fuz.DISTANCE.UNREACHABLE, data.getDistanceBetween(factory, topLeft));
  }

  @Test
  public void testNearestCityDistance() {
    final String[][] largeMap = new String[][]{
      new String[]{"CITY-P*", "", "BASE-P1", "", "", "CITY-P*", "", "", "", "", "CITY-P*"},
      new String[]{"", "", "", "", "HQTR-P1", "", "", "", "", "", ""},
      new String[]{"", "HQTR-P2", "CITY-P*", "", "", "", "", "", "", "", ""},
      new String[]{"", "BASE-P2", "BASE-P1", "", "", "", "", "", "", "", ""},
    };

    TextMapParser parser = new TextMapParser(largeMap);
    Map map = parser.parseMap();
    createGame(map);
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    City myFactory = map.getCityOn(2, 0);
    City myFactory2 = map.getCityOn(2, 3);

    Assert.assertEquals(Fuz.DISTANCE.VERY_CLOSE, data.getDistanceToNearestCity(myFactory));
    Assert.assertEquals(Fuz.DISTANCE.VERY_CLOSE, data.getDistanceToNearestCity(myFactory2));
  }

  @Test
  public void testFindMostExpensiveUnits() {
    createGame(createMap());
    BuildAIDataGenerator generator = new BuildAIDataGenerator(game);
    BuildAIInformation data = generator.generate();

    Assert.assertTrue(data.getMostExpensiveUnits().contains("CARRIER"));
    Assert.assertTrue(data.getMostExpensiveUnits().contains("BOMBER"));
    Assert.assertTrue(data.getMostExpensiveUnits().contains("HEAVY_TANK"));
    Assert.assertTrue(data.getMostExpensiveUnits().contains("JET"));
    Assert.assertTrue(data.getMostExpensiveUnits().contains("SUB"));
  }
}
