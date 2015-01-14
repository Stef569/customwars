package com.customwars.client.model.gameobject;

import com.customwars.client.App;
import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.fight.BasicFight;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.map.Map;
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
  private static int[][] submergedDMG;
  private final Map map;

  public UnitFight(Map map, Attacker attacker, Defender defender) {
    super(attacker, defender);
    this.map = map;
  }

  public int getAttackDamagePercentage() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;

    if(attackingUnit.isDestroyed() || defendingUnit.isDestroyed()) {
      return 0;
    }

    int attackerHP = attackingUnit.getInternalHp();
    int attackMaxHP = attackingUnit.getInternalMaxHp();
    int attExpBonus = getExperienceBonus(attackingUnit);
    int attCOBonus = attackingUnit.getOwner().getCO().getAttackBonusPercentage(attackingUnit, defendingUnit);
    int attCommTowersCount = map.getCityCount("comm_tower", attackingUnit.getOwner());
    int attackBonus = attCOBonus + attCommTowersCount * 5 + attExpBonus;

    int defHP = defendingUnit.getInternalHp();
    int defMaxHP = defendingUnit.getInternalMaxHp();
    int defExpBonus = getExperienceBonus(defendingUnit);
    int defCoBonus = defendingUnit.getOwner().getCO().getDefenseBonusPercentage(attackingUnit, defendingUnit);
    int defCommTowersCount = map.getCityCount("comm_tower", defendingUnit.getOwner());
    int terrainDefense = getTerrainDefense(defendingUnit);
    int defenseBonus = defCoBonus + defCommTowersCount * 5 + (terrainDefense * 10 * defHP / defMaxHP) + defExpBonus;

    int baseDmg = getAttackDamagePercentage(attackingUnit, defendingUnit);
    float attackValue = baseDmg * (attackerHP / (float) attackMaxHP);

    int dmg = (int) Math.floor(attackValue * attackBonus / defenseBonus);
    return dmg < 0 ? 0 : dmg;
  }

  private int getExperienceBonus(Unit unit) {
    return App.getInt("plugin.unit_rank" + unit.getExperience() + "_fight_bonus");
  }

  private int getTerrainDefense(Unit unit) {
    if (unit.isAir()) {
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
  public static int getAttackDamagePercentage(Unit attacker, Unit defender) {
    int baseDmg = getBaseDamage(attacker, defender);
    int altDmg = getAltDamage(attacker, defender);
    return NumberUtil.findHighest(baseDmg, altDmg);
  }

  private static int getBaseDamage(Unit attacker, Unit defender) {
    int attackerID = attacker.getStats().getID();
    int defenderID = defender.getStats().getID();

    if (attacker.canFirePrimaryWeapon()) {
      if (defender.isSubmerged()) {
        return submergedDMG[attackerID][defenderID];
      } else {
        return baseDMG[attackerID][defenderID];
      }
    } else {
      return 0;
    }
  }

  private static int getAltDamage(Unit attacker, Unit defender) {
    if (attacker.canFireSecondaryWeapon()) {
      return altDMG[attacker.getStats().getID()][defender.getStats().getID()];
    } else {
      return 0;
    }
  }

  @Override
  public int getBasicAttackDamagePercentage() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;
    return getAttackDamagePercentage(attackingUnit, defendingUnit);
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

  @Override
  public boolean canUsePrimaryWeapon() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;
    return getBaseDamage(attackingUnit, defendingUnit) > 0;
  }

  @Override
  public boolean canUseSecondaryWeapon() {
    Unit attackingUnit = (Unit) attacker;
    Unit defendingUnit = (Unit) defender;
    return getAltDamage(attackingUnit, defendingUnit) > 0;
  }

  public static void setBaseDMG(int[][] baseDMG) {
    UnitFight.baseDMG = baseDMG;
  }

  public static void setAltDMG(int[][] altDMG) {
    UnitFight.altDMG = altDMG;
  }

  public static void setSubmergedDMG(int[][] submergedDMG) {
    UnitFight.submergedDMG = submergedDMG;
  }
}
