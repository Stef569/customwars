package com.customwars.client.io.loading;

import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.TestData;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import junit.framework.Assert;
import org.junit.After;
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

public class BinCW2MapParserTest {
  private static final String TEST_DIR_NAME = "test";
  private static final String MAP_NAME = "test.map";
  private static File testDir;
  private static File mapFile;

  private BinaryCW2MapParser mapParser;
  private Map<Tile> hardCodedMap;

  @BeforeClass
  public static void beforeEachClass() {
    TestData.storeTestData();
    testDir = new File(TEST_DIR_NAME);
    testDir.mkdir();
    mapFile = new File(TEST_DIR_NAME, MAP_NAME);
  }

  @Before
  public void beforeEachTest() {
    hardCodedMap = HardCodedGame.getMap();
    mapParser = new BinaryCW2MapParser();
  }

  @After
  public void afterEachTest() {
    hardCodedMap = null;
    mapParser = null;
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
    deleteTestDir();
  }

  private static void deleteTestDir() {
    // can't delete non empty dirs, first remove the content then the test dir.
    if (mapFile.exists()) {
      mapFile.delete();
    }
    testDir.delete();
  }

  /**
   * Write a hardcoded map to the test dir and then read it back in.
   * the hardcoded map and the loadedMap should have equal values.
   */
  @Test
  public void testWriteReadMap() throws IOException {
    // Write map to the test dir
    mapParser.writeMap(hardCodedMap, new FileOutputStream(mapFile));

    // Read the map back in
    Map<Tile> loadedMap = mapParser.readMap(new FileInputStream(mapFile));

    // The hardcoded map should be equal to the map loaded from the file
    Assert.assertEquals(hardCodedMap.getTileSize(), loadedMap.getTileSize());
    Assert.assertEquals(hardCodedMap.getNumPlayers(), loadedMap.getNumPlayers());
    Assert.assertEquals(hardCodedMap.countTiles(), loadedMap.countTiles());
    Assert.assertEquals(hardCodedMap.getMapName(), loadedMap.getMapName());

    for (Tile t : hardCodedMap.getAllTiles()) {
      Tile loadedMapTile = loadedMap.getTile(t);
      Assert.assertEquals(t.getCol(), loadedMapTile.getCol());
      Assert.assertEquals(t.getRow(), loadedMapTile.getRow());
      Assert.assertEquals(t.getTerrain().getName(), loadedMapTile.getTerrain().getName());
      Assert.assertEquals(t.getLocatableCount(), loadedMapTile.getLocatableCount());

      if (t.getLocatableCount() > 0) {
        Unit unit = hardCodedMap.getUnitOn(t);
        Unit loadedMapUnit = loadedMap.getUnitOn(loadedMapTile);

        validateUnits(unit, loadedMapUnit);
      }

      City hardCodedCity = hardCodedMap.getCityOn(t);
      City loadedCity = loadedMap.getCityOn(loadedMapTile);
      validateCities(hardCodedCity, loadedCity);
    }

    List<Player> hardCodedPlayers = new ArrayList<Player>(hardCodedMap.getUniquePlayers());
    List<Player> loadedPlayers = new ArrayList<Player>(loadedMap.getUniquePlayers());
    comparePlayers(hardCodedPlayers, loadedPlayers);
  }

  private void validateCities(City hardCodedCity, City loadedCity) {
    if (hardCodedCity != null) {
      Assert.assertTrue(hardCodedCity.getOwner() != null);
      Assert.assertTrue(loadedCity.getOwner() != null);
    }
  }

  private void validateUnits(Unit hardCodedUnit, Unit loadedUnit) {
    if (hardCodedUnit.canTransport() && hardCodedUnit.getLocatableCount() > 0) {
      int unitsInTransport = hardCodedUnit.getLocatableCount();
      int loadedUnitsInTransport = loadedUnit.getLocatableCount();
      Assert.assertEquals(unitsInTransport, loadedUnitsInTransport);
    }

    Assert.assertTrue(hardCodedUnit.getOwner() != null);
    Assert.assertTrue(loadedUnit.getOwner() != null);
  }

  private void comparePlayers(List<Player> hardCodedPlayers, List<Player> loadedPlayers) {
    Assert.assertTrue(hardCodedPlayers.size() == loadedPlayers.size());

    for (int key = 0; key < hardCodedMap.getNumPlayers(); key++) {
      Player hardCodedPlayer = hardCodedPlayers.get(key);
      Player loadedPlayer = loadedPlayers.get(key);
      Assert.assertTrue(hardCodedPlayer.getId() == (loadedPlayer.getId()));
      Assert.assertTrue(hardCodedPlayer.getColor().equals(loadedPlayer.getColor()));
    }
  }
}