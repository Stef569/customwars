package com.customwars.client.model.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class MapIterators<T extends Location> {
  private final TileMap<T> tileMap;

  public MapIterators(TileMap<T> tileMap) {
    this.tileMap = tileMap;
  }

  /**
   * Iterate over each tile in the map
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

  /**
   * Return all the valid tiles surrounding <tt>center</tt> within the <tt>min, max</tt> range.
   * The center is not included</p>
   * Based on the range an AdjacentIterator or CircleIterator is returned.
   * if center is not valid then an empty Iterator is returned
   *
   * @param center The tile that is the center of the tiles to iterate over.
   * @param range  The range in which we need to start iterating relative to the center tile.
   * @return The tiles surrounding the given center tile.
   */
  public Iterable<T> getSurroundingTiles(Location center, Range range) {
    if (!tileMap.isValid(center) || range.getMinRange() == 0 || range.getMaxRange() == 0) {
      return Collections.emptyList();
    }

    if (range.getMaxRange() == 1) {
      return getAdjacentIterator(center);
    } else {
      return getSpiralIterator(center, range);
    }
  }

  public Iterable<T> getAdjacentIterator(final Location center) {
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

  public Iterable<T> getSpiralIterator(final Location center, final Range range) {
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
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
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
   * The center tile is not included, and all returned positions are valid {@link com.customwars.client.model.map.TileMap#isValid(com.customwars.client.model.map.Location)}
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
      return row < tileMap.getRows();
    }

    /**
     * @return Next tile
     * @throws java.util.NoSuchElementException
     *          if the iterator ran off the map bounds
     */
    @Override
    public T nextTile() throws NoSuchElementException {
      if (row < tileMap.getRows()) {
        T tile = tileMap.getTile(col, row);
        col++;                  // 1 col to the right
        if (col == tileMap.getCols()) {      // Reached Last Column
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
   * The center tile is not included, and all returned positions are valid {@link com.customwars.client.model.map.TileMap#isValid(com.customwars.client.model.map.Location)}
   */
  private final class AdjacentIterator extends MapIterator {
    private static final int MAX_SURROUNDING_TILE_COUNT = 4;
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
        nextTile = tileMap.getRelativeTile(center, dir);

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
     * @throws java.util.NoSuchElementException
     *          if last tile already returned
     */
    @Override
    public T nextTile() throws NoSuchElementException {
      index += 1;
      if (tileMap.isWithinMapBounds(nextTile))
        return nextTile;
      else {
        throw new NoSuchElementException("Iterator exhausted");
      }
    }
  }

  /**
   * An iterator returning Tiles in the form of a square around the center tile within a given range.
   * The center tile is not included, and all returned positions are valid {@link com.customwars.client.model.map.TileMap#isValid(com.customwars.client.model.map.Location)}
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

      leftTopTile = tileMap.getTile(center.getCol() - colOffset, center.getRow() - rowOffset);

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
        tile = tileMap.getRelativeTile(tile, direction);

        if (!tileMap.isWithinMapBounds(tile)) {
          break;
        }
      }
      return offset;
    }

    private void buildSquare() {
      square = new LinkedList<T>();
      while (hasNextRow()) {
        T tile = tileMap.getTile(col++, row);

        if (tile != null && tile != center) {
          square.add(tile);
        }

        if (!hasNextCol()) {
          gotoNextRow();
        }
      }
    }

    private boolean hasNextRow() {
      return row < totalSquareRows && row < tileMap.getRows();
    }

    private boolean hasNextCol() {
      return col < totalSquareCols && col < tileMap.getCols();
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
   * The center tile is not included, and all returned positions are valid {@link com.customwars.client.model.map.TileMap#isValid(com.customwars.client.model.map.Location)}
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
        if (tileMap.isValid(tile) && inRange(tile)) {
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
      return tileMap.isValid(nextTile) && index != circleTileList.size();
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
      int distance = TileMap.getDistanceBetween(tile, center);
      return range.isInRange(distance);
    }
  }
}