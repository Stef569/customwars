package com.customwars.client.controller;

import com.customwars.client.model.fight.Defender;
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
  Map map;
  Unit unit;
  MoveTraverse moveTraverse;

  protected UnitController(Unit unit, InGameContext gameContext) {
    this.game = gameContext.getObj(Game.class);
    this.map = game.getMap();
    this.unit = unit;
    this.moveTraverse = gameContext.getObj(MoveTraverse.class);
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
      boolean canCapture = city.canBeCapturedBy(unit) && !city.isAlliedWith(unit.getOwner());

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
    Unit unit = (Unit) selected.getLocatable(0);
    return selected.isFogged() || unit.isHidden() || selected.getLocatableCount() == 1;
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
   * Can the transport drop any units
   * #1 This controller controls a unit with transport abilities
   * #2 There is at least 1 free tile within the drop range
   * #3 There is at least 1 unit in the transport
   * #4 At least 1 unit in the transport can be unloaded on an adjacent tile.
   *
   * @return true if the transport can drop at least 1 unit
   */
  boolean canStartDrop() {
    List<Location> freeDropLocations = map.getFreeDropLocations(unit);

    boolean isTransportingUnit = unit.getStats().canTransport();
    boolean atleast1FreeDropTile = !freeDropLocations.isEmpty();
    boolean atLeast1UnitToDrop = unit.getLocatableCount() > 0;

    return isTransportingUnit && atleast1FreeDropTile && atLeast1UnitToDrop;
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
      Unit unitToDrop = (Unit) unit.getLocatable(dropIndex);
      return map.isFreeDropLocation(unit, unitToDrop, tile);
    } else {
      return false;
    }
  }

  /**
   * @param origUnitLocation Where the unit was located(used to determine if the unit has moved)
   * @return if this unit can attack
   */
  boolean canStartAttack(Location origUnitLocation) {
    if (isInDirectUnitMoved(origUnitLocation)) return false;

    Unit activeUnit = game.getActiveUnit();
    List<Defender> enemiesInRange = game.getMap().getEnemiesInRangeOf(activeUnit);

    return !enemiesInRange.isEmpty();
  }

  boolean canAttackUnit(Tile selected) {
    return map.getUnitOn(selected) != null;
  }

  boolean canAttackCity(Tile selected) {
    City city = map.getCityOn(selected);
    return city != null && city.canBeDestroyed();
  }

  /**
   * The join conditions are:
   * Our unit is on the same tile as the target, both units are of the same type
   * The two units must have the same owner
   * The target unit must have 9HP or less.
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
        if (target.getOwner() == unit.getOwner() && target.getHp() <= 9) {
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
    boolean moved = from != unit.getLocation();
    boolean hasFlareAbility = unit.getStats().canFlare();
    boolean ammoLeft = unit.hasPrimaryWeapon() && unit.getPrimaryWeapon().hasAmmoLeft();
    boolean fogON = map.isFogOfWarOn();
    return hasFlareAbility && fogON && !moved && ammoLeft;
  }

  boolean canBuildCity(Tile selected) {
    return unit.canConstructCityOn(selected.getTerrain());
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

  boolean canLoadCO(Location originalLocation) {
    City city = map.getCityOn(unit.getLocation());

    if (city != null) {
      boolean coLoaded = unit.getOwner().isCOLoaded();
      boolean canAffordCO = unit.getOwner().isWithinBudget(unit.getPrice() / 2);
      boolean onCity = city.getLocation().equals(originalLocation);
      boolean onFriendlyCity = city.isOwnedBy(unit.getOwner()) && onCity;
      boolean cityCanProduceCO = city.canBuild(unit) || city.isHQ();

      return !coLoaded && canAffordCO && onFriendlyCity && cityCanProduceCO;
    } else {
      return false;
    }
  }

  boolean canDoPower() {
    return unit.isCoOnBoard() && unit.getOwner().getCO().canDoPower();
  }

  boolean canDoSuperPower() {
    return unit.isCoOnBoard() && unit.getOwner().getCO().canDoSuperPower();
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
   * @return if this unit is a indirect fire type and has it moved
   */
  boolean isInDirectUnitMoved(Location originalLocation) {
    boolean moved = originalLocation != unit.getLocation();
    return moved && unit.isInDirect();
  }

  /**
   * Is the tile and the unit on it visible for the given player
   * <p/>
   * #1 Fogged tiles are not visible
   * #2 Hidden enemy units are not visible
   */
  boolean isUnitVisibleTo(Player player) {
    Tile tile = (Tile) unit.getLocation();
    boolean alliedUnit = unit.isAlliedWith(player);
    boolean hiddenEnemyUnit = !alliedUnit && unit.isHidden();

    return !tile.isFogged() && !hiddenEnemyUnit;
  }
}
