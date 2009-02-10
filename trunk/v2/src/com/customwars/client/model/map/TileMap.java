package com.customwars.client.model.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Data is stored as a 2-dimensional array containing references to Location objects
 * A List is used because it has better generics support
 * <br/>
 * Each location represents a tile square of height:tileSize and width:tileSize.
 * There are various ways to iterate over the tiles:
 * All tiles of this map or surrounding tiles around a center tile within a min/max range.
 * This class should not contain game specific objects, it only knows about Location objects.
 *
 * @author Stefan
 * @see Location
 */
public class TileMap<T extends Location> {
  private int tileSize;             // The square size in pixels
  private int cols, rows;
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
    initMap();
  }

  public void initMap() {
    tiles.clear();
    for (int col = 0; col < cols; col++) {
      ArrayList<T> rowList = new ArrayList<T>(rows);
      for (int row = 0; row < rows; row++) {
        rowList.add(null);
      }
      tiles.add(rowList);
    }
  }

  // ---------------------------------------------------------------------------
  // Iterate
  // ---------------------------------------------------------------------------
  public Iterable<T> getAllTiles() {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final WholeMapIterator m = new WholeMapIterator();

        return new Iterator<T>() {
          public boolean hasNext() {
            return m.hasNext();
          }

          public T next() {
            return m.next();
          }

          public void remove() {
            m.remove();
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
   * @param mapLocation the col, row where newTile should be placed
   * @param newTile     the tile to add to the map
   */
  public void setTile(Location mapLocation, T newTile) {
    int col = mapLocation.getCol();
    int row = mapLocation.getRow();
    setTile(col, row, newTile);
  }

  public void setTile(int col, int row, T newTile) {
    tiles.get(col).set(row, newTile);
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

  public int countTiles() {
    return rows * cols;
  }

  public int getWidth() {
    return cols * tileSize;
  }

  public int getHeight() {
    return rows * tileSize;
  }

  public T getRandomTile() {
    int randCol = (int) (Math.random() * cols);
    int randRow = (int) (Math.random() * rows);
    return getTile(randCol, randRow);
  }

  public T getTile(Location loc) {
    return getTile(loc.getCol(), loc.getRow());
  }

  public T getTile(int col, int row) {
    if (isWithinMapBounds(col, row)) {
      return tiles.get(col).get(row);
    } else {
      return null;
    }
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
