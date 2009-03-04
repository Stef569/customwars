package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import tools.Args;

/**
 * Default Move strategy for units
 * The Pathfinder will use getMoveCost(Tile) to build paths and zones.
 */
public class DefaultMoveStrategy implements MoveStrategy {
  private Mover mover;

  public DefaultMoveStrategy(Mover mover) {
    Args.checkForNull(mover);
    this.mover = mover;
  }

  /**
   * If a tile cannot be traversed over then Terrain.IMPASSIBLE should be returned.
   * moveCosts are within bounds of 0 and Terrain.IMPASSIBLE
   * Each terrain has a moveCost for a mover
   */
  public int getMoveCost(Location tile) {
    return getMoveCost((Tile) tile);
  }

  public int getMoveCost(Tile tile) {
    if (tile.isFogged()) {
      return getTerrainCost(tile.getTerrain());
    } else if (!tile.isFogged() && hasEnemyUnit(tile)) {
      return Terrain.IMPASSIBLE;
    } else {
      return getTerrainCost(tile.getTerrain());
    }
  }

  private boolean hasEnemyUnit(Tile t) {
    Unit unit;
    if (t.getLastLocatable() instanceof Unit) {
      unit = (Unit) t.getLastLocatable();
      return !unit.getOwner().isAlliedWith(mover.getOwner());
    } else {
      return false;
    }
  }

  private int getTerrainCost(Terrain terrain) {
    int movementType = mover.getMovementType();
    return terrain.getMoveCost(movementType);
  }
}
