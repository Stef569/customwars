package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.Args;

/**
 * Default Move strategy for units
 */
public class DefaultMoveStrategy implements MoveStrategy {
  private Mover mover;

  public DefaultMoveStrategy(Mover mover) {
    Args.checkForNull(mover);
    this.mover = mover;
  }

  public int getMoveCost(Location tile) {
    return getMoveCost((Tile) tile);
  }

  /**
   * What is the cost to move over the given tile
   * moveCosts are within inclusive bounds of Terrain.MIN_MOVE_COST and Terrain.IMPASSIBLE
   * If a tile cannot be traversed over then Terrain.IMPASSIBLE is returned.
   */
  public int getMoveCost(Tile tile) {
    if (tile.isFogged()) {
      return getTerrainCost(tile.getTerrain());
    } else if (!tile.isFogged() && mover.hasTrapperOn(tile)) {
      return Terrain.IMPASSIBLE;
    } else {
      return getTerrainCost(tile.getTerrain());
    }
  }

  private int getTerrainCost(Terrain terrain) {
    int movementType = mover.getMovementType();
    return terrain.getMoveCost(movementType);
  }
}
