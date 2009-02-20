package com.customwars.client.model.rules;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

public class UnitRules {
  private Map<Tile> map;

  public UnitRules(Map<Tile> map) {
    this.map = map;
  }

  public boolean canSupply(Unit supplier, Unit supplyable) {
    return supplier.isActive() && supplier.getOwner().isAlliedWith(supplyable.getOwner()) &&
            !isFogged(supplyable.getLocation());
  }

  public boolean canAttack(Unit attacker, Unit damageable) {
    return (attacker.isActive() && !attacker.getOwner().isAlliedWith(damageable.getOwner())) &&
            !isFogged(damageable.getLocation());
  }

  /**
   * Attacker is attacking defender, can the defender counter attack the attacker?
   */
  public boolean canCounterAttack(Unit attacker, Unit defender) {
    if (defender != null && defender.canCounterAttack()) {
      for (Unit enemyInRange : map.getEnemiesInRangeOf(defender, defender.getLocation())) {
        if (enemyInRange == attacker) return true;
      }
    }
    return false;
  }

  public boolean canHeal(Unit healer, Unit damageable) {
    return healer.isActive() && healer.getOwner().isAlliedWith(damageable.getOwner()) &&
            !isFogged(damageable.getLocation());
  }

  private boolean isFogged(Location location) {
    Tile t = (Tile) location;
    return t.isFogged();
  }
}
