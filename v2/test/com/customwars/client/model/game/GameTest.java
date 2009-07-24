package com.customwars.client.model.game;

import com.customwars.client.model.TestData;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class GameTest {
  private Game game;
  private Map<Tile> map;
  private Player p1, p2, p3, p4;

  @Before
  public void beforeEachTest() {
    game = null;
    Terrain plain = TerrainFactory.getTerrain(TestData.PLAIN);
    map = new Map<Tile>(10, 10, 32, 4, false, plain);
    map.setFogOfWarOn(true);

    p1 = new Player(0, Color.RED, false, null, "Stef", Integer.MAX_VALUE, 0, false);
    p2 = new Player(1, Color.BLUE, false, null, "JSR", 8500, 0, false);
    p3 = new Player(2, Color.GREEN, false, null, "Ben", 500, 1, false);
    p4 = new Player(3, Color.BLACK, false, null, "Joop", 1000, 5, false);
  }

  private void startGame(Player gameStarter) {
    startGame(new GameConfig(), gameStarter);
  }

  private void startGame(GameConfig gc, Player gameStarter) {
    List<Player> players = Arrays.asList(p1, p2, p3, p4);
    startGame(gc, gameStarter, players);
  }

  private void startGame(GameConfig gc, Player gameStarter, List<Player> players) {
    if (gc.getTurnLimit() == 0)
      gc.setTurnLimit(Turn.UNLIMITED);
    if (gc.getDayLimit() == 0)
      gc.setDayLimit(Turn.UNLIMITED);

    game = new Game(map, players, gc);
    game.startGame(gameStarter);
  }

  @Test
  public void testCityBudget() {
    int P4_START_BUDGET = 5000;
    int P1_START_BUDGET = 800;
    int CITY_FUNDS = 1200;

    p1 = new Player(0, Color.RED, false, null, "Stef", P1_START_BUDGET, 0, false);
    p4 = new Player(3, Color.BLACK, false, null, "Joop", P4_START_BUDGET, 5, false);

    // Give player 4 a City
    City p4City = CityFactory.getCity(TestData.BASE);
    Tile tile = map.getTile(0, 0);
    tile.setTerrain(p4City);
    p4City.setLocation(tile);
    p4City.setOwner(p4);

    // Give player 1 a City
    City p1City = CityFactory.getCity(TestData.BASE);
    tile = map.getTile(1, 0);
    tile.setTerrain(p1City);
    p1City.setLocation(tile);
    p1City.setOwner(p1);

    GameConfig gc = new GameConfig();
    gc.setTurnLimit(500);
    gc.setCityfunds(CITY_FUNDS);
    startGame(gc, p4);

    // Game is now started
    Assert.assertTrue(game.isStarted());
    Assert.assertEquals(0, game.getTurn());
    Assert.assertEquals(p4, game.getActivePlayer());

    // p4 now has more cash because the one building it had
    // added 1200 funds to it's budget.
    Assert.assertEquals(p4.getBudget(), P4_START_BUDGET + CITY_FUNDS);

    game.endTurn();

    // 1 turn further
    // p1 is now the activeplayer
    Assert.assertEquals(1, game.getTurn());
    Assert.assertEquals(p1, game.getActivePlayer());

    // p1 now has more cash because the one building it had
    // added 1200 funds to p1 budget.
    Assert.assertEquals(p1.getBudget(), P1_START_BUDGET + CITY_FUNDS);
  }

  /**
   * p2 cannot end his turn because the active Player is p1
   */
  @Test(expected = NotYourTurnException.class)
  public void testEndTurnWithNonActivePlayer() {
    startGame(new GameConfig(), p1);
    game.endTurn(p2);
  }

  /**
   * Daylimit is 2, so each player can end his turn, but then the game has ended.
   */
  @Test(expected = IllegalStateException.class)
  public void testEndTurnOnLastTurn() {
    GameConfig gc = new GameConfig();
    gc.setDayLimit(2);
    startGame(gc, p1);

    game.endTurn();  // p1
    game.endTurn();  // p2
    game.endTurn();  // p3
    game.endTurn();  // p4

    // Game has ended:
    Assert.assertEquals(true, game.isGameOver());

    // Attempts to end the turn after the game has ended will result in an IllegalStateException
    game.endTurn();
  }

  /**
   * Should not throw any exceptions
   * in other words the happy path
   */
  @Test
  public void testEndTurn() {
    startGame(new GameConfig(), p1);

    int counter = 0;
    while (counter < 1500) {
      game.endTurn();
      counter++;
    }
  }

  @Test
  public void testGameOverByLastPlayerRemaining() {
    startGame(p1);

    // Give p2 a unit
    Unit inf2 = UnitFactory.getUnit(TestData.INF);
    map.getRandomTile().add(inf2);
    p3.addUnit(inf2);

    // Give p3 a unit
    Unit inf3 = UnitFactory.getUnit(TestData.INF);
    map.getRandomTile().add(inf3);
    p3.addUnit(inf3);

    // Give p4 a unit
    Unit inf4 = UnitFactory.getUnit(TestData.INF);
    map.getRandomTile().add(inf4);
    p4.addUnit(inf4);

    // p1 slays them all:
    inf2.destroy();
    inf3.destroy();
    inf4.destroy();

    // Game is now over
    Assert.assertTrue(game.isGameOver());
  }

  /**
   * Try to Start a game with duplicate players
   */
  @Test(expected = IllegalStateException.class)
  public void testDuplicatePlayers() {
    startGame(new GameConfig(), p1, Arrays.asList(p1, p2, p3, p3));
  }

  /**
   * The game ends when only 1 team remains(all other team players are destroyed)
   * There are 2 teams: 1(Stef, JSR) and 2(Ben, Joop)
   */
  @Test
  public void testGameOverByAlliedVictory() {
    Player p1 = new Player(0, Color.RED, false, null, "Stef", Integer.MAX_VALUE, 1, false);
    Player p2 = new Player(1, Color.BLUE, false, null, "JSR", 8500, 1, false);
    Player p3 = new Player(2, Color.GREEN, false, null, "Ben", 500, 2, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Joop", 1000, 2, false);
    startGame(new GameConfig(), p1, Arrays.asList(p1, p2, p3, p4));

    // p1 builds up his army
    Unit inf1 = UnitFactory.getUnit(TestData.INF);
    map.getRandomTile().add(inf1);
    p1.addUnit(inf1);

    // p2 builds up his army
    Unit inf2 = UnitFactory.getUnit(TestData.INF);
    map.getRandomTile().add(inf2);
    p2.addUnit(inf2);

    // team 2 comes along and kills all units of team 1
    inf1.destroy();
    inf2.destroy();

    Assert.assertTrue(p1.isDestroyed());
    Assert.assertTrue(p2.isDestroyed());
    Assert.assertTrue(game.isGameOver());
  }


  /**
   * When the game has started the starting player(p1) contains active units
   * all the other players contain idle units
   */
  @Test
  public void testActiveUnitsAfterGameStart() {
    // Add some units to each player
    Unit inf1 = UnitFactory.getUnit(TestData.INF);
    map.getTile(0, 0).add(inf1);
    inf1.setOwner(p1);

    Unit inf2 = UnitFactory.getUnit(TestData.INF);
    map.getTile(0, 1).add(inf2);
    inf2.setOwner(p2);

    Unit inf3 = UnitFactory.getUnit(TestData.INF);
    map.getTile(0, 2).add(inf3);
    inf3.setOwner(p3);

    Unit inf4 = UnitFactory.getUnit(TestData.INF);
    map.getTile(0, 3).add(inf4);
    inf4.setOwner(p3);

    startGame(p1);

    // Only player1 has active units
    for (Unit unit : p1.getArmy()) {
      Assert.assertEquals(unit.getState(), GameObjectState.ACTIVE);
    }

    for (Unit unit : p2.getArmy()) {
      Assert.assertEquals(unit.getState(), GameObjectState.IDLE);
    }

    for (Unit unit : p3.getArmy()) {
      Assert.assertEquals(unit.getState(), GameObjectState.IDLE);
    }
  }
}