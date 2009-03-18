package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.testdata.TestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tools.MapUtil;

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
    map = new Map<Tile>(10, 10, 32, 4);
    map.setFogOfWarOn(true);
    MapUtil.fillWithTiles(map, TerrainFactory.getTerrain(TestData.PLAIN));

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
      gc.setTurnLimit(99);

    game = new Game(map, players, gc);
    game.init();
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

    // Game is now started (active)
    Assert.assertEquals(GameObjectState.ACTIVE, game.getState());
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

  @Test(expected = NotYourTurnException.class)
  /**
   * p2 cannot end his turn because the active Player is p1
   */
  public void testEndTurnWithNonActivePlayer() {
    startGame(new GameConfig(), p1);
    game.endTurn(p2);
  }

  @Test(expected = IllegalStateException.class)
  /**
   * Turnlimit is 1, so player 1 can end his turn, but then the game has ended.
   */
  public void testEndTurnOnLastTurn() {
    GameConfig gc = new GameConfig();
    gc.setTurnLimit(1);
    startGame(gc, p1);

    game.endTurn();

    // Game has ended:
    Assert.assertEquals(true, game.isDestroyed());

    // Attempts to end the turn after the game has ended will result in an IllegalStateException
    game.endTurn();
  }

  @Test
  /**
   *
   * Should not throw any exceptions
   * in other words the happy path
   */
  public void testEndTurn() {
    startGame(new GameConfig(), p1);

    game.endTurn();
    game.endTurn();
    game.endTurn();
    game.endTurn();
    game.endTurn();
    game.endTurn();
  }

  @Test
  public void testGameOver() {
    startGame(p1);

    // Give p3 a unit
    Unit inf = UnitFactory.getUnit(TestData.INF);
    game.getMap().getRandomTile().add(inf);
    p3.addUnit(inf);

    // Kill it
    inf.destroy();

    // Game is now over
    Assert.assertEquals(true, game.isDestroyed());
  }

  @Test
  public void testGameWithNeutralPlayer() {
    Player p1 = new Player(1, Color.YELLOW, false, null, "Stef", Integer.MAX_VALUE, 0, false);
    Player p2 = new Player(2, Color.GREEN, false, null, "JSR", 8500, 0, false);
    Player p3 = new Player(3, Color.BLACK, false, null, "Ben", 50, 0, false);
    Player neutral = new Player(Color.GRAY, null, -1);

    // Try to Start a game with neutral
    startGame(new GameConfig(), neutral, Arrays.asList(p1, p2, p3, neutral));

    // Neutral is skipped, next active player is p1
    Assert.assertEquals(p1, game.getActivePlayer());

    // next in the list is p2(doh)
    game.endTurn();
    Assert.assertEquals(p2, game.getActivePlayer());

    // next in the list is p3(doh)
    game.endTurn();
    Assert.assertEquals(p3, game.getActivePlayer());

    // Neutral is skipped again
    game.endTurn();
    Assert.assertEquals(p1, game.getActivePlayer());
  }
}
