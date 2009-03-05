package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
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

  boolean canSelect(Location selected) {
    return isUnitOn(selected) && isUnitVisible() &&
            unit.isActive() && game.getActiveUnit() == null;
  }

  boolean canCapture(Tile selected) {
    if (selected == null) return false;

    Unit activeUnit = game.getActiveUnit();
    Terrain terrain = selected.getTerrain();

    if (terrain instanceof City) {
      City city = (City) terrain;

      return isUnitOn(selected) && isActiveUnitInGame() &&
              city.canBeCapturedBy(activeUnit) &&
              !city.getOwner().isAlliedWith(activeUnit.getOwner()) &&
              selected.getLocatableCount() == 1;
    } else {
      return false;
    }
  }

  boolean canMove(Location from, Location to) {
    return (from != null || to != null) && isUnitOn(from) && isActiveUnitInGame() &&
            unit.isActive() && game.getActiveUnit().isWithinMoveZone(to);

  }

  public boolean canWait(Tile selected) {
    return selected != null && isActiveUnitInGame() && unit.isActive();
  }

  public boolean canSupply(Tile selected) {
    return selected != null && isActiveUnitInGame() && unit.isActive() &&
            !game.getMap().getSuppliablesInRange(game.getActiveUnit()).isEmpty();
  }

  /**
   * Can the activeUnit be added to the transport
   */
  public boolean canLoad(Tile selected) {
    if (selected == null) return false;

    Unit transporter;
    Locatable locatable = selected.getLocatable(0);
    if (locatable instanceof Unit) {
      transporter = (Unit) locatable;
    } else {
      return false;
    }

    return isActiveUnitInGame() && unit.isActive() && isUnitVisible() &&
            transporter.canTransport(unit.getMovementType()) &&
            transporter.getOwner() == unit.getOwner();
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