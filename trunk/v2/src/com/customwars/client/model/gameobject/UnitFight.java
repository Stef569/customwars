package com.customwars.client.model.gameobject;

import com.customwars.client.model.Attacker;
import com.customwars.client.model.Defender;
import com.customwars.client.model.Fight;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Handles Fights between 2 units
 *
 * @author Stefan
 */
public class UnitFight extends Fight {
  private static final int NO_DAMAGE = 0;
  private static int[][] baseDMG;
  private static int[][] altDMG;
  private Map<Tile> map;

  public UnitFight(Map<Tile> map) {
    this.map = map;
  }

  public final int calcAttackDamagePercentage(Defender defender) {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;
    int attackerHP = attackingUnit.getHp();
    int attackMaxHP = attackingUnit.getMaxHp();

    int terrainDef = calcTerrainDefense(defendingUnit);
    int baseDmg = getAttackDamagePercentage(attackingUnit, defendingUnit);
    int special = calcAdditionalDamageCases();

    return (int) Math.floor(attackerHP / (float) attackMaxHP * baseDmg - terrainDef - special);
  }

  public int calcTerrainDefense(Unit unit) {
    Tile t = (Tile) unit.getLocation();
    return t.getTerrain().getDefenseBonus();
  }

  protected int calcAdditionalDamageCases() {
    return 0;
  }

  /**
   * Get the highest damage percentage that the attacker can cause to defender
   *
   * @return The highest damage percentage or N0_DAMAGE when the attacker cannot attack the defender
   */
  private int getAttackDamagePercentage(Unit attacker, Unit defender) {
    int baseDmg = getBaseDamage(attacker, defender);
    int altDmg = getAltDamage(attacker, defender);
    int highestDamage = findHighestDamage(baseDmg, altDmg);

    if (highestDamage == NO_DAMAGE) {
      return NO_DAMAGE;
    } else if (baseDmg == highestDamage) {
      return baseDmg;
    } else if (altDmg == highestDamage) {
      return altDmg;
    } else {
      return NO_DAMAGE;
    }
  }

  private int getBaseDamage(Unit attacker, Unit defender) {
    if (attacker.canFirePrimaryWeapon()) {
      return baseDMG[attacker.getID()][defender.getID()];
    } else {
      return NO_DAMAGE;
    }
  }

  private int getAltDamage(Unit attacker, Unit defender) {
    if (attacker.canFireSecondaryWeapon()) {
      return altDMG[attacker.getID()][defender.getID()];
    } else {
      return NO_DAMAGE;
    }
  }

  private int findHighestDamage(int... values) {
    int highest = 0;

    for (int val : values) {
      if (val > highest) highest = val;
    }
    return highest;
  }

  public boolean canCounterAttack(Attacker attacker, Defender defender) {
    return super.canCounterAttack(attacker, defender) && defender.canCounterAttack(attacker) &&
            isDefenderAdjacentOfAttacker((Attacker) defender, (Defender) attacker);
  }

  public void counterAttack() {
    super.counterAttack();
    attacker.attack(defender, this);
  }

  /**
   * @return The WeaponType that will inflict the highest damage
   *         null is returned when the inflicted damage == 0
   */
  public WeaponType getBestAttackWeaponType() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;

    int baseDmg = getBaseDamage(attackingUnit, defendingUnit);
    int altDmg = getAltDamage(attackingUnit, defendingUnit);
    int highestDamage = findHighestDamage(baseDmg, altDmg);

    if (highestDamage == NO_DAMAGE) {
      return null;
    } else if (baseDmg == highestDamage) {
      return WeaponType.PRIMARY;
    } else if (altDmg == highestDamage) {
      return WeaponType.SECONDARY;
    } else {
      return null;
    }
  }

  private boolean isDefenderAdjacentOfAttacker(Attacker attacker, Defender defender) {
    for (Tile t : map.getSurroundingTiles(attacker.getLocation(), 1, 1)) {
      Unit unit = map.getUnitOn(t);
      if (unit != null && unit == defender) return true;
    }
    return false;
  }

  public static void setBaseDMG(int[][] baseDMG) {
    UnitFight.baseDMG = baseDMG;
  }

  public static void setAltDMG(int[][] altDMG) {
    UnitFight.altDMG = altDMG;
  }
}
