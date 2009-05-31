package com.customwars.client.model.gameobject;

import com.customwars.client.model.fight.BasicFight;
import com.customwars.client.model.map.Tile;
import tools.NumberUtil;

/**
 * Handles Fights between 2 units
 *
 * @author Stefan
 */
public class UnitFight extends BasicFight {
  private static int[][] baseDMG;
  private static int[][] altDMG;

  public int getAttackDamagePercentage() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;
    int attackerHP = attackingUnit.getInternalHp();
    int attackMaxHP = attackingUnit.getInternalMaxHp();

    int terrainDef = getTerrainDefense(defendingUnit);
    int baseDmg = getAttackDamagePercentage(attackingUnit, defendingUnit);
    return (int) Math.floor(attackerHP / (float) attackMaxHP * baseDmg - terrainDef);
  }

  private int getTerrainDefense(Unit unit) {
    Tile t = (Tile) unit.getLocation();
    return t.getTerrain().getDefenseBonus();
  }

  /**
   * Get the highest damage percentage that the attacker can cause to defender
   *
   * @return The highest damage percentage or 0 when the attacker cannot attack the defender
   */
  private int getAttackDamagePercentage(Unit attacker, Unit defender) {
    int baseDmg = getBaseDamage(attacker, defender);
    int altDmg = getAltDamage(attacker, defender);
    return NumberUtil.findHighest(baseDmg, altDmg);
  }

  private int getBaseDamage(Unit attacker, Unit defender) {
    if (attacker.canFirePrimaryWeapon()) {
      return baseDMG[attacker.getID()][defender.getID()];
    } else {
      return 0;
    }
  }

  private int getAltDamage(Unit attacker, Unit defender) {
    if (attacker.canFireSecondaryWeapon()) {
      return altDMG[attacker.getID()][defender.getID()];
    } else {
      return 0;
    }
  }

  /**
   * @return The WeaponType that will do the highest damage
   *         WeaponType.NONE is returned when the damage == 0
   */
  public WeaponType getBestAttackWeaponType() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;

    int baseDmg = getBaseDamage(attackingUnit, defendingUnit);
    int altDmg = getAltDamage(attackingUnit, defendingUnit);
    int highestDamage = NumberUtil.findHighest(baseDmg, altDmg);

    if (highestDamage == 0) {
      return WeaponType.NONE;
    } else if (baseDmg == highestDamage) {
      return WeaponType.PRIMARY;
    } else if (altDmg == highestDamage) {
      return WeaponType.SECONDARY;
    } else {
      return WeaponType.NONE;
    }
  }

  public static void setBaseDMG(int[][] baseDMG) {
    UnitFight.baseDMG = baseDMG;
  }

  public static void setAltDMG(int[][] altDMG) {
    UnitFight.altDMG = altDMG;
  }
}
