package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.GameObject;
import com.customwars.client.model.gameobject.Locatable;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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
 * @author Stefan
 * @see Location
 * @see Locatable
 * @see Direction
 */
public class TileMap<T extends Location> extends GameObject {
  private static final Logger logger = Logger.getLogger(TileMap.class);
  private int tileSize;             // The square size in pixels
  private int cols, rows;           // The map size in tiles
  private List<List<T>> tiles;      // The map data, A List is used because it has generic support.

  /**
   * Create a Map with all tiles set to null.
   * Tiles should be added by setTile
   *
   * @param cols     The amount of columns in the map
   * @param rows     The amount of rows in the map
   * @param tileSize square size of 1 tile
   */
  public TileMap(int cols, int rows, int tileSize) {
    this.cols = cols;
    this.rows = rows;
    this.tileSize = tileSize;
    this.tiles = new ArrayList<List<T>>(cols);
    initTiles();
    validateMapState(false);
  }

  /**
   * Fills the map with null tiles
   */
  private void initTiles() {
    tiles.clear();
    for (int col = 0; col < cols; col++) {
      ArrayList<T> rowList = new ArrayList<T>(rows);
      for (int row = 0; row < rows; row++) {
        rowList.add(null);
      }
      tiles.add(rowList);
    }
  }

  /**
   * Validate this map
   *
   * @param validateTiles if all the tiles in the map should be checked
   * @throws IllegalStateException when the map is not valid
   */
  void validateMapState(boolean validateTiles) throws IllegalStateException {
    if (cols <= 0 || rows <= 0) {
      throw new IllegalStateException("Map cols:" + cols + ", rows:" + rows + " is not valid");
    }

    if (tileSize <= 0) {
      throw new IllegalStateException("TileSize: " + tileSize + " is <=0");
    }

    if (validateTiles) {
      // Each map position needs a non null tile object
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          T tile = tiles.get(col).get(row);
          if (tile == null) {
            throw new IllegalStateException("Map Location @ col:" + col + ", Row:" + row + " is null");
          }
        }
      }
    }
  }

  /**
   * Teleports the locatable from Location <tt>from</tt> to Location <tt>to</tt>
   * </p>
   * <tt>from</tt> should contain the locatable else the teleport action will not be executed
   * <tt>to</tt> can already contain a Locatable, in this case the locatable is added to the <tt>to</tt> tile.
   */
  public void teleport(Location from, Location to, Locatable locatable) {
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
   */
  public Iterable<T> getAllTiles() {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final WholeMapIterator wholeMapIterator = new WholeMapIterator();

        return new Iterator<T>() {
          public boolean hasNext() {
            return wholeMapIterator.hasNext();
          }

          public T next() {
            return wholeMapIterator.next();
          }

          public void remove() {
            wholeMapIterator.remove();
          }
        };
      }
    };
  }

  public Iterable<T> getSurroundingTiles(Location center, int minRange, int maxRange) {
    Range range = new Range(minRange, maxRange);
    return getSurroundingTiles(center, range);
  }

  /**
   * Return all the valid tiles surrounding <tt>center</tt> within the <tt>min, max</tt> range.
   * The center is never included</p>
   * Based on the range an AdjacentIterator or CircleIterator is returned.
   * if center is not valid then an empty Iterator is returned
   *
   * @param center The tile that is the center of the tiles to iterate over.
   * @param range  The range in which we need to start iterating relative to the center tile.
   * @return The tiles surrounding the given center tile.
   */
  public Iterable<T> getSurroundingTiles(Location center, Range range) {
    if (!isValid(center) || range.getMinRange() == 0 || range.getMaxRange() == 0) {
      return Collections.emptyList();
    }

    if (range.getMaxRange() == 1) {
      return getAdjacentIterator(center);
    } else {
      return getCircleIterator(center, range);
    }
  }

  private Iterable<T> getAdjacentIterator(final Location center) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final AdjacentIterator adjacentIterator = new AdjacentIterator(center);

        return new Iterator<T>() {
          public boolean hasNext() {
            return adjacentIterator.hasNext();
          }

          public T next() {
            return adjacentIterator.next();
          }

          public void remove() {
            adjacentIterator.remove();
          }
        };
      }
    };
  }

  private Iterable<T> getCircleIterator(final Location center, final Range range) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final SpiralIterator spiralIterator = new SpiralIterator(center, range);

        return new Iterator<T>() {
          public boolean hasNext() {
            return spiralIterator.hasNext();
          }

          public T next() {
            return spiralIterator.next();
          }

          public void remove() {
            spiralIterator.remove();
          }
        };
      }
    };
  }

  public Iterable<T> getSquareIterator(final Location center, final int range) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final SquareIterator squareIterator = new SquareIterator(center, range);

        return new Iterator<T>() {
          public boolean hasNext() {
            return squareIterator.hasNext();
          }

          public T next() {
            return squareIterator.next();
          }

          public void remove() {
            squareIterator.remove();
          }
        };
      }
    };
  }

  /**
   * Base class for internal iterators.
   * Idea from freecol http://www.freecol.org/
   */
  private abstract class MapIterator implements Iterator<T> {

    /**
     * Get the next tile as a T rather as an object.
     *
     * @return next T
     * @throws java.util.NoSuchElementException
     *          if iterator is exhausted.
     */
    public abstract T nextTile() throws NoSuchElementException;

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements.
     */
    public T next() {
      return nextTile();
    }

    /**
     * removing of tiles has been disabled
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Loop through all columns for every row
   * The center tile is never included, and all returned positions are valid {@link TileMap#isValid(Location)}
   */
  private final class WholeMapIterator extends MapIterator {
    private int col, row;

    public WholeMapIterator() {
      col = 0;
      row = 0;
    }

    /**
     * Determine if the iterator has another position in it.
     *
     * @return True of there is another position
     */
    public boolean hasNext() {
      return row < rows;
    }

    /**
     * @return Next tile
     * @throws java.util.NoSuchElementException
     *          if the iterator ran off the map bounds
     */
    @Override
    public T nextTile() throws NoSuchElementException {
      if (row < rows) {
        T tile = tiles.get(col).get(row);
        col++;                  // 1 col to the right
        if (col == cols) {      // Reached Last Column
          col = 0;              // Reset Col
          row++;                // Next Row
        }
        return tile;
      }
      throw new NoSuchElementException("Iterator exhausted");
    }
  }

  /**
   * Loop through Tiles around the Center Location
   * This will only return tiles at the 4 compass Directions(N,E,S,W)
   * relative to the center.
   * The center tile is never included, and all returned positions are valid {@link TileMap#isValid(Location)}
   */
  private final class AdjacentIterator extends MapIterator {
    private final int MAX_SURROUNDING_TILE_COUNT = 4;
    private final Location center;
    private int index;
    T nextTile;

    /**
     * @param center The tile around which to iterate
     */
    public AdjacentIterator(Location center) {
      this.center = center;
    }

    /**
     * Determine if the iterator has another position in it.
     *
     * @return True of there is another position
     */
    public boolean hasNext() {
      if (index == MAX_SURROUNDING_TILE_COUNT) {
        return false;
      }

      // loop through the 4 tiles around center starting at 0.
      while (index < MAX_SURROUNDING_TILE_COUNT) {
        Direction dir = Direction.values()[index];
        nextTile = getRelativeTile(center, dir);

        // Skip null tiles
        if (nextTile == null) {
          index++;
        } else {
          return true;
        }
      }
      return false;
    }

    /**
     * Obtain the next position to iterate over.
     *
     * @return Next tile
     * @throws NoSuchElementException if last tile already returned
     */
    @Override
    public T nextTile() throws NoSuchElementException {
      index += 1;
      if (isWithinMapBounds(nextTile))
        return nextTile;
      else {
        throw new NoSuchElementException("Iterator exhausted");
      }
    }
  }

  /**
   * An iterator returning Tiles in the form of a square around the center tile within a given range.
   * The center tile is never included, and all returned positions are valid {@link TileMap#isValid(Location)}
   */
  private final class SquareIterator extends MapIterator {
    private final Location center;
    private final int range;

    private final int totalSquareRows, totalSquareCols;
    private final Location leftTopTile;
    private final int rowOffset, colOffset;
    private int row, col;
    private LinkedList<T> square;

    /**
     * @param center The location to Iterate around
     * @param range  The amount of tiles to move away from the center
     */
    public SquareIterator(Location center, int range) {
      this.center = center;
      this.range = range;

      // The square can be smaller then the range due map bounds.
      rowOffset = countTiles(Direction.NORTH);
      colOffset = countTiles(Direction.WEST);

      leftTopTile = getTile(center.getCol() - colOffset, center.getRow() - rowOffset);

      // The size of the square
      // range 1 == 3, range 2 == 5, range 3 == 7...
      totalSquareCols = leftTopTile.getCol() + range + 1 + range - (range - colOffset);
      totalSquareRows = leftTopTile.getRow() + range + 1 + range - (range - rowOffset);

      // loop vars
      col = leftTopTile.getCol();
      row = leftTopTile.getRow();

      buildSquare();
    }

    // Move range Tiles to the direction relative to center
    // until we run of the map bounds
    private int countTiles(Direction direction) {
      Location tile = center;
      int offset;

      for (offset = 0; offset < range; offset++) {
        tile = getRelativeTile(tile, direction);

        if (!isWithinMapBounds(tile)) {
          break;
        }
      }
      return offset;
    }

    private void buildSquare() {
      square = new LinkedList<T>();
      while (hasNextRow()) {
        T tile = tiles.get(col++).get(row);

        if (tile != null && tile != center) {
          square.add(tile);
        }

        if (!hasNextCol()) {
          gotoNextRow();
        }
      }
    }

    private boolean hasNextRow() {
      return row < totalSquareRows && row < rows;
    }

    private boolean hasNextCol() {
      return col < totalSquareCols && col < cols;
    }

    private void gotoNextRow() {
      col = leftTopTile.getCol();
      row++;
    }

    public boolean hasNext() {
      return !square.isEmpty();
    }

    public T nextTile() {
      return square.removeLast();
    }
  }

  /**
   * An iterator returning tiles in a spiral starting at a center tile
   * The center tile is never included, and all returned positions are valid {@link TileMap#isValid(Location)}
   */
  private final class SpiralIterator extends MapIterator {
    private final Range range;
    private final Location center;
    private final List<T> circleTileList = new ArrayList<T>();
    private int index;
    private T nextTile;

    /**
     * @param center The center location of the spiral
     * @param range  min, max Radius of the spiral
     */
    public SpiralIterator(Location center, Range range) {
      this.range = range;
      this.center = center;

      if (center == null) {
        throw new IllegalArgumentException("Center tile cannot be null.");
      }

      // Get All tiles within Range.
      for (T tile : getSquareIterator(center, range.getMaxRange())) {
        if (isValid(tile) && inRange(tile)) {
          circleTileList.add(tile);
        }
      }
      // Validate the first Location.
      nextTile = circleTileList.get(0);
    }

    /**
     * Determine if the CircleIterator has another tile in it.
     *
     * @return <code>true</code> if there is another tile and
     *         <code>false</code> otherwise.
     */
    public boolean hasNext() {
      return isValid(nextTile) && index != circleTileList.size();
    }

    /**
     * Obtains the next tile.
     *
     * @return The next tile. This tile is guaranteed to be valid(not null, within map bounds)
     */
    public T nextTile() {
      nextTile = circleTileList.get(index++);
      return nextTile;
    }

    public boolean inRange(Location tile) {
      int distance = getDistanceBetween(tile, center);
      return range.isInRange(distance);
    }
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
    T oldVal = tiles.get(col).get(row);
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

  public T getRandomTile() {
    int randCol = (int) (Math.random() * cols);
    int randRow = (int) (Math.random() * rows);
    return getTile(randCol, randRow);
  }

  /**
   * @param loc the location(col,row) to retrieve the tile from
   * @return the tile @ the location or
   *         null if
   *         the location is outside the map bounds or
   *         the map has not yet been filled with tiles.
   */
  public T getTile(Location loc) {
    return getTile(loc.getCol(), loc.getRow());
  }

  /**
   * Returns the Location at the specified position in this list.
   *
   * @param col column coordinate in range of 0 - getCols()-1
   * @param row row coordinate in range of 0 - getRows()-1
   * @return the tile @ (col, row) or
   *         null if
   *         the location is outside the map bounds or
   *         the map has not yet been filled with tiles.
   */
  public T getTile(int col, int row) {
    if (isWithinMapBounds(col, row)) {
      return tiles.get(col).get(row);
    } else {
      return null;
    }
  }

  /**
   * Get the tile relative to the baseTile in a given direction.
   *
   * @param direction A Direction where a tile should be retrieved from relative to baseTile.
   * @param baseTile  The tile of which we want to retrieve the relative tile from.
   * @return The relative Tile or baseTile if direction is Direction.STILL
   */
  public T getRelativeTile(Location baseTile, Direction direction) {
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
    return getTile(col, row);
  }

  /**
   * Get the direction of the baseTile relative to the tile.
   * This method never returns null, instead Direction.still is returned when the direction could not be found.
   *
   * @return The Direction of the given tile relative to the baseTile.
   *         if tile has no relative direction to the baseTile Direction.STILL is returned.
   *         if baseTile or tile is null Direction.STILL is returned.
   */
  public Direction getDirectionTo(Location baseTile, Location tile) {
    Direction direction = Direction.STILL;

    if (baseTile == null || tile == null) {
      return Direction.STILL;
    }

    if (getRelativeTile(baseTile, Direction.NORTH) == tile) {
      direction = Direction.NORTH;
    } else if (getRelativeTile(baseTile, Direction.EAST) == tile) {
      direction = Direction.EAST;
    } else if (getRelativeTile(baseTile, Direction.SOUTH) == tile) {
      direction = Direction.SOUTH;
    } else if (getRelativeTile(baseTile, Direction.WEST) == tile) {
      direction = Direction.WEST;
    } else if (getRelativeTile(baseTile, Direction.NORTHEAST) == tile) {
      direction = Direction.NORTHEAST;
    } else if (getRelativeTile(baseTile, Direction.NORTHWEST) == tile) {
      direction = Direction.NORTHWEST;
    } else if (getRelativeTile(baseTile, Direction.SOUTHEAST) == tile) {
      direction = Direction.SOUTHEAST;
    } else if (getRelativeTile(baseTile, Direction.SOUTHWEST) == tile) {
      direction = Direction.SOUTHWEST;
    }
    return direction;
  }

  /**
   * @return The amount of tiles to traverse to go from a to b.
   */
  public int getDistanceBetween(Location a, Location b) {
    return Math.abs(a.getRow() - b.getRow()) +
            Math.abs(a.getCol() - b.getCol());
  }

  /**
   * @return True if the two tiles are next to each other
   */
  public boolean isAdjacent(Location a, Location b) {
    if (a == b)
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

    if (row < rows / 2 && col > cols / 2) return Direction.NORTHEAST;
    if (row < rows / 2 && col < cols / 2) return Direction.NORTHWEST;
    if (row > rows / 2 && col > cols / 2) return Direction.SOUTHEAST;
    if (row > rows / 2 && col < cols / 2) return Direction.SOUTHWEST;

    throw new AssertionError("A location is always in one of the 4 quadrants");
  }

  public boolean isValid(Location tile) {
    return isWithinMapBounds(tile);
  }

  private boolean isWithinMapBounds(Location tile) {
    return tile != null && isWithinMapBounds(tile.getCol(), tile.getRow());
  }

  private boolean isWithinMapBounds(int col, int row) {
    return col >= 0 && col < cols && row >= 0 && row < rows;
  }
}
