package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.GameObject;
import com.customwars.client.model.gameobject.Locatable;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Data is stored as a 2-dimensional array containing references to Location objects
 * A List is used because it has generic support.
 * The map consists of a grid of rectangular tiles. Tiles have 4 neighbours(North, East, South, West).
 * Each tile has a height and width in pixels of tileSize.
 * There are various ways to iterate over the tiles:
 * All tiles of this map, surrounding tiles around a center tile within a range
 * surrounding tiles in a square around a center within a range.
 * This class should not contain game specific logic, it only knows about Location objects.
 *
 * @author Stefan
 * @see Location
 */
public class TileMap<T extends Location> extends GameObject {
  private static final Logger logger = Logger.getLogger(TileMap.class);
  private int tileSize;             // The square size in pixels
  private int cols, rows;           // The map size in tiles
  private List<List<T>> tiles;

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
   * <tt>from</tt> should contain the locatable, else the teleport action will not be executed
   * if <tt>to</tt> already contains a Locatable then locatable is added to <tt>to</tt>.
   */
  public void teleport(Location from, Location to, Locatable locatable) {
    if (from.contains(locatable)) {
      from.remove(locatable);
      to.add(locatable);
    } else {
      logger.warn("from " + from + " does not contain " + locatable);
    }
  }

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

  /**
   * Return all the tiles surrounding <tt>center</tt> within the <tt>min, max</tt> range.
   * The center is not included.</p>
   * Based on the range an AdjacentIterator or CircleIterator is returned.
   * if center is not valid then an empty Iterator is returned
   *
   * @param center   The tile that is the center of the tiles to iterate over.
   * @param minRange How far away do we need to start iterating from the center tile.
   * @param maxRange How far away do we need to stop iterating from the center tile.
   * @return The tiles surrounding the given tile.
   */
  public Iterable<T> getSurroundingTiles(Location center, int minRange, int maxRange) {
    if (minRange > maxRange)
      throw new IllegalArgumentException("minrange " + minRange + " > then " + maxRange);

    if (!isValid(center)) {
      return emptyIterator();
    }

    if (maxRange == 1) {
      return getAdjacentIterator(center);
    } else {
      return getCircleIterator(center, minRange, maxRange);
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

  private Iterable<T> getCircleIterator(final Location center, final int minRange, final int maxRange) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final CircleIterator circleIterator = new CircleIterator(center, minRange, maxRange);

        return new Iterator<T>() {
          public boolean hasNext() {
            return circleIterator.hasNext();
          }

          public T next() {
            return circleIterator.next();
          }

          public void remove() {
            circleIterator.remove();
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

  private Iterable<T> emptyIterator() {
    return new Iterable<T>() {
      public Iterator<T> iterator() {

        return new Iterator<T>() {
          public boolean hasNext() {
            return false;
          }

          public T next() {
            return null;
          }

          public void remove() {
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
     * @throws java.util.NoSuchElementException if iterator is exhausted.
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
     * @throws java.util.NoSuchElementException if we ran off the map bounds
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
   * This will only return tiles at the 4 compass Directions
   * relative to the center.
   * if a tile is found to be out of the map bounds it is skipped
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
        nextTile = getAdjacent(center, dir);

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
   * An interator returning Tiles in the form of a square around the center tile within a given range.
   * The center tile is included, and all returned positions are valid.
   */
  private final class SquareIterator extends MapIterator {
    private final Location startTile;

    // The square can be bigger or smaller then the range due map bounds.
    private final int totalSquareRows, totalSquareCols;
    private int row, col;

    /**
     * @param center The location to Iterate around
     * @param range  The amount of tiles to move away from the center
     */
    public SquareIterator(Location center, int range) {
      if (center == null) {
        throw new IllegalArgumentException("Center Location cannot be null");
      }

      Location tile = center;
      // Move maxRange Tiles up
      int rowOffset;
      for (rowOffset = 0; rowOffset < range; rowOffset++) {
        tile = getAdjacent(tile, Direction.NORTH);
        // We reached the map limit
        if (tile == null) {
          break;
        }
      }

      tile = center;
      // Move maxRange Tiles left
      int colOffset;
      for (colOffset = 0; colOffset < range; colOffset++) {
        tile = getAdjacent(tile, Direction.WEST);
        // We reached the map limit
        if (tile == null) {
          break;
        }
      }

      // Get startTile(left Top position)
      int startRow = center.getRow() - rowOffset;
      int startCol = center.getCol() - colOffset;
      startTile = getTile(startCol, startRow);

      // Loop Vars
      totalSquareCols = startTile.getCol() + range - (range - colOffset) + range;
      totalSquareRows = startTile.getRow() + range - (range - rowOffset) + range;
      col = startTile.getCol();
      row = startTile.getRow();
    }

    /**
     * Determine if the iterator has another Location Within
     * Square bounds and map bounds.
     *
     * @return <code>true</code> if there is another tile and
     *         <code>false</code> otherwise.
     */
    public boolean hasNext() {
      return row <= totalSquareRows && row < rows;
    }

    /**
     * Obtains the next tile.
     *
     * @return The next Tile.
     *
     *         This Tile is guaranteed to be valid(not null, within map bounds)
     */
    public T nextTile() {
      T tile = null;
      if (row <= totalSquareRows) {
        tile = tiles.get(col).get(row);
        col++;                  // 1 col to the right
        if (col > totalSquareCols || col >= cols) {  // Reached Last Column
          col = startTile.getCol();  // Reset Col
          row++;                     // Next Row
        }
      }
      return tile;
    }
  }

  /**
   * An iterator returning tiles in a spiral starting at a center tile
   * The center tile is never included
   */
  private final class CircleIterator extends MapIterator {
    private final int minRange;
    private final int maxRange;
    private final Location center;
    private final List<T> circleTileList = new ArrayList<T>();
    private int index;
    private T nextTile;

    /**
     * @param center   The center location of the circle
     * @param minRange min Radius of the circle
     * @param maxRange max radius of the circle
     */
    public CircleIterator(Location center, int minRange, int maxRange) {
      this.minRange = minRange;
      this.maxRange = maxRange;
      this.center = center;

      if (center == null) {
        throw new IllegalArgumentException("Center tile cannot be null.");
      }

      // Get All tiles within Range.
      for (T tile : getSquareIterator(center, maxRange)) {
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

    public boolean inRange(Location tileToCheck) {
      int valid = Math.abs(tileToCheck.getRow() - center.getRow()) + Math.abs(tileToCheck.getCol() - center.getCol());
      return valid >= minRange && valid <= maxRange;
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
   * The height in pixels
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
   * @param loc the location(col,row) to retrieve the tile
   * @return the tile @ col, row or null if the location col, row coordinates are outside the map bounds
   */
  public T getTile(Location loc) {
    return getTile(loc.getCol(), loc.getRow());
  }

  /**
   * @param col column to retrieve the tile from
   * @param row row to retrieve the tile from
   * @return the tile @ col, row or null if the col, row coordinates are outside the map bounds
   */
  public T getTile(int col, int row) {
    if (isWithinMapBounds(col, row)) {
      return tiles.get(col).get(row);
    } else {
      return null;
    }
  }

  /**
   * Gets the tile adjacent to the baseTile in a given direction.
   *
   * @param direction A Direction where a tile should be retrieved from relative to baseTile.
   * @param baseTile  The tile of which we want to retrieve the adjacent tile.
   * @return Adjacent Tile or baseTile if direction == STILL.
   */
  public T getAdjacent(Location baseTile, Direction direction) {
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
      case STILL:
        break;
    }
    return getTile(col, row);
  }

  /**
   * What direction is the baseTile relative to the adjacentTile.
   * if <code>adjacentTile</code> is not adjacent or null Direction.STILL is returned.
   *
   * @return The Direction of adjacentTile relative to baseTile.
   */
  public Direction getDirectionTo(Location baseTile, Location adjacentTile) {
    Direction direction;
    if (getAdjacent(baseTile, Direction.NORTH) == adjacentTile) {
      direction = Direction.NORTH;
    } else if (getAdjacent(baseTile, Direction.EAST) == adjacentTile) {
      direction = Direction.EAST;
    } else if (getAdjacent(baseTile, Direction.SOUTH) == adjacentTile) {
      direction = Direction.SOUTH;
    } else if (getAdjacent(baseTile, Direction.WEST) == adjacentTile) {
      direction = Direction.WEST;
    } else {
      direction = Direction.STILL;
    }
    return direction;
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
