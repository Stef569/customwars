package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import tools.Args;

/**
 * Default Move strategy for units:
 */
public class DefaultMoveStrategy implements MoveStrategy {
  private Mover mover;

  public DefaultMoveStrategy(Mover mover) {
    Args.checkForNull(mover);
    this.mover = mover;
  }

  public int getMoveCost(Location location) {
    return getMoveCost((Tile) location);
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
