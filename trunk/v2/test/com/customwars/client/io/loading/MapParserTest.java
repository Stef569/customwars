package com.customwars.client.io.loading;

import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import slick.HardCodedGame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapParserTest {
  private static final String TEST_DIR = "testData/";
  private static final String MAP_NAME = "test.map";

  private MapParser mapParser;
  private Map<Tile> map;

  @Before
  public void beforeEachTest() {
    map = HardCodedGame.getMap();
    mapParser = new MapParser();
  }

  @AfterClass
  public static void afterAllTest() {
    File mapFile = new File(TEST_DIR + MAP_NAME);

    if (mapFile.exists() && !mapFile.isDirectory())
      mapFile.delete();
  }

  @Ignore
  /**
   * Write a map to testdata and then read it back in.
   * the map and the loadedMap should have equal values.
   */
  public void testWriteReadMap() throws IOException {
    List<String> mapProperties = new ArrayList<String>();
    Map<Tile> loadedMap;  // the map read from disk

    for (String key : map.getPropertyKeys()) {
      mapProperties.add("[" + key + " " + map.getProperty(key) + "]");
    }

    mapParser.writeMap(TEST_DIR + MAP_NAME, mapProperties.toArray(new String[]{}), map);
    loadedMap = mapParser.loadMapAsResource(TEST_DIR + MAP_NAME);

    Assert.assertEquals(map.getTileSize(), loadedMap.getTileSize());
    Assert.assertEquals(map.getNumPlayers(), loadedMap.getNumPlayers());
    Assert.assertEquals(map.countTiles(), loadedMap.countTiles());
    Assert.assertEquals(map.getProperty("MAP_NAME"), loadedMap.getProperty("MAP_NAME"));

    for (Tile t : map.getAllTiles()) {
      Assert.assertEquals(t.getTerrain().getName(), loadedMap.getTile(t).getTerrain().getName());
      Assert.assertEquals(t.getLocatableCount(), loadedMap.getTile(t).getLocatableCount());
      Assert.assertEquals(t.getLastLocatable().getLocation(), loadedMap.getTile(t).getLastLocatable().getLocation());
      Assert.assertEquals(t.getCol(), loadedMap.getTile(t).getCol());
      Assert.assertEquals(t.getRow(), loadedMap.getTile(t).getRow());
      Assert.assertEquals(t.getCol(), loadedMap.getTile(t).getCol());
    }
  }
}
