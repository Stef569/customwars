package com.customwars.client.io.loading;

import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import slick.HardCodedGame;

import java.io.File;
import java.io.IOException;

public class BinCW2MapParserTest {

  private static final String TEST_DIR = "test";
  private static final String MAP_NAME = "test.map";
  private BinaryCW2MapParser mapParser;
  private Map<Tile> hardCodedMap;

  @Before
  public void beforeEachTest() {
    hardCodedMap = HardCodedGame.getMap();
    mapParser = new BinaryCW2MapParser();
    createTestDir();
  }

  private void createTestDir() {
    new File(TEST_DIR).mkdir();
  }

  @After
  public void afterAllTest() {
    deleteTestDir();
  }

  private void deleteTestDir() {
    File mapFile = new File(TEST_DIR, MAP_NAME);

    // can't delete non empty dirs, first remove the content then the test dir.
    if (mapFile.exists()) {
      mapFile.delete();
    }
    new File(TEST_DIR).delete();
  }

  /**
   * Write a hardcoded map to the test dir and then read it back in.
   * the hardcoded map and the loadedMap should have equal values.
   */
  @Test
  public void testWriteReadMap() throws IOException {
    // Write map to the test dir
    File mapFile = new File(TEST_DIR, MAP_NAME);
    mapParser.writeMap(hardCodedMap, mapFile);

    // Read it back in
    Map<Tile> loadedMap = mapParser.readMap(mapFile);

    // The map in memory is the same as the map loaded from the file
    Assert.assertEquals(hardCodedMap.getTileSize(), loadedMap.getTileSize());
    Assert.assertEquals(hardCodedMap.getNumPlayers(), loadedMap.getNumPlayers());
    Assert.assertEquals(hardCodedMap.countTiles(), loadedMap.countTiles());
    Assert.assertEquals(hardCodedMap.getProperty("MAP_NAME"), loadedMap.getProperty("MAP_NAME"));

    for (Tile t : hardCodedMap.getAllTiles()) {
      Tile loadedMapTile = loadedMap.getTile(t);
      Assert.assertEquals(t.getCol(), loadedMapTile.getCol());
      Assert.assertEquals(t.getRow(), loadedMapTile.getRow());
      Assert.assertEquals(t.getTerrain().getName(), loadedMapTile.getTerrain().getName());
      Assert.assertEquals(t.getLocatableCount(), loadedMapTile.getLocatableCount());

      if (t.getLocatableCount() > 0) {
        Unit unit = hardCodedMap.getUnitOn(t);
        Unit loadedMapUnit = loadedMap.getUnitOn(loadedMapTile);
        if (unit.canTransport() && unit.getLocatableCount() > 0) {
          int unitsInTransport = unit.getLocatableCount();
          int loadedUnitsInTransport = loadedMapUnit.getLocatableCount();
          Assert.assertEquals(unitsInTransport, loadedUnitsInTransport);
        }
      }
    }
  }
}
