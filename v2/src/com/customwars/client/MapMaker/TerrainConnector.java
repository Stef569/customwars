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
  private static final Logger logger = Logger.getLogger(TerrainConnector.class);
  private static final Comparator<Terrain> TERRAIN_SORT_ON_ID = new Comparator<Terrain>() {
    public int compare(Terrain terrainA, Terrain terrainB) {
      return terrainA.getID() - terrainB.getID();
    }
  };

  private TileMap<Tile> map;
  private Terrain baseTerrain;
  private List<Terrain> terrains;

  // Calculated values
  private SurroundingTileData tileData;
  private List<Terrain> perfectMatchingTerrains;
  private List<Terrain> perfectAdjacentMatchingTerrains;
  private List<Terrain> matchingTerrains;
  private Terrain specialMatchingTerrain;

  public TerrainConnector(TileMap<Tile> map) {
    this.map = map;
    this.terrains = new ArrayList<Terrain>(TerrainFactory.getTerrains());
    Collections.sort(terrains, TERRAIN_SORT_ON_ID);
  }

  /**
   * Connect each surrounding terrain that is equal to the baseTerrain
   * around the the baseTile.
   */
  public void turnSurroundingTerrains(Tile baseTile, Terrain baseTerrain) {
    validate(baseTile, baseTerrain);

    tileData = new SurroundingTileData(baseTile, baseTerrain);
    tileData.calcSurroundingTileInformation();

    for (Tile t : tileData.touchingTilesWithSameTerrain) {
      Terrain bestMatchingTerrain = connectTerrain(t, baseTerrain);
      t.setTerrain(bestMatchingTerrain);
    }
  }

  /**
   * Find the matching terrain that can fit on baseTile by examining the tiles that surround the base tile.
   * Terrains of the same type should connect.
   *
   * @param baseTile    The tile where the resulting terrain should be added to
   * @param baseTerrain The base terrain type that is used to calculate the best matching terrain.
   */
  public Terrain connectTerrain(Tile baseTile, Terrain baseTerrain) {
    validate(baseTile, baseTerrain);
    this.baseTerrain = baseTerrain;

    // Cities can't be overwritten by other terrains, but do connect
    if (baseTile.getTerrain() instanceof City) {
      return baseTile.getTerrain();
    }

    // Reefs don't connect at all, return it
    if (baseTerrain.getName().equalsIgnoreCase("Reef"))
      return baseTerrain;

    return getBestMatchingTerrain(baseTile);
  }

  private void validate(Tile baseTile, Terrain baseTerrain) {
    Args.checkForNull(baseTile);
    Args.checkForNull(baseTerrain);
  }

  private Terrain getBestMatchingTerrain(Tile baseTile) {
    tileData = new SurroundingTileData(baseTile, baseTerrain);
    tileData.calcSurroundingTileInformation();

    perfectMatchingTerrains = getTerrainsThatConnectsToAllDirections(tileData.allDirections);
    perfectAdjacentMatchingTerrains = getTerrainsThatConnectsToAllDirections(tileData.adjacentDirections);
    matchingTerrains = getTerrainsThatConnectsToOneofDirections();
    specialMatchingTerrain = getSpecialTerrain();
    printMatches();

    return getBestMatchingTerrain();
  }

  private Terrain getBestMatchingTerrain() {
    Terrain bestMatch = null;

    if (specialMatchingTerrain != null)
      bestMatch = specialMatchingTerrain;
    else if (perfectMatchingTerrains.size() != 0) {
      bestMatch = perfectMatchingTerrains.get(0);
    } else if (perfectAdjacentMatchingTerrains.size() != 0) {
      bestMatch = perfectAdjacentMatchingTerrains.get(0);
    } else if (matchingTerrains.size() != 0) {
      bestMatch = matchingTerrains.get(0);
    }

    // If at this point no match has been found, default back to the user chosen terrain
    if (bestMatch == null) {
      bestMatch = baseTerrain;
    }
    return bestMatch;
  }

  /**
   * Returns a list of terrains that can connect to all of the <tt>touchingDirections<tt>.
   * for the same Terrain type, this is the perfect match.
   */
  private List<Terrain> getTerrainsThatConnectsToAllDirections(List<Direction> directions) {
    List<Terrain> result = new LinkedList<Terrain>();
    for (Terrain terrain : terrains) {
      if (terrain.isSameType(baseTerrain) && terrain.canConnectToAll(directions)) {
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
      if (terrain.isSameType(baseTerrain) && terrain.canConnectToOneOf(tileData.allDirections)) {
        result.add(terrain);
      }
    }
    return result;
  }

  private Terrain getSpecialTerrain() {
    Terrain specialTerrain = null;
    if (isOverRiver()) {
      specialTerrain = getBridge(true, false, perfectMatchingTerrains, matchingTerrains);
    } else if (isOverOcean()) {
      specialTerrain = getBridge(false, true, perfectMatchingTerrains, matchingTerrains);
    }
    return specialTerrain;
  }

  /**
   * Are the adjacent Terrains(Horizontal or vertical) an ocean
   */
  private boolean isOverOcean() {
    return !baseTerrain.isOcean() && (isOcean(tileData.horizontalTerrains) || isOcean(tileData.verticalTerrains));
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
    return !baseTerrain.isRiver() && (isRiver(tileData.horizontalTerrains) || isRiver(tileData.verticalTerrains));

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

  private Terrain getBridge(boolean overRiver, boolean overOcean, List<Terrain>... terrainLists) {
    for (List<Terrain> terrainList : terrainLists) {
      for (Terrain terrain : terrainList) {
        if (overRiver && terrain.getName().equalsIgnoreCase("Bridge")) {
          return terrain;
        } else if (overOcean && terrain.getName().equalsIgnoreCase("Suspension")) {
          return terrain;
        }
      }
    }
    return null;
  }

  private void printMatches() {
    logger.debug("All directions: " + tileData.allDirections);
    logger.debug("Adjacent directions: " + tileData.adjacentDirections);
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

  /**
   * Data store for surrounding tile information
   */
  private class SurroundingTileData {
    private Tile center;
    private Terrain terrain;

    private List<Tile> allTouchingTiles;
    private List<Tile> touchingTilesWithSameTerrain;
    private List<Direction> allDirections;
    private List<Direction> adjacentDirections;
    private List<Terrain> horizontalTerrains;
    private List<Terrain> verticalTerrains;

    private SurroundingTileData(Tile center, Terrain terrain) {
      this.center = center;
      this.terrain = terrain;
    }

    public void calcSurroundingTileInformation() {
      allTouchingTiles = calcAllSurroundingTiles();
      touchingTilesWithSameTerrain = calcTerrainsOfTheSameType(allTouchingTiles);
      allDirections = calcSurroundingDirections(touchingTilesWithSameTerrain);
      adjacentDirections = calcAjacentDirections(touchingTilesWithSameTerrain);
      horizontalTerrains = calcHorizontalTerrains();
      verticalTerrains = calcVerticalTerrains();
    }

    /**
     * Returns all tiles around the center
     */
    private List<Tile> calcAllSurroundingTiles() {
      List<Tile> surroundingTiles = new ArrayList<Tile>();
      if (map.isValid(center)) {
        for (Tile t : map.getSquareIterator(center, 1)) {
          surroundingTiles.add(t);
        }
      }
      return surroundingTiles;
    }

    /**
     * Search around the center for the same Terrain types ie (Road, River, ...)
     */
    public List<Tile> calcTerrainsOfTheSameType(List<Tile> surroundingTiles) {
      List<Tile> terrains = new ArrayList<Tile>();
      for (Tile t : surroundingTiles) {
        if (t.getTerrain().isSameType(terrain)) {
          terrains.add(t);
        }
      }
      return terrains;
    }

    /**
     * Loop through the surrounding tiles and return the directions relative to the center.
     */
    private List<Direction> calcSurroundingDirections(List<Tile> surroundingTiles) {
      List<Direction> directions = new ArrayList<Direction>();
      for (Tile t : surroundingTiles) {
        Direction direction = map.getDirectionTo(center, t);
        if (direction != Direction.STILL)
          directions.add(direction);
      }
      return directions;
    }

    private List<Direction> calcAjacentDirections(List<Tile> surroundingTiles) {
      List<Direction> directions = new ArrayList<Direction>();
      for (Tile t : surroundingTiles) {
        if (map.isAdjacent(t, center)) {
          Direction direction = map.getDirectionTo(center, t);
          if (direction != Direction.STILL)
            directions.add(direction);
        }
      }
      return directions;
    }

    /**
     * Get all the vertical terrains excluding the center
     * Some Tiles may return null when they are of the map bounds
     * these are not included in the returned terrain list
     */
    private List<Terrain> calcVerticalTerrains() {
      List<Terrain> verticalTerrains = new ArrayList<Terrain>();
      Tile north = map.getRelativeTile(center, Direction.NORTH);
      Tile south = map.getRelativeTile(center, Direction.SOUTH);
      if (north != null) verticalTerrains.add(north.getTerrain());
      if (south != null) verticalTerrains.add(south.getTerrain());
      return verticalTerrains;
    }

    /**
     * Get all the horizontal terrains excluding the center
     * Some Tiles may return null when they are of the map bounds
     * these are not included in the returned terrain list
     */
    private List<Terrain> calcHorizontalTerrains() {
      List<Terrain> horizontalTerrains = new ArrayList<Terrain>();
      Tile east = map.getRelativeTile(center, Direction.EAST);
      Tile west = map.getRelativeTile(center, Direction.WEST);
      if (east != null) horizontalTerrains.add(east.getTerrain());
      if (west != null) horizontalTerrains.add(west.getTerrain());
      return horizontalTerrains;
    }
  }
}
