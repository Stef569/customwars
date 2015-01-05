package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class generates a list of enemies for each unit.
 * It allows to check what unit is most dangerous(can inflict the most damage) against another unit.
 * To calculate the enemies {@code generate()} should be called.
 *
 * @see #generate
 * @see #getTopEnemies(String)
 */
public class UnitFightStats {
  private static final List<Unit> allUnits;
  private Map<String, List<Unit>> enemiesByName;

  static {
    allUnits = UnitFactory.getAllUnits();
  }

  public UnitFightStats() {
    enemiesByName = new HashMap<String, List<Unit>>();
  }

  /**
   * Generate a list that contains the enemies of each unit.
   * Sort the list based on the most damage that each enemy unit can do.
   * <pre>
   * Anti-Tank enemies = [Mech, Inf, Bomber]
   * Infantry enemies  = [Anti-Air, Bomber]
   * ...
   * </pre>
   */
  public void generate() {
    for (Unit unit : allUnits) {
      String unitName = unit.getStats().getName();
      List<Unit> enemies = findEnemies(unit);
      enemiesByName.put(unitName, enemies);
    }
  }

  /**
   * Find the enemies of the given unit. The enemy units are sorted by attack damage.
   * units that inflict the most damage will be at the start of the list.
   * Enemy units that cannot be attacked by the given unit are excluded.
   *
   * @param unit The unit to search enemies for
   * @return a sorted list of enemies that can inflict the most damage to the unit.
   */
  private List<Unit> findEnemies(Unit unit) {
    List<Enemy> enemies = new ArrayList<Enemy>();

    for (Unit enemyUnit : allUnits) {
      if (enemyUnit.canFireOn(unit)) {
        int attackDamage = UnitFight.getAttackDamagePercentage(enemyUnit, unit);

        if (attackDamage > 0) {
          enemies.add(new Enemy(enemyUnit, attackDamage));
        }
      }
    }

    Collections.sort(enemies, new Comparator<Enemy>() {
      public int compare(Enemy o1, Enemy o2) {
        return o2.attackDamage - o1.attackDamage;
      }
    });

    List<Unit> enemyUnits = new ArrayList<Unit>(enemies.size());
    for (Enemy enemy : enemies) {
      enemyUnits.add(enemy.unit);
    }

    return enemyUnits;
  }

  /**
   * Get the enemies that can do the most damage against the given unit.
   * Enemy units that cannot be attacked by the given unit are excluded.
   *
   * @param unitName The name of the unit to search enemies for
   * @return a sorted list of enemies that can inflict the most damage to the unit.
   */
  public List<Unit> getTopEnemies(String unitName) {
    if (enemiesByName.containsKey(unitName)) {
      return enemiesByName.get(unitName);
    } else {
      throw new IllegalArgumentException("no enemies for " + unitName);
    }
  }

  /**
   * This class is used to sort the enemies
   */
  private class Enemy {
    public Unit unit;
    public int attackDamage;

    public Enemy(Unit unit, int attackDamage) {
      this.unit = unit;
      this.attackDamage = attackDamage;
    }
  }

}
