package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.UnitVsCityFight;
import com.customwars.client.model.map.Map;

import java.util.List;

/**
 * If an enemy is in range, attack it.
 * If there is more then 1 enemy, choose the one that we can do the most damage to.
 */
public class AttackRoutine implements AIRoutine {
  private final Game game;
  private final Map map;
  private final Unit unit;

  public AttackRoutine(Game game, Unit unit) {
    this.game = game;
    this.map = game.getMap();
    this.unit = unit;
  }

  @Override
  public RoutineResult think() {
    List<Defender> enemiesInRange = game.getMap().getEnemiesInRangeOf(unit);

    if (!enemiesInRange.isEmpty()) {
      return searchForDirectAttacks(enemiesInRange);
    } else {
      return searchForIndirectAttacks();
    }
  }

  private RoutineResult searchForDirectAttacks(List<Defender> enemiesInRange) {
    Defender enemyToAttack = findBestEnemyToAttack(enemiesInRange);

    if (enemyToAttack instanceof Unit) {
      return new RoutineResult(Fuz.UNIT_ORDER.ATTACK_UNIT, unit.getLocation(), enemyToAttack.getLocation());
    } else if (enemyToAttack instanceof City) {
      return new RoutineResult(Fuz.UNIT_ORDER.ATTACK_CITY, unit.getLocation(), enemyToAttack.getLocation());
    } else {
      return null;
    }
  }

  private Defender findBestEnemyToAttack(List<Defender> enemiesInRange) {
    int highestDamage = 0;
    Defender enemyToAttack = null;

    for (Defender defender : enemiesInRange) {
      if (defender instanceof Unit) {
        if (!defender.isDestroyed() && !unit.isDestroyed()) {
          UnitFight fight = new UnitFight(map, unit, defender);
          int attackDamage = fight.getAttackDamagePercentage();

          if (attackDamage > highestDamage) {
            highestDamage = attackDamage;
            enemyToAttack = defender;
          }
        }
      } else if (defender instanceof City) {
        UnitVsCityFight fight = new UnitVsCityFight(unit, defender);
        int attackDamage = fight.getAttackDamagePercentage();

        if (attackDamage > highestDamage) {
          highestDamage = attackDamage;
          enemyToAttack = defender;
        }
      }
    }
    return enemyToAttack;
  }

  private RoutineResult searchForIndirectAttacks() {
    return null;
  }
}
