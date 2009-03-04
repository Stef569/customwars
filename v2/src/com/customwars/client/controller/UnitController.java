package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;

/**
 * Handles any input for 1 unit
 * This can be surrounding tile information, player clicks on a menu, Ai
 *
 * @author stefan
 */
public abstract class UnitController {
  Game game;
  Map map;
  Unit unit;
  MoveTraverse moveTraverse;
  ActionManager actionManager;

  protected UnitController(Game game, Unit unit, ActionManager actionManager, MoveTraverse moveTraverse) {
    this.game = game;
    this.map = game.getMap();
    this.unit = unit;
    this.actionManager = actionManager;
    this.moveTraverse = moveTraverse;
  }

  boolean canSelect(Location location) {
    return isUnitOn(location) && isUnitVisible() &&
            unit.isActive() && game.getActiveUnit() == null;
  }

  boolean canCapture(Tile clicked) {
    if (clicked == null) return false;

    Unit activeUnit = game.getActiveUnit();
    Terrain terrain = clicked.getTerrain();

    if (terrain instanceof City) {
      City city = (City) terrain;

      return isUnitOn(clicked) && isActiveUnitInGame() &&
              city.canBeCapturedBy(activeUnit) &&
              !city.getOwner().isAlliedWith(activeUnit.getOwner()) &&
              clicked.getLocatableCount() == 1;
    } else {
      return false;
    }
  }

  boolean canMove(Location from, Location to) {
    if (from == null || to == null) return false;

    return isUnitOn(from) && isActiveUnitInGame() &&
            unit.isActive() && game.getActiveUnit().isWithinMoveZone(to);
  }

  public boolean canWait(Tile selected) {
    return isActiveUnitInGame() && unit.isActive();
  }

  private boolean isActiveUnitInGame() {
    return game.getActiveUnit() == unit;
  }

  /**
   * Is this unit on the tile
   */
  boolean isUnitOn(Location tile) {
    return tile != null && tile.getLastLocatable() == unit;
  }

  boolean isInDirect(Tile selected) {
    boolean moved = selected != unit.getLocation();
    return moved && unit.getMinAttackRange() > 1;
  }

  /**
   * Is the tile and unit visible
   */
  boolean isUnitVisible() {
    Tile tile = (Tile) unit.getLocation();
    return !tile.isFogged() && !unit.isHidden();
  }
}
