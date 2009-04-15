package com.customwars.client.controller;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.state.InGameContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles any input for 1 unit
 * This can be surrounding tile information, clicks in a menu, Ai
 * Note that the unit is teleported to the select tile in each method.
 *
 * @author stefan
 */
public abstract class UnitController {
  Game game;
  Map<Tile> map;
  Unit unit;
  MoveTraverse moveTraverse;

  protected UnitController(Unit unit, InGameContext inGameContext) {
    this.game = inGameContext.getGame();
    this.map = game.getMap();
    this.unit = unit;
    this.moveTraverse = inGameContext.getMoveTraverse();
  }

  boolean canSelect(Location selected) {
    return isUnitOn(selected) && isUnitVisible() &&
            unit.isActive() && game.getActiveUnit() == null;
  }

  boolean canCapture(Tile selected) {
    Terrain terrain = selected.getTerrain();

    if (terrain instanceof City) {
      City city = (City) terrain;
      boolean validUnit = isUnitOn(selected) && isActiveUnitInGame();
      boolean canCapture = city.canBeCapturedBy(unit) && !city.getOwner().isAlliedWith(unit.getOwner());

      if (selected.isFogged()) {
        return validUnit && canCapture;
      } else {
        return validUnit && canCapture && selected.getLocatableCount() == 1;
      }
    } else {
      return false;
    }
  }

  boolean canMove(Location from, Location to) {
    return from != null || to != null && unit.isWithinMoveZone(to);
  }

  boolean isValidUnitLocation(Tile selected) {
    return isUnitVisible() && isUnitOn(selected);
  }

  boolean isActiveUnit() {
    return isActiveUnitInGame() && unit.isActive();
  }

  boolean canWait(Tile selected) {
    return selected.isFogged() || selected.getLocatableCount() == 1;
  }

  boolean canSupply(Tile selected) {
    return !game.getMap().getSuppliablesInRange(unit).isEmpty();
  }

  /**
   * Can the unit be added to the transport
   * The unit and the transport are both on the same tile
   * Transport is the first unit
   */
  boolean canLoad(Tile selected) {
    Unit transporter;
    Locatable locatable = selected.getLocatable(0);
    if (locatable instanceof Unit) {
      transporter = (Unit) locatable;
    } else {
      return false;
    }

    return transporter.canAdd(unit) &&
            transporter.getOwner() == unit.getOwner();
  }

  boolean canStartDrop(Tile center, Tile selected, int dropCount) {
    List<Location> emptyTiles = getEmptyAjacentTiles(center);

    return !emptyTiles.isEmpty() &&
            unit.canTransport() && dropCount > 0 && dropCount <= unit.getLocatableCount();
  }

  private List<Location> getEmptyAjacentTiles(Tile selected) {
    List<Location> emptyTiles = new ArrayList<Location>();
    for (Location location : game.getMap().getSurroundingTiles(selected, 1, 1)) {
      if (location.getLocatableCount() == 0) {
        emptyTiles.add(location);
      }
    }

    return emptyTiles;
  }

  boolean canDrop(Tile selected) {
    Unit transporter = game.getActiveUnit();
    if (selected.isFogged()) {
      return transporter.getLocatableCount() > 0;
    } else {
      return transporter.getLocatableCount() > 0 && selected.getLocatableCount() == 0;
    }
  }

  /**
   * @param origUnitLocation Where the unit was located(used to determine if the unit has moved)
   * @param selected         the location that has been clicked on
   * @return if this unit can attack
   */
  boolean canStartAttack(Location origUnitLocation, Tile selected) {
    if (isDirectUnitMoved(origUnitLocation)) return false;

    Unit activeUnit = game.getActiveUnit();
    List<Unit> enemiesInRange = game.getMap().getEnemiesInRangeOf(activeUnit);

    return !enemiesInRange.isEmpty();
  }

  boolean canAttack(Tile selected) {
    return game.getMap().getUnitOn(selected) != null;
  }

  /**
   * The join conditions are:
   * Our unit is on the same tile as the target, both units are of the same type
   * The two units must have the same owner
   * The target unit must have 50% or less HP
   * Both units can use the join command
   * If the unit-type of the units is Transport, the two unit must not have any units loaded
   */
  boolean canJoin(Tile selected) {
    // Get the target we want to join with
    Unit target = (Unit) selected.getLocatable(0);

    if (target != null && target != unit) {
      if (selected.getLastLocatable() == unit && unit.getID() == target.getID()) {
        if (target.getOwner() == unit.getOwner() && target.getHpPercentage() <= 50) {
          if (target.canJoin() && unit.canJoin()) {
            if (target.canTransport()) {
              if (target.getLocatableCount() == 0 && unit.getLocatableCount() == 0) {
                return true;
              }
            } else {
              return true;
            }
          }
        }
      }
    }
    return false;
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
   * @param originalLocation original unit location
   * @return if this unit is a direct fire type and has it moved
   */
  boolean isDirectUnitMoved(Location originalLocation) {
    boolean moved = originalLocation != unit.getLocation();
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
