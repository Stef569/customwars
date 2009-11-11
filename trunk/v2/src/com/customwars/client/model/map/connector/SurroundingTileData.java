package com.customwars.client.model.map.connector;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Location2D;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Data store for surrounding tile and terrain information around a center tile
 */
public class SurroundingTileData {
  private final TileMap<Tile> map;
  private final Tile center;
  private final Terrain terrain;

  private List<Tile> surroundingTiles;
  private List<Tile> surroundingTilesWithSameTerrain;
  private List<Direction> allDirections;
  private List<Direction> adjacentDirections;
  private List<Terrain> horizontalTerrains;
  private List<Terrain> verticalTerrains;

  public SurroundingTileData(Tile center, Terrain terrain, TileMap<Tile> map) {
    if (!map.isValid(center)) {
      throw new IllegalArgumentException("center is not valid");
    }

    this.center = center;
    this.terrain = terrain;
    this.map = map;
    calcSurroundingTileInformation();
  }

  private void calcSurroundingTileInformation() {
    surroundingTiles = calcAllSurroundingTiles();
    surroundingTilesWithSameTerrain = calcTerrainsOfTheSameType(surroundingTiles);
    allDirections = calcSurroundingDirections(surroundingTilesWithSameTerrain);
    adjacentDirections = calcAjacentDirections(surroundingTilesWithSameTerrain);
    horizontalTerrains = calcHorizontalTerrains();
    verticalTerrains = calcVerticalTerrains();
  }

  /**
   * @return all tiles around the center including the tiles that are off the map
   */
  private List<Tile> calcAllSurroundingTiles() {
    List<Tile> surroundingTiles = new ArrayList<Tile>();

    for (Tile t : map.getSquareIterator(center, 1)) {
      surroundingTiles.add(t);
    }
    addOffTheMapTiles(surroundingTiles);
    return surroundingTiles;
  }

  /**
   * Add tiles with sea terrain to the surrounding tiles until it contains 8 tiles.
   * one for each Direction around the center.
   *
   * @param surroundingTiles a list of tiles that are within the map,
   *                         they are missing the off the map tiles
   */
  private void addOffTheMapTiles(List<Tile> surroundingTiles) {
    List<Direction> surroundingDirections = calcSurroundingDirections(surroundingTiles);

    while (surroundingTiles.size() < 8) {
      for (Direction direction : Direction.values()) {
        if (direction == Direction.STILL) continue;

        // for each direction that is off the map
        // get the off the map position(col,row)
        // and add it to the surrounding tiles collection
        if (!surroundingDirections.contains(direction)) {
          Location offTheMapLocation = getOffTheMapLocation(direction);
          int offTheMapCol = offTheMapLocation.getCol();
          int offTheMapRow = offTheMapLocation.getRow();
          Terrain sea = TerrainFactory.getTerrain("ocean");
          Tile seaTile = new Tile(offTheMapCol, offTheMapRow, sea);
          surroundingTiles.add(seaTile);
        }
      }
    }
  }

  private Location getOffTheMapLocation(Direction direction) {
    int col = center.getCol();
    int row = center.getRow();

    switch (direction) {
      case NORTH:
        row--;
        break;
      case NORTHEAST:
        col++;
        row--;
        break;
      case EAST:
        col = map.getCols();
        break;
      case SOUTHEAST:
        col++;
        row++;
        break;
      case SOUTH:
        row = map.getRows();
        break;
      case SOUTHWEST:
        col--;
        row++;
        break;
      case WEST:
        col--;
        break;
      case NORTHWEST:
        col--;
        row--;
        break;
    }
    return new Location2D(col, row);
  }

  /**
   * Search around the center for the same Terrain types ie (Roads, Rivers, ...)
   * Include the surrounding terrains that span over this terrain type(bridges)
   */
  public List<Tile> calcTerrainsOfTheSameType(List<Tile> surroundingTiles) {
    List<Tile> terrains = new ArrayList<Tile>();

    for (Tile t : surroundingTiles) {
      Terrain surroundingTerrain = t.getTerrain();
      if (surroundingTerrain.isSameType(terrain) || surroundingTerrain.spansOver(terrain)) {
        terrains.add(t);
      }
    }
    return terrains;
  }

  /**
   * Loop through the same terrain tiles and return the directions relative to the center.
   */
  private List<Direction> calcSurroundingDirections(List<Tile> surroundingTilesWithSameTerrain) {
    List<Direction> directions = new ArrayList<Direction>();

    for (Tile t : surroundingTilesWithSameTerrain) {
      Direction direction = map.getDirectionTo(center, t);
      if (direction != Direction.STILL)
        directions.add(direction);
    }
    return directions;
  }

  private List<Direction> calcAjacentDirections(List<Tile> surroundingTilesWithSameTerrain) {
    List<Direction> adjacentDirections = new ArrayList<Direction>();

    for (Tile t : surroundingTilesWithSameTerrain) {
      if (TileMap.isAdjacent(t, center)) {
        Direction direction = map.getDirectionTo(center, t);
        if (direction != Direction.STILL)
          adjacentDirections.add(direction);
      }
    }
    return adjacentDirections;
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

  public List<Tile> getSurroundingTiles() {
    return surroundingTiles;
  }

  public List<Tile> getSurroundingTilesWithSameTerrain() {
    return surroundingTilesWithSameTerrain;
  }

  public List<Direction> getAllDirections() {
    return allDirections;
  }

  public List<Direction> getAdjacentDirections() {
    return adjacentDirections;
  }

  public List<Terrain> getHorizontalTerrains() {
    return horizontalTerrains;
  }

  public List<Terrain> getVerticalTerrains() {
    return verticalTerrains;
  }
}