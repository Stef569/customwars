package com.customwars.client.io.loading;

import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import junit.framework.Assert;
import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import slick.HardCodedGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a Hardcoded Game, write the game to a file
 * and read it back in again.
 *
 * The hardcoded game and the game read from the file should be equal.
 */
public class BinCW2GameParserTest {
  private Game hardCodedGame;
  private BinaryCW2GameParser gameParser;
  private static final String SAVE_PATH = "resources/testData/test.save";

  @BeforeClass
  public static void beforeAllTests() {
    BasicConfigurator.configure();
    TestData.storeTestData();
  }

  @AfterClass
  public static void afterAllTests() {
    File file = new File(SAVE_PATH);
    if (file.exists()) file.delete();
  }

  @Before
  public void beforeEachTest() {
    hardCodedGame = HardCodedGame.getGame();
    hardCodedGame.startGame();
    hardCodedGame.endTurn();
    hardCodedGame.endTurn();
    hardCodedGame.endTurn();
    hardCodedGame.endTurn();
    hardCodedGame.endTurn();
    hardCodedGame.endTurn();

    gameParser = new BinaryCW2GameParser();
  }

  @Test
  public void testWritingAndReadingGame() throws IOException {
    gameParser.writeGame(hardCodedGame, new FileOutputStream(SAVE_PATH));
    Game gameFromFile = gameParser.readGame(new FileInputStream(SAVE_PATH));

    Assert.assertEquals(hardCodedGame.getActivePlayers(), gameFromFile.getActivePlayers());
    Assert.assertEquals(hardCodedGame.getTurn(), gameFromFile.getTurn());
    Assert.assertEquals(hardCodedGame.getDay(), gameFromFile.getDay());
    Assert.assertEquals(hardCodedGame.getDayLimit(), gameFromFile.getDayLimit());
    Assert.assertEquals(hardCodedGame.getMap().getMapName(), gameFromFile.getMap().getMapName());
    Assert.assertEquals(hardCodedGame.getActivePlayer(), gameFromFile.getActivePlayer());
    Assert.assertTrue(hardCodedGame.isStarted());
    Assert.assertEquals(hardCodedGame.isStarted(), gameFromFile.isStarted());

    Map<Tile> hardCodedMap = hardCodedGame.getMap();
    Map<Tile> mapFromFile = gameFromFile.getMap();
    mapFromFile.validate();
    List<Player> players = new ArrayList<Player>(mapFromFile.getUniquePlayers());
    for (Player player : hardCodedMap.getUniquePlayers()) {
      Player playerFromFile = players.get(players.indexOf(player));

      Assert.assertEquals(player.getName(), playerFromFile.getName());
      Assert.assertEquals(player.getColor(), playerFromFile.getColor());
      if (player.getHq() != null) {
        Assert.assertEquals(player.getHq().getOwner(), playerFromFile.getHq().getOwner());
      }
      Assert.assertEquals(player.getArmyCount(), playerFromFile.getArmyCount());
      Assert.assertEquals(player.getCityCount(), playerFromFile.getCityCount());
      Assert.assertEquals(player.getState(), playerFromFile.getState());
    }
    Assert.assertEquals(hardCodedMap.countTiles(), mapFromFile.countTiles());
  }
}
