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

import java.util.ArrayList;
import java.util.List;

/**
 * Handles any input for 1 unit
 * This can be surrounding tile information, clicks in a menu, Ai
 *
 * @author stefan
 */
public abstract class UnitController {
  Game game;
  Map<Tile> map;
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

  boolean canWait(Tile selected) {
    if (selected == null || !isActiveUnitInGame() || !unit.isActive()) return false;

    if (selected.isFogged()) {
      return true;
    } else {
      return selected.getLocatableCount() == 1;
    }
  }

  boolean canSupply(Tile selected) {
    return selected != null && isActiveUnitInGame() && unit.isActive() &&
            !game.getMap().getSuppliablesInRange(game.getActiveUnit()).isEmpty();
  }

  /**
   * Can the activeUnit be added to the transport
   */
  boolean canLoad(Tile selected) {
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

  boolean canStartDrop(Tile selected) {
    List<Location> emptyTiles = getEmptyAjacentTiles(selected);
    Unit activeUnit = game.getActiveUnit();

    return selected != null && !emptyTiles.isEmpty() &&
            activeUnit.canTransport() && activeUnit.getLocatableCount() > 0;
  }

  private List<Location> getEmptyAjacentTiles(Tile clicked) {
    List<Location> emptyTiles = new ArrayList<Location>();
    for (Location location : game.getMap().getSurroundingTiles(clicked, 1, 1)) {
      if (location.getLocatableCount() == 0) {
        emptyTiles.add(location);
      }
    }
    return emptyTiles;
  }

  boolean canDrop(Tile selected) {
    Unit transporter = game.getActiveUnit();
    return isActiveUnitInGame() && unit.isActive() && isUnitVisible() &&
            selected != null && transporter.getLocatableCount() > 0;
  }

  /**
   * @param origUnitLocation Where the unit was located(used to determine if the unit has moved)
   * @param selected         the location that has been clicked on
   * @return if this unit can attack
   */
  boolean canStartAttack(Tile origUnitLocation, Tile selected) {
    if (!isActiveUnitInGame() || isDirectUnitMoved(origUnitLocation)) return false;

    Unit activeUnit = game.getActiveUnit();
    List<Unit> enemiesInRange = game.getMap().getEnemiesInRangeOf(activeUnit);

    if (selected.isFogged()) {
      return !enemiesInRange.isEmpty();
    } else {
      return !enemiesInRange.isEmpty() && selected.getLocatableCount() == 1;
    }
  }

  boolean canAttack(Tile selected) {
    if (!isActiveUnitInGame() || selected == null || !isUnitVisible()) return false;

    Unit selectedUnit = game.getMap().getUnitOn(selected);
    return selectedUnit != null;
  }

  boolean isActiveUnitInGame() {
    return game.getActiveUnit() == unit;
  }

  /**
   * Is this unit on the tile
   */
  boolean isUnitOn(Location tile) {
    return tile != null && tile.getLastLocatable() == unit;
  }

  /**
   * @param selected original unit location
   * @return if this unit is a direct fire type and has it moved
   */
  boolean isDirectUnitMoved(Location selected) {
    boolean moved = selected != unit.getLocation();
    return moved && unit.getMinAttackRange() > 1;
  }

  /**
   * Is the tile and the unit on it visible
   */
  boolean isUnitVisible() {
    Tile tile = (Tile) unit.getLocation();
    return !tile.isFogged() && !unit.isHidden();
  }
}
