package com.customwars.client.model.map.path;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.map.TextMapParser;
import com.customwars.client.model.TestData;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
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

public class PathTest {
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
  public void testInfantryMovement() {
    final String[][] simplePlainMap = new String[][]{
      new String[]{"HQTR-P1,INFT-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"HQTR-P2", "", "", "", "", "", "", "", "", "", ""},
    };

    TextMapParser parser = new TextMapParser(simplePlainMap);
    Map map = parser.parseMap();
    Game game = createGame(map);

    // The infantry can move over plains
    // Check how far he can go using the 2 pathfinder methods.
    Unit unit = map.getUnitOn(0, 0);
    PathFinder pathFinder = new PathFinder(map);

    // Path includes the unit location
    List<Location> path = pathFinder.getMovementPath(unit, new Location2D(3, 0));
    Assert.assertFalse(path.isEmpty());
    Assert.assertEquals(4, path.size());
    Assert.assertEquals(new Location2D(3, 0), path.get(path.size() - 1));

    // Path does not include the unit location
    List<Location> path2 = pathFinder.getShortestPath(unit, new Location2D(3, 0));
    Assert.assertFalse(path2.isEmpty());
    Assert.assertEquals(3, path2.size());
    Assert.assertEquals(new Location2D(3, 0), path2.get(path2.size() - 1));
  }

  @Test
  public void testDestinationReacrhableThroughPipeSeam() {
    final String[][] simpleMap = new String[][]{
      new String[]{"TANK-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"HQTR-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "PIPE-P*", "PIPE-P*"},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPS-P*", "", "HQTR-P2"}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    Game game = createGame(map);

    // OK, so the tank wants to go to the HQ in the map.
    // Problem there is no direct route towards the city.
    // It must first destroy the destroyable pipe seam.
    Unit unit = map.getUnitOn(0, 0);
    City HQ = map.getCityOn(10, 6);
    City pipeSeam = map.getCityOn(8, 6);

    PathFinder pathFinder = new PathFinder(map);

    // Find a path to the HQ
    // Move the unit towards the destination (it will take 3 turns)
    List<Location> path = pathFinder.getShortestPath(unit, HQ.getLocation());
    Assert.assertFalse(path.isEmpty());
    map.teleport(unit.getLocation(), path.get(path.size() - 1), unit);

    List<Location> path2 = pathFinder.getShortestPath(unit, HQ.getLocation());
    Assert.assertFalse(path2.isEmpty());
    map.teleport(unit.getLocation(), path2.get(path2.size() - 1), unit);

    List<Location> path3 = pathFinder.getShortestPath(unit, HQ.getLocation());
    Assert.assertFalse(path3.isEmpty());
    map.teleport(unit.getLocation(), path3.get(path3.size() - 1), unit);

    Location expected = new Location2D(7, 6);
    String message = "Tank is before the pipe seam";
    Assert.assertEquals(message, expected, unit.getLocation());
  }

  @Test
  public void testDestinationReachableThroughPipeSeamOnLargeMap() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BIKE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"HQTR-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "PIPE-P*", "PIPE-P*"},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "", ""},
      new String[]{"", "", "", "", "", "PIPE-P*", "PIPE-P*", "", "", "", ""},
      new String[]{"", "", "", "", "", "PIPS-P*", "", "", "", "", "HQTR-P2"}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    Game game = createGame(map);

    // OK, so the bike wants to move to the single HQ in the map.
    // Problem there is no direct route towards the city.
    // It must first destroy the destroyable pipe seam.
    Unit unit = map.getUnitOn(0, 0);
    City HQ = map.getCityOn(10, 15);

    PathFinder pathFinder = new PathFinder(map);
    List<Location> path = pathFinder.getShortestPath(unit, HQ.getLocation());

    Assert.assertFalse(path.isEmpty());
    Assert.assertTrue(pathFinder.canMoveTo(unit, HQ.getLocation()));
  }

  @Test
  public void testDestinationReachableOnlyForInfantry() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BIKE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"HQTR-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "PIPE-P*", "PIPE-P*", "PIPE-P*"},
      new String[]{"", "", "", "", "", "PIPE-P*", "PIPE-P*", "", "", "", ""},
      new String[]{"", "", "", "", "", "MNTN", "", "", "", "", "HQTR-P2"}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    Game game = createGame(map);

    // OK, so the bike wants to move to the single HQ in the map.
    // Problem there is no direct route towards the city.
    // Since a bike cannot move over the mountain
    // It must move as close as possible to the HQ
    // Better to move towards a pipe then blocking a factory!
    Unit unit = map.getUnitOn(0, 0);
    City HQ = map.getCityOn(10, 4);

    PathFinder pathFinder = new PathFinder(map);
    List<Location> path = pathFinder.getShortestPath(unit, HQ.getLocation());

    Assert.assertFalse(path.isEmpty());
  }
}
