package com.customwars.client.io.loading;

import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import slick.HardCodedGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    mapParser.writeMap(hardCodedMap, new FileOutputStream(mapFile));

    // Read the map back in
    Map<Tile> loadedMap = mapParser.readMap(new FileInputStream(mapFile));

    // The hardcoded map should be equal to the map loaded from the file
    Assert.assertEquals(hardCodedMap.getTileSize(), loadedMap.getTileSize());
    Assert.assertEquals(hardCodedMap.getNumPlayers(), loadedMap.getNumPlayers());
    Assert.assertEquals(hardCodedMap.countTiles(), loadedMap.countTiles());
    Assert.assertEquals(hardCodedMap.getProperty("NAME"), loadedMap.getProperty("NAME"));

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
    java.util.Map<Integer, Player> hardCodedPlayers = getPlayers(hardCodedMap);
    java.util.Map<Integer, Player> loadedPlayers = getPlayers(loadedMap);

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

  private void comparePlayers(java.util.Map<Integer, Player> hardCodedPlayers, java.util.Map<Integer, Player> loadedPlayers) {
    Assert.assertTrue(hardCodedPlayers.size() == loadedPlayers.size());

    for (int key : hardCodedPlayers.keySet()) {
      Player hardCodedPlayer = hardCodedPlayers.get(key);
      Player loadedPlayer = loadedPlayers.get(key);
      Assert.assertTrue(hardCodedPlayer.getId() == (loadedPlayer.getId()));
      Assert.assertTrue(hardCodedPlayer.getColor().equals(loadedPlayer.getColor()));
    }
  }

  private java.util.Map<Integer, Player> getPlayers(Map<Tile> map) {
    Set<Player> players = new HashSet();
    for (Tile t : map.getAllTiles()) {
      Unit unit = map.getUnitOn(t);
      City city = map.getCityOn(t);

      if (unit != null) {
        players.add(unit.getOwner());
      }
      if (city != null) {
        players.add(city.getOwner());
      }
    }

    java.util.Map playerMap = new HashMap();
    for (Player player : players) {
      playerMap.put(player.getId(), player);
    }
    return playerMap;
  }
}