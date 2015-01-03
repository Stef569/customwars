package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;

/**
 * Default Move strategy for units
 */
public class DefaultMoveStrategy implements MoveStrategy {
  private static final long serialVersionUID = 1L;
  private final Mover mover;

  public DefaultMoveStrategy() {
    this.mover = null;
  }

  public DefaultMoveStrategy(Mover mover) {
    this.mover = mover;
  }

  public MoveStrategy newInstance(Mover mover) {
    return new DefaultMoveStrategy(mover);
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
    Unit unit = (Unit) tile.getLastLocatable();
    boolean hasHiddenUnit = unit != null && unit.isHidden();

    if (tile.isFogged() || hasHiddenUnit) {
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
