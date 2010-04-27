package com.customwars.client.model.gameobject;

import com.customwars.client.App;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.fight.BasicFight;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.NumberUtil;

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
    int expBonus = getExperienceBonus(attackingUnit, defendingUnit);
    int coBonus = getCOBonus(attackingUnit, defendingUnit);

    double dmg = Math.floor(attackerHP / (float) attackMaxHP * baseDmg - terrainDef) + expBonus + coBonus;
    return dmg < 0 ? 0 : (int) dmg;
  }

  private int getExperienceBonus(Unit attackingUnit, Unit defendingUnit) {
    int attackBonus = getExperienceBonus(attackingUnit);
    int defenderBonus = getExperienceBonus(defendingUnit);
    return attackBonus - defenderBonus;
  }

  private int getExperienceBonus(Unit unit) {
    return App.getInt("plugin.unit_rank" + unit.getExperience() + "_fight_bonus");
  }

  private int getCOBonus(Unit attackingUnit, Unit defendingUnit) {
    int attackBonus = attackingUnit.getOwner().getCO().getAttackBonusPercentage(attackingUnit, defendingUnit);
    int defenseBonus = defendingUnit.getOwner().getCO().getDefenseBonusPercentage(attackingUnit, defendingUnit);
    return attackBonus - defenseBonus;
  }

  private int getTerrainDefense(Unit unit) {
    if (unit.getArmyBranch() == ArmyBranch.AIR) {
      return 0;
    } else {
      Tile t = (Tile) unit.getLocation();
      int terrainDefenseBonus = t.getTerrain().getDefenseBonus();
      return unit.getOwner().getCO().terrainDefenseHook(terrainDefenseBonus);
    }
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
      return baseDMG[attacker.getStats().getID()][defender.getStats().getID()];
    } else {
      return 0;
    }
  }

  private int getAltDamage(Unit attacker, Unit defender) {
    if (attacker.canFireSecondaryWeapon()) {
      return altDMG[attacker.getStats().getID()][defender.getStats().getID()];
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
