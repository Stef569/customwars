package com.customwars.client.model.map;

import com.customwars.client.model.Observable;
import com.customwars.client.model.gameobject.Locatable;
import org.apache.log4j.Logger;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The map consists of a grid of rectangular tiles,
 * stored as a 2-dimensional array represented by Locations.
 * </p>
 * A Tile has 8 neighbours:
 * 4 adjacent (North, East, South, West) and 4 diagonal(NorthEast, SouthEast, SouthWest, NorthWest)
 * Each tile has a height and width in pixels of tileSize.
 * A Tile is always in one of the 4 quadrants(NE, NW, SE, SW)
 * </p>
 * There are various ways to iterate over the tiles:
 * All tiles, surrounding tiles in a spiral and in a square.
 * This class does not contain game specific logic.
 *
 * @see Location
 * @see Locatable
 * @see Direction
 */
public class TileMap<T extends Location> implements Observable, Serializable {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(TileMap.class);
  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
  private transient MapIterators<T> mapIterators = new MapIterators<T>(this);
  private final int tileSize;             // The square size in pixels
  private final int cols, rows;           // The map size in tiles
  private final List<List<T>> tiles;      // The map data, A List is used because it has generic support.

  /**
   * Create a Map with all tiles set to null.
   * Tiles should be added by setTile
   *
   * @param cols     The amount of columns in the map
   * @param rows     The amount of rows in the map
   * @param tileSize square size of 1 tile
   */
  public TileMap(int cols, int rows, int tileSize) {
    if (cols <= 0 || rows <= 0) {
      throw new IllegalArgumentException("Map cols:" + cols + ", rows:" + rows + " is not valid");
    }

    if (tileSize <= 0) {
      throw new IllegalArgumentException("TileSize: " + tileSize + " is <=0");
    }

    this.cols = cols;
    this.rows = rows;
    this.tileSize = tileSize;
    this.tiles = new ArrayList<List<T>>(cols);
    initTiles();
  }

  /**
   * Fills the map with null tiles
   */
  private void initTiles() {
    tiles.clear();
    for (int col = 0; col < cols; col++) {
      List<T> rowList = new ArrayList<T>(rows);
      for (int row = 0; row < rows; row++) {
        rowList.add(null);
      }
      tiles.add(rowList);
    }
  }

  /**
   * Teleport the locatable from one Location in the map to another Location
   * </p>
   * The <tt>from location</tt> in the map should contain the locatable else the teleport action will not be executed
   * The <tt>destination</tt> in the map can already contain a Locatable, in this case the locatable is added to the <tt>to</tt> tile.
   *
   * A warning is logged if the locatable is not present on the from location.
   *
   * @param fromLocation the location in the map to retrieve the locatable from
   * @param destination  the location in the map to teleport the locatable <tt>to</tt>
   * @param locatable    The locatable to teleport to the <tt>destination</tt>
   */
  public void teleport(Location fromLocation, Location destination, Locatable locatable) {
    Location from = getTile(fromLocation);
    Location to = getTile(destination);

    if (from.contains(locatable)) {
      from.remove(locatable);
      to.add(locatable);
    } else {
      logger.warn("from " + from + " does not contain " + locatable);
    }
  }

  /**
   * Iterate over each tile in the map
   * The returned tiles can be null if the map has not been initialised yet.
   *
   * @return All the tiles in this map
   */
  public Iterable<T> getAllTiles() {
    return mapIterators.getAllTiles();
  }

  /**
   * Return all the valid tiles surrounding <tt>center</tt> within the <tt>min, max</tt> range.
   * The center is not included</p>
   *
   * @param center   The tile that is the center of the tiles to iterate over.
   * @param minRange The min range in which we need to start iterating relative to the center tile.
   * @param maxRange The max range at which we need to stop iterating relative to the center tile.
   * @return The tiles surrounding the given center tile, excluding off the map tiles
   */
  public Iterable<T> getSurroundingTiles(Location center, int minRange, int maxRange) {
    return mapIterators.getSurroundingTiles(center, new Range(minRange, maxRange));
  }

  /**
   * Return all the valid tiles surrounding <tt>center</tt> within the <tt>min, max</tt> range.
   * The center is not included</p>
   *
   * @param center The tile that is the center of the tiles to iterate over.
   * @param range  The range in which we need to start iterating relative to the center tile.
   * @return The tiles surrounding the given center tile, excluding off the map tiles
   */
  public Iterable<T> getSurroundingTiles(Location center, Range range) {
    return mapIterators.getSurroundingTiles(center, range);
  }

  /**
   * Return tiles in the form of a square around the center tile within a given range.
   * The center tile is not included, and all returned positions are within the map.
   *
   * @param center The center of the square to iterate around
   * @param range  The range in tiles around the center
   * @return All tiles in the form of a square around the center withing the given range
   */
  public Iterable<T> getSquareIterator(final Location center, final int range) {
    return mapIterators.getSquareIterator(center, range);
  }

  /**
   * Use the col, row of the Location
   * to set itself in the map.
   *
   * @param location the location to put in the map at location.getCol(), location.getRow()
   */
  public void setTile(T location) {
    setTile(location.getCol(), location.getRow(), location);
  }

  /**
   * @param mapLocation the col, row where newTile should be placed
   * @param newTile     the tile to add to the map
   */
  public void setTile(Location mapLocation, T newTile) {
    int col = mapLocation.getCol();
    int row = mapLocation.getRow();
    setTile(col, row, newTile);
  }

  /**
   * @param col     column in the map where the newTile shoud be placed
   * @param row     row in the map where the newTile should be placed
   * @param newTile the tile to add to the map
   */
  public void setTile(int col, int row, T newTile) {
    T oldVal = getTile(col, row);
    tiles.get(col).set(row, newTile);
    firePropertyChange("tile", oldVal, newTile);
  }

  public int getTileSize() {
    return tileSize;
  }

  public int getCols() {
    return cols;
  }

  public int getRows() {
    return rows;
  }

  /**
   * @return The total amount of tiles
   */
  public int countTiles() {
    return rows * cols;
  }

  /**
   * @return The width in pixels
   */
  public int getWidth() {
    return cols * tileSize;
  }

  /**
   * @return The height in pixels
   */
  public int getHeight() {
    return rows * tileSize;
  }

  /**
   * @return The size in pixels
   */
  public Dimension getSize() {
    return new Dimension(getWidth(), getHeight());
  }

  public T getRandomTile() {
    int randCol = (int) (Math.random() * cols);
    int randRow = (int) (Math.random() * rows);
    return getTile(randCol, randRow);
  }

  /**
   * @param loc the location(col,row) to retrieve the tile from
   * @return the tile @ the location or
   * null if
   * the location is outside the map bounds or
   * the map has not yet been filled with tiles.
   */
  public T getTile(Location loc) {
    return getTile(loc.getCol(), loc.getRow());
  }

  /**
   * Returns the Tile object at the specified col, row coordinates.
   *
   * @param col column coordinate in range of 0 - getCols()-1
   * @param row row coordinate in range of 0 - getRows()-1
   * @return the tile @ (col, row) or
   * null if
   * the location is outside the map bounds or
   * the map has not yet been filled with tiles.
   */
  public T getTile(int col, int row) {
    return isWithinMapBounds(col, row) ? tiles.get(col).get(row) : null;
  }

  /**
   * Get the tile relative to the baseTile in a given direction.
   *
   * @param direction The Direction where a tile should be retrieved from relative to baseTile.
   * @param baseTile  The tile of which we want to retrieve the relative tile from.
   * @return The relative Tile
   * The baseTile if direction is Direction.STILL
   * Null if the direction relative to the current tile is out of the map
   */
  public T getRelativeTile(Location baseTile, Direction direction) {
    Location location = getRelativeLocation(baseTile, direction);
    return getTile(location);
  }

  private static Location getRelativeLocation(Location baseTile, Direction direction) {
    int row = baseTile.getRow();
    int col = baseTile.getCol();

    switch (direction) {
      case NORTH:
        row = baseTile.getRow() - 1;
        break;
      case EAST:
        col = baseTile.getCol() + 1;
        break;
      case SOUTH:
        row = baseTile.getRow() + 1;
        break;
      case WEST:
        col = baseTile.getCol() - 1;
        break;
      case NORTHEAST:
        col = baseTile.getCol() + 1;
        row = baseTile.getRow() - 1;
        break;
      case SOUTHEAST:
        col = baseTile.getCol() + 1;
        row = baseTile.getRow() + 1;
        break;
      case SOUTHWEST:
        col = baseTile.getCol() - 1;
        row = baseTile.getRow() + 1;
        break;
      case NORTHWEST:
        col = baseTile.getCol() - 1;
        row = baseTile.getRow() - 1;
        break;
      case STILL:
        break;
    }
    return new Location2D(col, row);
  }

  /**
   * Get the direction of the baseTile relative to the tile.
   * This method never returns null, instead Direction.
   * Direction.Still is returned when the direction to the relative tile could not be determined.
   *
   * @param baseTile     The tile of which we want to retrieve the direction to relativeTile from.
   * @param relativeTile The tile that touches the baseTile
   * @return The Direction of the given tile relative to the baseTile.
   * if the relativeTile does not touch the baseTile Direction.STILL is returned.
   * if baseTile or tile is null Direction.STILL is returned.
   */
  public Direction getDirectionTo(Location baseTile, Location relativeTile) {
    if (baseTile == null || relativeTile == null) {
      return Direction.STILL;
    }

    Direction direction = Direction.STILL;
    if (getRelativeLocation(baseTile, Direction.NORTH).equals(relativeTile)) {
      direction = Direction.NORTH;
    } else if (getRelativeLocation(baseTile, Direction.EAST).equals(relativeTile)) {
      direction = Direction.EAST;
    } else if (getRelativeLocation(baseTile, Direction.SOUTH).equals(relativeTile)) {
      direction = Direction.SOUTH;
    } else if (getRelativeLocation(baseTile, Direction.WEST).equals(relativeTile)) {
      direction = Direction.WEST;
    } else if (getRelativeLocation(baseTile, Direction.NORTHEAST).equals(relativeTile)) {
      direction = Direction.NORTHEAST;
    } else if (getRelativeLocation(baseTile, Direction.NORTHWEST).equals(relativeTile)) {
      direction = Direction.NORTHWEST;
    } else if (getRelativeLocation(baseTile, Direction.SOUTHEAST).equals(relativeTile)) {
      direction = Direction.SOUTHEAST;
    } else if (getRelativeLocation(baseTile, Direction.SOUTHWEST).equals(relativeTile)) {
      direction = Direction.SOUTHWEST;
    }
    return direction;
  }

  /**
   * Returns the distance in tiles between two locations.
   * if Location a or b is null, 0 is returned
   *
   * @param a one location
   * @param b another location
   * @return The amount of tiles to traverse to go from a to b.
   */
  public static int getDistanceBetween(Location a, Location b) {
    if (a == b || a == null || b == null)
      return 0;

    return Math.abs(a.getRow() - b.getRow()) +
      Math.abs(a.getCol() - b.getCol());
  }

  /**
   * Test whether location a is adjacent to location b
   *
   * @param a one location
   * @param b another location
   * @return True if the two locations are next to each other
   */
  public static boolean isAdjacent(Location a, Location b) {
    if (a == b || a == null || b == null)
      return false;

    int deltaRow = Math.abs(a.getRow() - b.getRow());
    int deltaCol = Math.abs(a.getCol() - b.getCol());
    return (deltaRow <= 1) && (deltaCol <= 1) && (deltaRow != deltaCol);
  }

  /**
   * Get the quadrant for the given location
   * Each tilemap has 4 quadrants, a Location within this tilemap is always within 1 quadrant
   *
   * @param tile The tile that is located in one of the 4 quadrants
   * @return The quadrant as a Compass direction (NE, SE, SW or NW)
   */
  public Direction getQuadrantFor(Location tile) {
    int col = tile.getCol();
    int row = tile.getRow();

    if (row <= rows / 2 && col >= cols / 2) return Direction.NORTHEAST;
    if (row <= rows / 2 && col <= cols / 2) return Direction.NORTHWEST;
    if (row >= rows / 2 && col >= cols / 2) return Direction.SOUTHEAST;
    if (row >= rows / 2 && col <= cols / 2) return Direction.SOUTHWEST;

    throw new AssertionError("A location is always in one of the 4 quadrants");
  }

  /**
   * A tile is valid when it is not null and within the map bounds
   */
  public boolean isValid(Location tile) {
    return isWithinMapBounds(tile);
  }

  public boolean isWithinMapBounds(Location tile) {
    return tile != null && isWithinMapBounds(tile.getCol(), tile.getRow());
  }

  private boolean isWithinMapBounds(int col, int row) {
    return col >= 0 && col < cols && row >= 0 && row < rows;
  }

  /**
   * Converts pixel coordinates into a Tile within the map
   *
   * @param x pixel position in the game (where x is relative to the map 0,0 coordinates)
   * @param y pixel position in the game (where y is relative to the map 0,0 coordinates)
   * @return The tile at the pixel location, or null if x,y is not valid.
   */
  public T pixelsToTile(int x, int y) {
    if (x < 0 || y < 0) return null;

    int col = x / tileSize;
    int row = y / tileSize;
    return getTile(col, row);
  }

  void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    mapIterators = new MapIterators<T>(this);
  }
}
