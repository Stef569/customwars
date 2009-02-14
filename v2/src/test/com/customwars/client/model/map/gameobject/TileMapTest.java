package test.com.customwars.client.model.map.gameobject;

import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import test.testData.HardCodedGame;

/**
 * Test functions in the TileMap2D class
 *
 * @author Stefan
 * @since 2.0
 */
public class TileMapTest {
  private TileMap<Tile> map;

  @Before
  public void beforeEachTest() {
    map = HardCodedGame.getMap();
  }

  // Check If getAllTiles realy Returns AllTiles
  // by counting them and checking them for notNull
  // All Tiles should at least have a terrain
  @Test
  public void loopThroughAllTiles() {
    int nTiles = 0;
    for (Tile t : map.getAllTiles()) {
      Assert.assertNotNull(t);
      Assert.assertNotNull(t.getTerrain());
      nTiles++;
    }

    // The real amount should match the loop amount
    int realCount = map.countTiles();
    Assert.assertEquals(realCount, nTiles);
  }

  @Test
  public void isWithinMapBounds() {
    Location leftCorner = map.getTile(0, 0);
    Location tile = map.getTile(0, 1);
    Assert.assertFalse(map.isValid(map.getTile(-1, -1)));
    Assert.assertTrue(map.isValid(leftCorner));
    Assert.assertTrue(map.isAdjacent(leftCorner, tile));
  }

  @Test
  public void testIsTileAdjacent() {
    Location left = map.getTile(0, 1);
    Location up = map.getTile(1, 0);
    Location right = map.getTile(2, 1);
    Location down = map.getTile(1, 2);
    Location center = map.getTile(1, 1);

    // All tiles touch the center tile
    Assert.assertTrue(map.isAdjacent(left, center));
    Assert.assertTrue(map.isAdjacent(up, center));
    Assert.assertTrue(map.isAdjacent(right, center));
    Assert.assertTrue(map.isAdjacent(down, center));

    // These Tiles don't touch
    Assert.assertFalse(map.isAdjacent(down, up));
    Assert.assertFalse(map.isAdjacent(left, right));
    Assert.assertFalse(map.isAdjacent(up, left));
    Assert.assertFalse(map.isAdjacent(up, right));
    Assert.assertFalse(map.isAdjacent(up, down));
  }

  /**
   * Loop through all tiles that touch the baseTile 5,0
   * North = 4,-1  == excluded
   * South = 5,1  == included
   * East = 6,0   == included
   * West = 4,0   == included
   * Since the topEdge tile is the top row, the Iterator should ignore the north side
   * and not return it.
   */
  @Test
  public void adjacentIteratorEdgeOfMap1() {
    Location topEdge = map.getTile(5, 0);
    for (Location t : map.getSurroundingTiles(topEdge, 1, 1)) {
      boolean eastTileIsIncluded = t.getCol() == 6 && t.getRow() == 0;
      boolean southTileIsIncluded = t.getCol() == 5 && t.getRow() == 1;
      boolean westTileIsIncluded = t.getCol() == 4 && t.getRow() == 0;

      Assert.assertNotNull(t);
      Assert.assertEquals(true, eastTileIsIncluded || westTileIsIncluded || southTileIsIncluded);
    }
  }

  /**
   * Loop through all tiles that touch the leftTop tile 0,0
   * Iterating around the left top should return 2 Tiles east and south.
   * Ignoring North and West
   */
  @Test
  public void adjacentIteratorEdgeOfMap2() {
    Location leftTop = map.getTile(0, 0);
    for (Location t : map.getSurroundingTiles(leftTop, 1, 1)) {
      boolean eastTile = t.getCol() == 1 && t.getRow() == 0;
      boolean southTile = t.getCol() == 0 && t.getRow() == 1;
      Assert.assertNotNull(t);
      Assert.assertEquals(true, eastTile || southTile);
    }
  }

  @Test
  public void getDirectionTo() {
    Direction dir;
    Location baseTile = map.getTile(5, 5);
    Location left = map.getTile(4, 5);
    Location right = map.getTile(6, 5);
    Location up = map.getTile(5, 4);
    Location down = map.getTile(5, 6);

    // Connected Tiles should return correct compass direction
    dir = map.getDirectionTo(baseTile, left);
    Assert.assertEquals(dir, Direction.WEST);

    dir = map.getDirectionTo(baseTile, right);
    Assert.assertEquals(dir, Direction.EAST);

    dir = map.getDirectionTo(baseTile, up);
    Assert.assertEquals(dir, Direction.NORTH);

    dir = map.getDirectionTo(baseTile, down);
    Assert.assertEquals(dir, Direction.SOUTH);

    dir = map.getDirectionTo(baseTile, baseTile);
    Assert.assertEquals(dir, Direction.STILL);

    // Not connected should return STILL
    dir = map.getDirectionTo(left, right);
    Assert.assertEquals(dir, Direction.STILL);
  }
}
