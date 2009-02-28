package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import tools.Args;

/**
 * Default Move strategy for units:
 */
public class DefaultMoveStrategy implements MovementCost {
  private Mover mover;
  private Map<Tile> map;

  public DefaultMoveStrategy(Mover mover, Map<Tile> map) {
    Args.checkForNull(mover);
    Args.checkForNull(map);
    this.mover = mover;
    this.map = map;
  }

  /**
   * Get the movement cost required to traverse a particular location with the current mover.
   *
   * @return the cost required for the current mover to traverse tile at (col, row).
   */
  public int getMovementCost(int col, int row) {
    Tile tile = map.getTile(col, row);

    if (map.isValid(tile)) {
      if (tile.isFogged()) {
        return getTerrainCost(tile.getTerrain());
      } else if (!tile.isFogged() && hasEnemyUnit(tile)) {
        return Terrain.IMPASSIBLE;
      } else {
        return getTerrainCost(tile.getTerrain());
      }
    } else {
      throw new IllegalArgumentException("cannot get movementCost for non valid tile " + tile);
    }
  }

  private boolean hasEnemyUnit(Location location) {
    Unit unit = map.getUnitOn(location);
    return unit != null && !unit.getOwner().isAlliedWith(mover.getOwner());
  }

  private int getTerrainCost(Terrain terrain) {
    int movementType = mover.getMovementType();
    return terrain.getMoveCost(movementType);
  }
}
