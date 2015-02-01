package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.UnitVsCityFight;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.path.PathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * If an enemy is in range, attack it.
 * If there is more then 1 enemy, choose the one that we can do the most damage to.
 * If the unit is a Direct unit, find the best adjacent location.
 */
public class AttackRoutine implements AIRoutine {
  private final Game game;
  private final Map map;
  private final Unit unit;
  private final PathFinder pathFinder;

  public AttackRoutine(Game game, Unit unit) {
    this.game = game;
    this.map = game.getMap();
    this.unit = unit;
    this.pathFinder = new PathFinder(map);
  }

  @Override
  public RoutineResult think() {
    // Create the move and attack zone
    // This is required for unit.getAttackZone()
    map.buildMovementZone(unit);
    map.buildAttackZone(unit);

    if (unit.isDirect()) {
      return moveAndFire();
    } else if (unit.isInDirect()) {
      if (unit.isBallistic()) {
        return moveAndFireAsIndirect();
      } else {
        return fire();
      }
    }

    return null;
  }

  /**
   * Move and Fire finds the best enemy to attack for direct units
   *
   * @return The RoutineResult that contains the locations needed to attack the enemy
   */
  private RoutineResult moveAndFire() {
    List<Defender> enemiesInRange = map.getEnemiesInRangeOfIndirect(unit);

    if (!enemiesInRange.isEmpty()) {
      List<AttackPriority> attackPriorities = findBestEnemiesToAttack(enemiesInRange);

      Defender enemyToAttack = null;
      Location moveDestination = null;
      for (AttackPriority attackPriority : attackPriorities) {
        if (attackPriority.priority > 45) {
          Defender enemy = attackPriority.enemy;
          Location bestAdjacentLocation = findBestAdjacentLocation(enemy);

          if (bestAdjacentLocation != null) {
            enemyToAttack = enemy;
            moveDestination = bestAdjacentLocation;
            break;
          }
        }
      }

      if (enemyToAttack != null) {
        return createAttackRoutineResultForDirect(moveDestination, enemyToAttack);
      }
    }

    return null;
  }

  private Location findBestAdjacentLocation(Defender enemyToAttack) {
    List<Location> attackZone = unit.getAttackZone();
    Location enemyLocation = enemyToAttack.getLocation();

    int highestTileValue = 0;
    Location bestLocation = null;

    for (Location adjacentLocation : map.getSurroundingTiles(enemyLocation, 1, 1)) {
      if (attackZone.contains(adjacentLocation)) {
        boolean hasUnit = map.hasUnitOn(adjacentLocation);
        boolean sameUnit = adjacentLocation.equals(unit.getLocation());

        if (!hasUnit || sameUnit) {
          if (pathFinder.canMoveTo(unit, adjacentLocation)) {
            Terrain terrain = map.getTile(adjacentLocation).getTerrain();
            int terrainDefense = terrain.getDefenseBonus();

            if (terrainDefense > highestTileValue) {
              highestTileValue = terrainDefense;
              bestLocation = adjacentLocation;
            }
          }
        }
      }
    }

    return bestLocation;
  }

  private RoutineResult createAttackRoutineResultForDirect(Location moveDestination, Defender enemyToAttack) {
    if (enemyToAttack instanceof Unit) {
      return new RoutineResult(Fuz.UNIT_ORDER.ATTACK_UNIT, unit.getLocation(), moveDestination, enemyToAttack.getLocation());
    } else if (enemyToAttack instanceof City) {
      return new RoutineResult(Fuz.UNIT_ORDER.ATTACK_CITY, unit.getLocation(), moveDestination, enemyToAttack.getLocation());
    } else {
      return null;
    }
  }

  /**
   * Fire finds the best enemy to attack for indirect-ranged units
   *
   * @return The RoutineResult that contains the locations needed to attack the enemy
   */
  private RoutineResult fire() {
    List<Defender> enemiesInRange = map.getEnemiesInRangeOf(unit);

    if (!enemiesInRange.isEmpty()) {
      Defender enemyToAttack = findBestEnemyToAttackForInDirect(enemiesInRange);

      if (enemyToAttack != null) {
        return createMoveAndAttackRoutineResult(enemyToAttack);
      }
    }

    return null;
  }

  private Defender findBestEnemyToAttackForInDirect(List<Defender> enemiesInRange) {
    List<AttackPriority> attackPriorities = findBestEnemiesToAttack(enemiesInRange);

    if (!attackPriorities.isEmpty()) {
      return attackPriorities.get(0).enemy;
    }

    return null;
  }

  private RoutineResult createMoveAndAttackRoutineResult(Defender enemyToAttack) {
    if (enemyToAttack instanceof Unit) {
      return new RoutineResult(Fuz.UNIT_ORDER.ATTACK_UNIT, unit.getLocation(), unit.getLocation(), enemyToAttack.getLocation());
    } else if (enemyToAttack instanceof City) {
      return new RoutineResult(Fuz.UNIT_ORDER.ATTACK_CITY, unit.getLocation(), unit.getLocation(), enemyToAttack.getLocation());
    } else {
      return null;
    }
  }

  private List<AttackPriority> findBestEnemiesToAttack(List<Defender> enemiesInRange) {
    List<AttackPriority> attackPriorities = new ArrayList<AttackPriority>();
    City hq = game.getActivePlayer().getHq();

    Unit capturingUnit = null;
    if (isHqBeingCaptured(hq)) {
      capturingUnit = map.getUnitOn(hq.getLocation());
    }

    for (Defender defender : enemiesInRange) {
      if (defender instanceof Unit) {
        if (!defender.isDestroyed() && !unit.isDestroyed()) {
          if (defender == capturingUnit) {
            attackPriorities.add(new AttackPriority(defender, 100));
          } else {
            UnitFight fight = new UnitFight(map, unit, defender);
            int attackDamage = fight.getAttackDamagePercentage();
            attackPriorities.add(new AttackPriority(defender, attackDamage));
          }
        }
      } else if (defender instanceof City) {
        UnitVsCityFight fight = new UnitVsCityFight(unit, defender);
        int attackDamage = fight.getAttackDamagePercentage();
        attackPriorities.add(new AttackPriority(defender, attackDamage));
      }
    }

    Collections.sort(attackPriorities);
    return attackPriorities;
  }

  private boolean isHqBeingCaptured(City city) {
    return city != null && city.isHQ() && city.isBeingCaptured();
  }

  private RoutineResult moveAndFireAsIndirect() {
    // todo battleship
    return fire();
  }

  public EnumSet<Fuz.UNIT_ORDER> getSupportedOrders() {
    return EnumSet.of(Fuz.UNIT_ORDER.ATTACK_UNIT, Fuz.UNIT_ORDER.ATTACK_CITY);
  }
}
