package com.customwars.client.model.gameobject;

import com.customwars.client.model.fight.BasicFight;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.tools.NumberUtil;

/**
 * Handles Fights between a unit and a city
 *
 * @author Stefan
 */
public class UnitVsCityFight extends BasicFight {
  private static int[][] baseDMG;
  private static int[][] altDMG;

  public int getAttackDamagePercentage() {
    Unit attackingUnit = (Unit) attacker;
    City city = (City) defender;
    City baseCity = CityFactory.getBaseCity(city.getType());
    int attackerHP = attackingUnit.getInternalHp();
    int attackMaxHP = attackingUnit.getInternalMaxHp();

    int baseDmg = getAttackDamagePercentage(attackingUnit, baseCity);
    double dmg = Math.floor(attackerHP / (float) attackMaxHP * baseDmg);
    return dmg < 0 ? 0 : (int) dmg;
  }

  /**
   * Get the highest damage percentage that the attacker can cause to defender
   *
   * @return The highest damage percentage or 0 when the attacker cannot attack the defender
   */
  private int getAttackDamagePercentage(Unit attacker, City city) {
    int baseDmg = getBaseDamage(attacker, city);
    int altDmg = getAltDamage(attacker, city);
    return NumberUtil.findHighest(baseDmg, altDmg);
  }

  private int getBaseDamage(Unit attacker, City city) {
    return attacker.canFirePrimaryWeapon() ? baseDMG[attacker.getStats().getID()][city.getID()] : 0;
  }

  private int getAltDamage(Unit attacker, City city) {
    return attacker.canFireSecondaryWeapon() ? altDMG[attacker.getStats().getID()][city.getID()] : 0;
  }

  /**
   * @return The WeaponType that will do the highest damage
   *         WeaponType.NONE is returned when the damage == 0
   */
  public Fight.WeaponType getBestAttackWeaponType() {
    Unit attackingUnit = (Unit) attacker;
    City city = (City) defender;

    int baseDmg = getBaseDamage(attackingUnit, city);
    int altDmg = getAltDamage(attackingUnit, city);
    int highestDamage = NumberUtil.findHighest(baseDmg, altDmg);

    if (highestDamage == 0) {
      return Fight.WeaponType.NONE;
    } else if (baseDmg == highestDamage) {
      return Fight.WeaponType.PRIMARY;
    } else if (altDmg == highestDamage) {
      return Fight.WeaponType.SECONDARY;
    } else {
      return Fight.WeaponType.NONE;
    }
  }

  public static void setBaseDMG(int[][] baseDMG) {
    UnitVsCityFight.baseDMG = baseDMG;
  }

  public static void setAltDMG(int[][] altDMG) {
    UnitVsCityFight.altDMG = altDMG;
  }
}