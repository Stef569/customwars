package com.customwars.client.controller;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
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
    Unit unit = map.getUnitOn(selected, 0);
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
    Unit transporter = map.getUnitOn(selected, 0);

    return transporter != null && transporter.canAdd(unit) &&
        transporter.getOwner() == unit.getOwner();
  }

  /**
   * Can the transport drop any units
   * #1 This controller controls a unit with transport abilities
   * #2 There is at least 1 free tile within the drop range
   * #3 There is at least 1 ground unit in the transport
   * #4 At least 1 unit in the transport can be unloaded on an adjacent tile.
   *
   * @return true if the transport can drop at least 1 unit
   */
  boolean canStartDrop() {
    List<Location> freeDropLocations = map.getFreeDropLocations(unit);

    boolean isTransportingUnit = unit.getStats().canTransport();
    boolean atleast1FreeDropTile = !freeDropLocations.isEmpty();
    boolean atLeast1GroundUnitToDrop = unit.isTransportingUnitType(ArmyBranch.LAND);

    return isTransportingUnit && atleast1FreeDropTile && atLeast1GroundUnitToDrop;
  }

  /**
   * Can this unit launch planes:
   * #1 This controller controls a unit with transport abilities
   * #2 There is at least 1 unit in the transport
   * #3 The plane can fly at least 1 tile away.
   * #4 The plane did not land on the carrier in this turn.
   * Note that carrier is just an example any unit can have launch abilities.
   * Only planes can be launched all other units are dropped.
   */
  boolean canStartLaunch(Tile from, Tile to) {
    boolean isCarrier = unit.getStats().canTransport();
    boolean surroundedByEnemyUnits = map.isSurroundedByEnemyUnits(unit);
    boolean atLeast1ActiveAirUnitLoaded = unit.isTransportingUnitType(ArmyBranch.AIR);
    boolean moved = !from.equals(to);

    return isCarrier && !surroundedByEnemyUnits && atLeast1ActiveAirUnitLoaded &&
      !moved && unit.isActive();
  }

  /**
   * Can the unit be launched from the carrier:
   * #1 The unit is an air unit.
   *
   * @see #canStartLaunch(Tile, Tile)
   */
  public boolean canLaunch(Tile from, Tile to, Unit unitToLaunch) {
    boolean canStartLaunch = canStartLaunch(from, to);

    return canStartLaunch && unitToLaunch.isAir();
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
    Unit target = map.getUnitOn(selected, 0);
    Unit selectedUnit = map.getUnitOn(selected, 1);

    if (target != unit) {
      UnitStats unitStats = unit.getStats();
      UnitStats targetStats = target.getStats();

      if (selectedUnit == unit && unitStats.getID() == targetStats.getID()) {
        if (target.getOwner() == unit.getOwner() && target.getHp() <= 9) {
          if (targetStats.canJoin() && unitStats.canJoin()) {
            if (targetStats.canTransport()) {
              if (!target.hasUnitsInTransport() && !unit.hasUnitsInTransport()) {
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

  /**
   * #1 The unit with the lowest produce cost can be bought by the player
   * #2 Only transporting units can produce a unit
   * #3 There is min 1 free place in the transport
   * #4 The unit did not move
   * #5 The unit has min 1 construction material
   */
  boolean canStartProduceUnit(Tile from) {
    UnitStats stats = unit.getStats();

    int lowestUnitPrice = Integer.MAX_VALUE;
    for (String unitName : stats.getUnitsThatCanBeProduced()) {
      int price = UnitFactory.getUnit(unitName).getPrice();
      if (price < lowestUnitPrice) {
        lowestUnitPrice = price;
      }
    }

    boolean canProduce = stats.canProduceUnits();
    boolean canAfford = unit.getOwner().isWithinBudget(lowestUnitPrice);
    boolean canTransport = stats.canTransport() && !unit.isTransportFull();
    boolean moved = from != unit.getLocation();
    boolean hasMaterials = unit.hasConstructionMaterials();

    return canProduce && canAfford && canTransport && !moved && hasMaterials;
  }

  /**
   * #1 Same conditions as canStartProduce
   * #2 The unitID can be bought
   * #3 The unitID can be produced
   */
  public boolean canProduceUnit(Tile from, String unitID) {
    boolean canStartProduce = canStartProduceUnit(from);
    boolean canProduce = unit.getStats().canProduceUnit(unitID);
    int price = UnitFactory.getUnit(unitID).getPrice();
    boolean canBuy = unit.getOwner().isWithinBudget(price);

    return canStartProduce && canProduce && canBuy;
  }

  boolean canDive() {
    return unit.canDive() && !unit.isSubmerged();
  }

  boolean canSurface() {
    // if a unit can dive it can also surface...
    return unit.canDive() && unit.isSubmerged();
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
    return tile != null && map.getUnitOn(tile) == unit;
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
