package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Direction;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Dijkstra class - Pathfinder algorithm
 * last updated on January 29, 2008, 2:08 AM
 * status: completed
 * dependancies:
 * created by Benjamin Islip
 * Revision:
 * Stef 31-03-08: Replaced integer directions by Direction enum
 */
public class Dijkstra {
  private int mapWidth, mapHeight;
  private int maxMovement;            // the maximum amount of nodes to be traversed
  private int startX, startY;
  private int distanceMap[][];        // the distance every tile is from the target
  private Direction pathMap[][];      // tells each tile the best step toward the target
  private boolean checkMap[][];       // a tile is checked (true) when it has been procesed
  private MovementCost movementCosts; // a class that calculates movement costs

  /**
   * @param x number of columns
   * @param y number of rows
   */
  public Dijkstra(int x, int y) {
    mapWidth = x;
    mapHeight = y;

    distanceMap = new int[mapWidth][mapHeight];
    pathMap = new Direction[mapWidth][mapHeight];
    checkMap = new boolean[mapWidth][mapHeight];
  }

  /**
   * This is the business end of the class, it must be run before any
   * get functions can be used. Tip: If multiple paths need to be calculated
   * to/from a single point, try using calculate only once on that point
   * to boost efficiency.
   *
   * @param x        x coordinate of unit.
   * @param y        y coordinate of unit.
   * @param movement the movement type of the vehicle.
   */
  public void calculate(int x, int y, int movement) {
    if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
      return;

    // initialisations
    startX = x;
    startY = y;
    this.maxMovement = movement;
    for (int i = 0; i < mapHeight; i++) {
      for (int j = 0; j < mapWidth; j++) {
        distanceMap[j][i] = -1;
        pathMap[j][i] = Direction.STILL;
        checkMap[j][i] = false;
      }
    }
    distanceMap[startX][startY] = 0;

    // run
    boolean nodesLeft = true;
    while (nodesLeft) {
      // v is our iterator, it points to the current node.
      Point v = null;

      // look for smallest node
      for (int j = 0; j < mapWidth; j++)
        for (int k = 0; k < mapHeight; k++)
          if (!checkMap[j][k] && distanceMap[j][k] != -1) // unselected and exists
          {
            if (v == null)
              v = new Point(j, k);
            else if (distanceMap[j][k] < distanceMap[v.x][v.y])
              v.setLocation(j, k);
          }

      if (v != null) // if we have a node
      {
        // look in each direction for a shorter path
        if (v.x + 1 < mapWidth)
          checkDir(v.x, v.y, v.x + 1, v.y, Direction.EAST);
        if (v.x - 1 >= 0)
          checkDir(v.x, v.y, v.x - 1, v.y, Direction.WEST);
        if (v.y + 1 < mapHeight)
          checkDir(v.x, v.y, v.x, v.y + 1, Direction.SOUTH);
        if (v.y - 1 >= 0)
          checkDir(v.x, v.y, v.x, v.y - 1, Direction.NORTH);

        checkMap[v.x][v.y] = true; // node traversed
      } else
        nodesLeft = false;
    }
  }

  private void checkDir(int x, int y, int nx, int ny, Direction dir) {
    int movementCost = movementCosts.getMovementCost(nx, ny);

    if (!checkMap[nx][ny] && movementCost != 0 && distanceMap[x][y] + movementCost <= maxMovement) // if not checked and tile is traversable
    {
      if (distanceMap[nx][ny] == -1 || distanceMap[x][y] + movementCost < distanceMap[nx][ny]) // if current path is shorter
      {
        distanceMap[nx][ny] = distanceMap[x][y] + movementCost; // edit path
        pathMap[nx][ny] = dir; // could be changed too pathMap[x][y]
      }
    }
  }

  /**
   * @param x x coordinate of the destination.
   * @param y y coordinate of the destination.
   * @return a vector containing the directions to the destination (x, y).
   *         returns null if no path can be found.
   */
  public List<Direction> getPath(int x, int y) {
    if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
      return null;

    List<Direction> path = new ArrayList<Direction>();

    while (!(x == startX && y == startY)) {
      path.add(0, pathMap[x][y]);
      switch (pathMap[x][y]) {

        case EAST:
          x--;
          break;
        case NORTH:
          y++;
          break;
        case SOUTH:
          y--;
          break;
        case WEST:
          x++;
          break;
        default:
          return null;
      }
    }
    return path;

  }

  /**
   * @param x destination.
   * @param y destination.
   * @return a vector containing all the positions to the destination (x, y).
   *         returns null if no route can be found.
   */
  public List<Point> getRoute(int x, int y) {
    if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight)
      return null;

    List<Point> path = new ArrayList<Point>();

    path.add(new Point(x, y));    // Store startValue
    while (!(x == startX && y == startY)) {
      if (pathMap[x][y] == Direction.EAST)
        x--;
      else if (pathMap[x][y] == Direction.WEST)
        x++;
      else if (pathMap[x][y] == Direction.SOUTH)
        y--;
      else if (pathMap[x][y] == Direction.NORTH)
        y++;
      else
        return null;
      path.add(new Point(x, y));
    }

    Collections.reverse(path);
    return path;
  }

  /**
   * todo: make this method more effecient by making the calculate method add the values.
   *
   * @return a vector containing all the avaliable movement options.
   */
  public List<Point> getMoveZone() {
    List<Point> zone = new ArrayList<Point>();

    for (int row = 0; row < mapHeight; row++) {
      for (int col = 0; col < mapWidth; col++) {
        if (distanceMap[col][row] != -1) {
          zone.add(new Point(col, row));
        }
      }
    }
    return zone;
  }

  /**
   * returns true if tile(x, y) is a possible movement option.
   * Tip: use getMoveZone() for all the avaliable movement options.
   *
   * @return whether the unit can move to the location.
   */
  public boolean canMoveTo(int x, int y) {
    return !(x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) && distanceMap[x][y] != -1;
  }

  /**
   * The move costs to use in Dijkstra
   *
   * @param movementCosts
   */
  public void setMovementCosts(MovementCost movementCosts) {
    this.movementCosts = movementCosts;
  }
}
