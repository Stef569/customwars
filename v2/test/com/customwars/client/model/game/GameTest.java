package com.customwars.client.model.game;

import com.customwars.client.model.TestData;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tools.MapUtil;

import java.awt.Color;
import java.util.Arrays;

public class GameTest {
  private Game game;
  private Map<Tile> map;    // Hardcoded map, see beforeEachTest and buildHardCodedMap

  @Before
  public void beforeEachTest() {
    Terrain plain = TerrainFactory.getTerrain(TestData.PLAIN);
    map = new Map<Tile>(10, 10, 32, true, plain);
  }

  @After
  public void afterEachTest() {
    game = null;
    map = null;
  }

  /**
   * p1 Start budget=800 cities=1
   * p2 Start budget=5000 cities=1
   */
  @Test
  public void testCityBudget() {
    int P1_START_BUDGET = 800;
    int P2_START_BUDGET = 5000;
    int CITY_FUNDS = 1200;

    buildHardCodedMap(2);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", P1_START_BUDGET, 0, false);
    Player p2 = new Player(1, Color.BLACK, false, null, "JSR", P2_START_BUDGET, 5, false);
    GameConfig gc = new GameConfig();
    gc.setCityfunds(CITY_FUNDS);

    // p1 is starting the game
    startGame(p1, gc, p1, p2);

    // Game is now started
    Assert.assertTrue(game.isStarted());
    Assert.assertEquals(0, game.getTurn());
    Assert.assertEquals(p1, game.getActivePlayer());

    // p1 has more cash because the one city it had
    // added 1200 funds to p1's budget.
    Assert.assertEquals(p1.getBudget(), P1_START_BUDGET + CITY_FUNDS);

    game.endTurn();

    // 1 turn further
    // p2 is now the activeplayer
    Assert.assertEquals(1, game.getTurn());
    Assert.assertEquals(p2, game.getActivePlayer());

    // p2 has more cash because the one city it had
    // added 1200 funds to p2's budget.
    Assert.assertEquals(p2.getBudget(), P2_START_BUDGET + CITY_FUNDS);
  }

  /**
   * p2 cannot end his turn because the active Player is p1
   */
  @Test(expected = NotYourTurnException.class)
  public void testEndTurnWithNonActivePlayer() {
    buildHardCodedMap(2);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", 0, 0, false);
    Player p2 = new Player(1, Color.BLACK, false, null, "JSR", 0, 1, false);
    startGame(new GameConfig(), p1, p2);

    game.endTurn(p2);
  }

  /**
   * Daylimit is 2, so each player can end his turn, but then the game has ended.
   */
  @Test(expected = IllegalStateException.class)
  public void testEndTurnOnLastTurn() {
    buildHardCodedMap(4);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", 0, 0, false);
    Player p2 = new Player(1, Color.RED, false, null, "JSR", 0, 0, false);
    Player p3 = new Player(2, Color.RED, false, null, "Ben", 0, 0, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Joop", 0, 5, false);
    GameConfig gc = new GameConfig();
    gc.setDayLimit(2);
    startGame(gc, p1, p1, p2, p3, p4);

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
    int END_TURN_ITERATIONS = 1500;

    buildHardCodedMap(4);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", 0, 0, false);
    Player p2 = new Player(1, Color.GREEN, false, null, "Jan", 0, 0, false);
    Player p3 = new Player(2, Color.BLUE, false, null, "JSR", 0, 0, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Joop", 0, 1, false);
    startGame(new GameConfig(), p1, p2, p3, p4);

    for (int counter = 0; counter < END_TURN_ITERATIONS; counter++) {
      game.endTurn();
    }
  }

  /**
   * Try to Start a game with duplicate players
   */
  @Test(expected = IllegalStateException.class)
  public void testDuplicatePlayers() {
    buildHardCodedMap(5);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", 0, 0, false);
    Player p2 = new Player(1, Color.RED, false, null, "Jan", 0, 0, false);
    Player p3 = new Player(2, Color.RED, false, null, "Jsr", 0, 0, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Joop", 0, 5, false);
    Player p5 = new Player(4, Color.YELLOW, false, null, "Kembo", 0, 4, false);
    startGame(new GameConfig(), p3, p3, p1, p2, p4, p5);
  }

  @Test
  public void testGameOverByLastPlayerRemaining() {
    buildHardCodedMap(4);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", 0, 0, false);
    Player p2 = new Player(1, Color.BLUE, false, null, "Jan", 0, 0, false);
    Player p3 = new Player(2, Color.YELLOW, false, null, "JSR", 0, 0, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Kembo", 0, 0, false);
    startGame(new GameConfig(), p1, p2, p3, p4);

    // Give p2 a unit
    Unit inf2 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, 0, 0, inf2, p2);

    // Give p3 a unit
    Unit inf3 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, 1, 0, inf3, p3);

    // Give p4 a unit
    Unit inf4 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, 2, 0, inf4, p4);

    // p1 slays them all:
    inf2.destroy();
    inf3.destroy();
    inf4.destroy();

    // Game is now over
    Assert.assertTrue(game.isGameOver());
  }

  /**
   * The game ends when only 1 team remains(all other team players are destroyed)
   * There are 2 teams: 1(Stef, JSR) and 2(Ben, Joop)
   */
  @Test
  public void testGameOverByAlliedVictory() {
    buildHardCodedMap(4);
    Player p1 = new Player(0, Color.RED, false, null, "Stef", Integer.MAX_VALUE, 1, false);
    Player p2 = new Player(1, Color.BLUE, false, null, "JSR", 8500, 1, false);
    Player p3 = new Player(2, Color.GREEN, false, null, "Ben", 500, 2, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Joop", 1000, 2, false);
    startGame(new GameConfig(), p1, p2, p3, p4);

    // p1 builds up his army
    Unit inf1 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, 0, 0, inf1, p1);

    // p2 builds up his army
    Unit inf2 = UnitFactory.getUnit(TestData.INF);
    MapUtil.addUnitToMap(map, 1, 0, inf2, p2);

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
    buildHardCodedMap(5);
    Player mapPlayer1 = new Player(0);
    Player mapPlayer2 = new Player(1);
    Player mapPlayer3 = new Player(2);
    Player mapPlayer4 = new Player(3);
    Player mapPlayer5 = new Player(4);

    // Add some units to each player
    MapUtil.addUnitToMap(map, 0, 0, TestData.INF, mapPlayer1);
    MapUtil.addUnitToMap(map, 1, 0, TestData.INF, mapPlayer2);
    MapUtil.addUnitToMap(map, 2, 0, TestData.INF, mapPlayer2);
    MapUtil.addUnitToMap(map, 3, 0, TestData.INF, mapPlayer2);
    MapUtil.addUnitToMap(map, 4, 0, TestData.INF, mapPlayer2);
    MapUtil.addUnitToMap(map, 5, 0, TestData.INF, mapPlayer2);
    MapUtil.addUnitToMap(map, 6, 0, TestData.INF, mapPlayer3);
    MapUtil.addUnitToMap(map, 7, 0, TestData.INF, mapPlayer3);
    MapUtil.addUnitToMap(map, 8, 0, TestData.INF, mapPlayer4);
    MapUtil.addUnitToMap(map, 9, 0, TestData.INF, mapPlayer4);
    MapUtil.addUnitToMap(map, 0, 1, TestData.INF, mapPlayer5);

    Player p1 = new Player(0, Color.RED, false, null, "Stef", 0, 0, false);
    Player p2 = new Player(1, Color.BLUE, false, null, "Jan", 0, 0, false);
    Player p3 = new Player(2, Color.WHITE, false, null, "Jsr", 0, 0, false);
    Player p4 = new Player(3, Color.BLACK, false, null, "Joop", 0, 5, false);
    Player p5 = new Player(4, Color.YELLOW, false, null, "Kembo", 0, 3, false);
    startGame(new GameConfig(), p1, p2, p3, p4, p5);

    // Only player1 has active units
    checkState(p1.getArmy(), GameObjectState.ACTIVE);
    checkState(p2.getArmy(), GameObjectState.IDLE);
    checkState(p3.getArmy(), GameObjectState.IDLE);
    checkState(p4.getArmy(), GameObjectState.IDLE);
    checkState(p5.getArmy(), GameObjectState.IDLE);
  }

  // Util Functions
  private void checkState(Iterable<Unit> units, GameObjectState state) {
    for (Unit unit : units) {
      Assert.assertEquals(unit.getState(), state);
    }
  }

  /**
   * Build a map that supports the playerCount
   * Supports means that for each player in the map there should be a game player.
   * When starting a game the game players count is compared to the player count in the map
   * so add a base for each player to the map.
   */
  private void buildHardCodedMap(int playerCount) {
    int col = 0;
    for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
      Player mapPlayer = new Player(playerIndex);
      MapUtil.addCityToMap(map, col++, 0, TestData.BASE, mapPlayer);
    }
  }

  /**
   * Start a game with the first player as game starter
   */
  private void startGame(GameConfig gc, Player... players) {
    startGame(players[0], gc, players);
  }

  private void startGame(Player gameStarter, GameConfig gc, Player... players) {
    game = new Game(map, Arrays.asList(players), gc);
    game.startGame(gameStarter);
  }
}
