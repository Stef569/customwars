package com.customwars.client.MapMaker;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Connect terrains of the same type + handle special cases(bridges, delta)
 *
 * @author stefan
 */
public class TerrainConnector {
  private static final Logger logger = Logger.getLogger(TerrainConnector.class);

  private TileMap<Tile> map;
  private Terrain terrainToAdd;     // The terrain that the user wants to add
  private Terrain baseTerrain;      // The terrain that is going to be overwritten
  private List<Terrain> terrains;   // All the available terrains, sorted on ID

  // Calculated values
  private SurroundingTileData tileData;
  private List<Terrain> perfectMatchingTerrains;
  private List<Terrain> perfectAdjacentMatchingTerrains;
  private List<Terrain> matchingTerrains;
  private Terrain specialMatchingTerrain;

  public TerrainConnector(TileMap<Tile> map) {
    this.map = map;
    this.terrains = new ArrayList<Terrain>(TerrainFactory.getTerrains());
  }

  /**
   * Connect each surrounding terrain around the baseTile.
   *
   * @param baseTile     The tile where the resulting terrain should be added to
   * @param terrainToAdd The terrain that is used to calculate the best matching terrain.
   */
  public void turnSurroundingTerrains(Tile baseTile, Terrain terrainToAdd) {
    validate(baseTile, terrainToAdd);

    this.terrainToAdd = terrainToAdd;
    this.tileData = new SurroundingTileData(baseTile, terrainToAdd, map);
    List<Tile> allTouchingTiles = tileData.getSurroundingTiles();

    for (Tile t : allTouchingTiles) {
      if (map.isValid(t)) {
        this.baseTerrain = t.getTerrain();
        tileData = new SurroundingTileData(t, baseTerrain, map);
        Terrain bestMatchingTerrain = connectTerrain(t, t.getTerrain(), baseTerrain);
        t.setTerrain(bestMatchingTerrain);
      }
    }
  }

  /**
   * Find the matching terrain that can fit on baseTile by examining the tiles that surround the base tile.
   * Terrains of the same type should connect.
   *
   * @param baseTile     The tile where the resulting terrain should be added to
   * @param terrainToAdd The terrain type that is used to calculate the best matching terrain.
   * @return The best matching terrain
   */
  public Terrain connectTerrain(Tile baseTile, Terrain terrainToAdd) {
    return connectTerrain(baseTile, terrainToAdd, baseTile.getTerrain());
  }

  /**
   * Find the matching terrain that can fit on baseTile by examining the tiles that surround the base tile.
   * Terrains of the same type should connect.
   *
   * @param baseTile     The tile where the resulting terrain should be added to
   * @param terrainToAdd The terrain that is used to calculate the best matching terrain.
   * @param baseTerrain  The old terrain that will be overwritten
   * @return The best matching terrain
   */
  public Terrain connectTerrain(Tile baseTile, Terrain terrainToAdd, Terrain baseTerrain) {
    validate(baseTile, terrainToAdd);
    this.terrainToAdd = terrainToAdd;
    this.baseTerrain = baseTerrain;

    // Cities can't be overwritten by other terrains, but do connect
    if (baseTile.getTerrain() instanceof City) {
      return baseTile.getTerrain();
    }

    // Reefs don't connect at all, return it
    if (terrainToAdd.getName().equalsIgnoreCase("Reef")) {
      return terrainToAdd;
    }

    return getBestMatchingTerrain(baseTile);
  }

  private void validate(Tile baseTile, Terrain terrainToAdd) {
    Args.checkForNull(baseTile);
    Args.checkForNull(terrainToAdd);
  }

  private Terrain getBestMatchingTerrain(Tile baseTile) {
    tileData = new SurroundingTileData(baseTile, terrainToAdd, map);
    perfectMatchingTerrains = getTerrainsThatConnectsToAllDirections(tileData.getAllDirections());
    perfectAdjacentMatchingTerrains = getTerrainsThatConnectsToAllDirections(tileData.getAdjacentDirections());
    matchingTerrains = getTerrainsThatConnectsToOneofDirections();
    specialMatchingTerrain = getSpecialTerrain();
    //printMatches();

    return getBestMatchingTerrain();
  }

  private Terrain getBestMatchingTerrain() {
    Terrain bestMatch = null;

    if (isShoal(terrainToAdd)) {
      removeAllNonShoalTerrainsFrom(perfectMatchingTerrains);
      removeAllNonShoalTerrainsFrom(perfectAdjacentMatchingTerrains);
      removeAllNonShoalTerrainsFrom(matchingTerrains);
    }

    if (specialMatchingTerrain != null)
      bestMatch = specialMatchingTerrain;
    else if (!perfectMatchingTerrains.isEmpty()) {
      bestMatch = perfectMatchingTerrains.get(0);
    } else if (!perfectAdjacentMatchingTerrains.isEmpty()) {
      bestMatch = perfectAdjacentMatchingTerrains.get(0);
    } else if (!matchingTerrains.isEmpty()) {
      bestMatch = matchingTerrains.get(0);
    }

    // If at this point no match has been found, default back to the terrain to add
    if (bestMatch == null) {
      bestMatch = terrainToAdd;
    }
    return bestMatch;
  }

  private void removeAllNonShoalTerrainsFrom(Collection<Terrain> collection) {
    Iterator<Terrain> it = collection.iterator();

    while (it.hasNext()) {
      Terrain terrain = it.next();
      if (!isShoal(terrain)) {
        it.remove();
      }
    }
  }

  private boolean isShoal(Terrain terrain) {
    return terrain.getName().equalsIgnoreCase("shoal");
  }

  /**
   * @param directions The directions a terrain should connect to
   * @return A list of terrains that can connect to all of the <tt>directions<tt>.
   *         for the same Terrain type, this is the perfect match.
   */
  private List<Terrain> getTerrainsThatConnectsToAllDirections(List<Direction> directions) {
    List<Terrain> result = new LinkedList<Terrain>();
    for (Terrain terrain : terrains) {
      if (terrain.isSameType(terrainToAdd) && terrain.canConnectToAll(directions)) {
        result.add(terrain);
      }
    }
    return result;
  }

  /**
   * @return A list of terrains that can connect to one of the <tt>directions<tt>.
   *         for the same Terrain type
   */
  private List<Terrain> getTerrainsThatConnectsToOneofDirections() {
    List<Terrain> result = new LinkedList<Terrain>();
    for (Terrain terrain : terrains) {
      if (terrain.isSameType(terrainToAdd) && terrain.canConnectToOneOf(tileData.getAllDirections())) {
        result.add(terrain);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private Terrain getSpecialTerrain() {
    Terrain specialTerrain = null;
    if (terrainToAdd.isSameType("Road")) {
      if (isOverRiver()) {
        specialTerrain = getBridge(true, false, perfectMatchingTerrains, matchingTerrains, terrains);
      } else if (isOverOcean()) {
        specialTerrain = getBridge(false, true, perfectMatchingTerrains, matchingTerrains, terrains);
      }
    }
    return specialTerrain;
  }

  /**
   * @return Are the adjacent Terrains(horizontal or vertical) river terrains
   *         is the original terrain a river or bridge terrain
   */
  private boolean isOverRiver() {
    return (isRiver(tileData.getHorizontalTerrains()) || isRiver(tileData.getVerticalTerrains())) &&
      (baseTerrain.isRiver() || baseTerrain.spansOver("River"));
  }

  /**
   * @return Are all terrains a river terrain
   */
  private boolean isRiver(List<Terrain> terrains) {
    for (Terrain terrain : terrains) {
      if (!terrain.isRiver()) return false;
    }
    return true;
  }

  /**
   * @return Are the adjacent Terrains(horizontal or vertical) ocean terrains
   *         is the original terrain an ocean or bridge terrain
   */
  private boolean isOverOcean() {
    return (isOcean(tileData.getHorizontalTerrains()) || isOcean(tileData.getVerticalTerrains())) &&
      (baseTerrain.isOcean() || baseTerrain.spansOver("Ocean"));
  }

  /**
   * @return Are all terrains an ocean terrain
   */
  private boolean isOcean(List<Terrain> terrains) {
    for (Terrain terrain : terrains) {
      if (!terrain.isOcean()) return false;
    }
    return true;
  }

  private Terrain getBridge(boolean overRiver, boolean overOcean, List<Terrain>... terrainLists) {
    for (List<Terrain> terrainList : terrainLists) {
      for (Terrain terrain : terrainList) {
        if (overRiver && terrain.spansOver("River")) {
          return terrain;
        } else if (overOcean && terrain.spansOver("Ocean")) {
          return terrain;
        }
      }
    }
    return null;
  }

  private void printMatches() {
    logger.debug("All directions: " + tileData.getAllDirections());
    logger.debug("Adjacent directions: " + tileData.getAdjacentDirections());
    logger.debug("Terrains that can fit Perfectly(all): " + toTerrainNames(perfectMatchingTerrains));
    logger.debug("Terrains that can fit Perfectly(adj): " + toTerrainNames(perfectAdjacentMatchingTerrains));
    logger.debug("Terrains that can fit: " + toTerrainNames(matchingTerrains));
  }

  private List<String> toTerrainNames(List<Terrain> terrains) {
    List<String> terrainNames = new ArrayList<String>();
    for (Terrain terrain : terrains) {
      terrainNames.add(terrain.getName());
    }
    return terrainNames;
  }
}
