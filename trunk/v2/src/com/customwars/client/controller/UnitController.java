package com.customwars.client.controller;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.gameobject.UnitStats;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.state.InGameContext;

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

  protected UnitController(Unit unit, InGameContext gameContext) {
    this.game = gameContext.getGame();
    this.map = game.getMap();
    this.unit = unit;
    this.moveTraverse = gameContext.getMoveTraverse();
  }

  boolean canSelect(Location selected) {
    Player activePlayer = game.getActivePlayer();

    return isUnitOn(selected) && isUnitVisibleTo(activePlayer) &&
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

  /**
   * Can the transport drop any ground units
   * #1 This controller controls a unit with transport abilities
   * #2 There is at least 1 free adjacent tile
   * #3 There is at least 1 ground unit in the transport
   *
   * @return true if the transport can drop at least 1 ground unit
   */
  boolean canStartDrop() {
    List<Location> emptyTiles = map.getFreeDropLocations(unit);

    int groundUnitsInTransportCount = 0;
    for (int i = 0; i < unit.getLocatableCount(); i++) {
      Unit unitInTransport = (Unit) unit.getLocatable(i);

      if (unitInTransport.getArmyBranch() == ArmyBranch.LAND) {
        groundUnitsInTransportCount++;
      }
    }

    boolean isTransportingUnit = unit.getStats().canTransport();
    boolean atleast1FreeAdjacentTile = !emptyTiles.isEmpty();
    boolean atLeast1GroundUnit = groundUnitsInTransportCount > 0;

    return isTransportingUnit && atleast1FreeAdjacentTile && atLeast1GroundUnit;
  }


  boolean canAirplaneTakeOffFromUnit() {
    Unit unitToTakeOff = null;

    if (unit.getLocatableCount() > 0) {
      unitToTakeOff = (Unit) unit.getLastLocatable();
    }

    return unit.getStats().canTransport() && unitToTakeOff != null && unitToTakeOff.getArmyBranch() == ArmyBranch.AIR;
  }

  /**
   * Can this transporting unit drop a unit at dropIndex on the given tile
   *
   * @param tile      The tile the unit is going to be dropped to
   * @param dropIndex The index of the unit in the transport(0 based)
   * @return true If this transport can drop the unit in the transport at the given index
   *         on the given tile
   */
  boolean canDrop(Tile tile, int dropIndex) {
    if (unit.getStats().canTransport() && unit.getLocatableCount() > dropIndex) {
      return map.isFreeDropLocation(tile, unit);
    } else {
      return false;
    }
  }

  /**
   * @param origUnitLocation Where the unit was located(used to determine if the unit has moved)
   * @return if this unit can attack
   */
  boolean canStartAttack(Location origUnitLocation) {
    if (isDirectUnitMoved(origUnitLocation)) return false;

    Unit activeUnit = game.getActiveUnit();
    List<Unit> enemiesInRange = game.getMap().getEnemiesInRangeOf(activeUnit);

    return !enemiesInRange.isEmpty();
  }

  boolean canAttack(Tile selected) {
    return map.getUnitOn(selected) != null;
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
    UnitStats unitStats = unit.getStats();
    UnitStats targetStats = target.getStats();

    if (target != null && target != unit) {
      if (selected.getLastLocatable() == unit && unitStats.getID() == targetStats.getID()) {
        if (target.getOwner() == unit.getOwner() && target.getHpPercentage() <= 50) {
          if (targetStats.canJoin() && unitStats.canJoin()) {
            if (targetStats.canTransport()) {
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

  boolean canLaunchRocket(Tile selected) {
    City city = map.getCityOn(selected);
    return city != null && city.canLaunchRocket(unit) && isUnitOn(city.getLocation());
  }

  boolean canTransformTerrain(Tile selected) {
    return unit.getStats().canTransformTerrain(selected.getTerrain());
  }

  public boolean canFlare(Tile selected) {
    return unit.getAttackZone().contains(selected);
  }

  boolean canFireFlare(Tile from) {
    return unit.getStats().canFlare() && map.isFogOfWarOn() &&
      !isDirectUnitMoved(from) && unit.getAvailableWeapon().hasAmmoLeft();
  }

  boolean canBuildCity(Tile selected) {
    return unit.getStats().canBuildCityOn(selected.getTerrain());
  }

  boolean canBuildUnit() {
    return unit.canBuildUnit();
  }

  boolean canDive() {
    return unit.getStats().canDive() && unit.getUnitState() != UnitState.SUBMERGED;
  }

  boolean canSurface() {
    // if a unit can dive it can also surface...
    return unit.getStats().canDive() && unit.getUnitState() == UnitState.SUBMERGED;
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
   * @return if this unit is a direct fire type and has it moved
   */
  boolean isDirectUnitMoved(Location originalLocation) {
    boolean moved = originalLocation != unit.getLocation();
    return moved && unit.getAttackRange().getMinRange() > 1;
  }

  /**
   * Is the tile and the unit on it visible for the given player
   *
   * #1 Fogged tiles are not visible
   * #2 Hidden enemy units are not visible
   */
  boolean isUnitVisibleTo(Player player) {
    Tile tile = (Tile) unit.getLocation();
    boolean alliedUnit = unit.getOwner().isAlliedWith(player);
    boolean hiddenEnemyUnit = !alliedUnit && unit.isHidden();

    return !tile.isFogged() && !hiddenEnemyUnit;
  }
}
