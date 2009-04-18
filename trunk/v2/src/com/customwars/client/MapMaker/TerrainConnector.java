package com.customwars.client.MapMaker;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Connect terrains of the same type + handle special cases(bridges, delta)
 *
 * @author stefan
 */
public class TerrainConnector {
  private static final Comparator<Terrain> TERRAIN_SORT_ON_ID = new Comparator<Terrain>() {
    public int compare(Terrain terrainA, Terrain terrainB) {
      return terrainA.getID() - terrainB.getID();
    }
  };
  private static final Logger logger = Logger.getLogger(TerrainConnector.class);

  private TileMap<Tile> map;
  private Tile baseTile;
  private Terrain userChosenTerrain;
  private List<Terrain> terrains;

  // Calculated values
  SurroundingTileData tileData;
  private List<Terrain> perfectMatchingTerrains;
  private List<Terrain> matchingTerrains;
  private Terrain specialMatchingTerrain;

  public TerrainConnector(TileMap<Tile> map) {
    Args.checkForNull(map);
    this.map = map;
  }

  /**
   * Find the matching terrain that can fit on baseTile by examining the tiles that surround the base tile.
   * Terrains of the same type should connect.
   *
   * @param baseTile    The tile where the resulting terrain should be added to
   * @param baseTerrain The base terrain type that is used to calculate the best matching terrain.
   */
  public Terrain connectTerrain(Tile baseTile, Terrain baseTerrain) {
    Args.checkForNull(baseTile);
    Args.checkForNull(baseTerrain);
    this.baseTile = baseTile;
    this.userChosenTerrain = baseTerrain;

    tileData = new SurroundingTileData();
    tileData.calcSurroundingTileInformation();
    terrains = new ArrayList<Terrain>(TerrainFactory.getTerrains());
    Collections.sort(terrains, TERRAIN_SORT_ON_ID);

    perfectMatchingTerrains = getTerrainsThatConnectsToAllDirections();
    matchingTerrains = getTerrainsThatConnectsToOneofDirections();
    specialMatchingTerrain = getSpecialTerrain();
    printMatches();

    Terrain matchingTerrain = getBestMatchingTerrain();
    return matchingTerrain;
  }

  private Terrain getBestMatchingTerrain() {
    Terrain bestMatch = null;

    if (specialMatchingTerrain != null)
      bestMatch = specialMatchingTerrain;
    else if (perfectMatchingTerrains.size() != 0) {
      bestMatch = perfectMatchingTerrains.get(0);
    } else if (matchingTerrains.size() != 0) {
      bestMatch = matchingTerrains.get(0);
    }

    // If at this point no match has been found, default back to the user chosen terrain
    if (bestMatch == null) {
      bestMatch = userChosenTerrain;
    }
    return bestMatch;
  }

  /**
   * Returns a list of terrains that can connect to all of the <tt>touchingDirections<tt>.
   * for the same Terrain type, this is the perfect match.
   */
  private List<Terrain> getTerrainsThatConnectsToAllDirections() {
    List<Terrain> result = new LinkedList<Terrain>();
    for (Terrain terrain : terrains) {
      if (terrain.isSameType(userChosenTerrain) && terrain.canConnectToAll(tileData.touchingDirections)) {
        result.add(terrain);
      }
    }
    return result;
  }

  /**
   * Returns a list of terrains that can connect to one of the <tt>touchingDirections<tt>.
   * for the same Terrain type
   */
  private List<Terrain> getTerrainsThatConnectsToOneofDirections() {
    List<Terrain> result = new LinkedList<Terrain>();
    for (Terrain terrain : terrains) {
      if (terrain.isSameType(userChosenTerrain) && terrain.canConnectToOneOf(tileData.touchingDirections)) {
        result.add(terrain);
      }
    }
    return result;
  }

  private Terrain getSpecialTerrain() {
    Terrain bridge = null;
    if (isOverRiver()) {
      bridge = getBridge(true, false, perfectMatchingTerrains, matchingTerrains);
    } else if (isOverOcean()) {
      bridge = getBridge(false, true, perfectMatchingTerrains, matchingTerrains);
    }
    return bridge;
  }

  /**
   * Are the adjacent Terrains(Horizontal or vertical) an ocean
   */
  private boolean isOverOcean() {
    if (userChosenTerrain.isOcean()) return false;
    List<Terrain> horizontalTerrains = getTerrains(tileData.horizontalTouchingTiles);
    List<Terrain> verticalTerrains = getTerrains(tileData.verticalTouchingTiles);

    return isOcean(horizontalTerrains) || isOcean(verticalTerrains);
  }

  /**
   * Are all terrains an ocean terrain
   */
  private boolean isOcean(List<Terrain> terrains) {
    for (Terrain terrain : terrains) {
      if (!terrain.isOcean()) return false;
    }
    return true;
  }

  /**
   * Are the adjacent Terrains(Horizontal or vertical) a river
   */
  private boolean isOverRiver() {
    if (userChosenTerrain.isRiver()) return false;
    List<Terrain> horizontalTerrains = getTerrains(tileData.horizontalTouchingTiles);
    List<Terrain> verticalTerrains = getTerrains(tileData.verticalTouchingTiles);

    return isRiver(horizontalTerrains) || isRiver(verticalTerrains);
  }

  /**
   * Are all terrains a river terrain
   */
  private boolean isRiver(List<Terrain> terrains) {
    for (Terrain terrain : terrains) {
      if (!terrain.isRiver()) return false;
    }
    return true;
  }

  /**
   * Get each terrain on every tile in tiles
   */
  private List<Terrain> getTerrains(List<Tile> tiles) {
    List<Terrain> terrains = new ArrayList<Terrain>();
    for (Tile t : tiles) {
      terrains.add(t.getTerrain());
    }
    return terrains;
  }

  private Terrain getBridge(boolean overRiver, boolean overOcean, List<Terrain>... terrainLists) {
    for (List<Terrain> terrainList : terrainLists) {
      for (Terrain terrain : terrainList) {
        if (overRiver && terrain.spansOverRiver()) {
          return terrain;
        } else if (overOcean && terrain.spansOverOcean()) {
          return terrain;
        }
      }
    }
    return null;
  }

  private void printMatches() {
    logger.debug("Terrains that can fit Perfectly: " + toTerrainNames(perfectMatchingTerrains));
    logger.debug("Terrains that can fit: " + toTerrainNames(matchingTerrains));
  }

  private List<String> toTerrainNames(List<Terrain> terrains) {
    List<String> terrainNames = new ArrayList<String>();
    for (Terrain terrain : terrains) {
      terrainNames.add(terrain.getName());
    }
    return terrainNames;
  }

  /**
   * Data store for surround tile information
   */
  private class SurroundingTileData {
    private List<Tile> allTouchingTiles;
    private List<Tile> touchingTiles;
    private List<Direction> touchingDirections;
    private List<Tile> horizontalTouchingTiles;
    private List<Tile> verticalTouchingTiles;

    public void calcSurroundingTileInformation() {
      allTouchingTiles = calcAllAdjacentTiles();
      touchingTiles = calcAdjacentTerrainsOfTheSameType();
      touchingDirections = calcAdjacentDirections(touchingTiles);
      horizontalTouchingTiles = calcHorizontalTouchingTiles();
      verticalTouchingTiles = calcVerticalTouchingTiles();
    }

    /**
     * Returns all adjacent tiles around the baseTile.
     */
    private List<Tile> calcAllAdjacentTiles() {
      List<Tile> adjacentTiles = new ArrayList<Tile>();
      for (Tile t : map.getSurroundingTiles(baseTile, 1, 1)) {
        adjacentTiles.add(t);
      }
      return adjacentTiles;
    }

    /**
     * Search around the Base Tile for the same Terrain types ie (Road, River, ...)
     */
    private List<Tile> calcAdjacentTerrainsOfTheSameType() {
      List<Tile> terrains = new ArrayList<Tile>();
      for (Tile t : map.getSurroundingTiles(baseTile, 1, 1)) {
        if (t.getTerrain().isSameType(userChosenTerrain)) {
          terrains.add(t);
        }
      }
      return terrains;
    }

    /**
     * Loop through the touching tiles and return the directions relative to the baseTile.
     */
    private List<Direction> calcAdjacentDirections(List<Tile> touchingTiles) {
      List<Direction> directions = new ArrayList<Direction>();
      for (Tile t : touchingTiles) {
        if (map.isAdjacent(baseTile, t)) {
          Direction direction = map.getDirectionTo(baseTile, t);
          directions.add(direction);
        }
      }
      return directions;
    }

    /**
     * Get all the vertical terrains excluding the baseTile
     * Some Tiles may return null when they are of the map bounds
     * these are not included in the returned list
     */
    private List<Tile> calcVerticalTouchingTiles() {
      List<Tile> verticalTiles = new ArrayList<Tile>();
      Tile north = map.getAdjacent(baseTile, Direction.NORTH);
      Tile south = map.getAdjacent(baseTile, Direction.SOUTH);
      if (north != null) verticalTiles.add(north);
      if (south != null) verticalTiles.add(south);
      return verticalTiles;
    }

    /**
     * Get all the horizontal terrains excluding the baseTile
     * Some Tiles may return null when they are of the map bounds
     * these are not included in the returned list
     */
    private List<Tile> calcHorizontalTouchingTiles() {
      List<Tile> horizontalTiles = new ArrayList<Tile>();
      Tile east = map.getAdjacent(baseTile, Direction.EAST);
      Tile west = map.getAdjacent(baseTile, Direction.WEST);
      if (east != null) horizontalTiles.add(east);
      if (west != null) horizontalTiles.add(west);
      return horizontalTiles;
    }
  }
}
