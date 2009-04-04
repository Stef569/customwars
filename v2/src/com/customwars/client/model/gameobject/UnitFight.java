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

  public int calcAttackDamage(Defender defender) {
    Unit defendingUnit = (Unit) defender;
    int attackDmgPercentage = calcAttackDamagePercentage();
    return (int) Math.floor((attackDmgPercentage * defendingUnit.getMaxHp()) / 100);
  }

  public final int calcAttackDamagePercentage() {
    Unit defendingUnit = (Unit) defender;
    Unit attackingUnit = (Unit) attacker;
    int attackerHP = attackingUnit.getHp();
    int attackMaxHP = attackingUnit.getMaxHp();

    int terrainDef = calcTerrainDefense(defendingUnit);
    int baseDmg = getBaseDamage(attackingUnit.getID(), defendingUnit.getID());
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

  private int getBaseDamage(int attUnitID, int defUnitID) {
    if (baseDMG[attUnitID][defUnitID] != NO_DAMAGE) {
      return baseDMG[attUnitID][defUnitID];
    } else {
      return altDMG[attUnitID][defUnitID];
    }
  }

  public boolean canCounterAttack(Attacker attacker, Defender defender) {
    return super.canCounterAttack(attacker, defender) && defender.canCounterAttack() &&
            isDefenderInRangeOfAttacker((Attacker) defender, (Defender) attacker);
  }

  public void counterAttack() {
    super.counterAttack();
    attacker.attack(defender, this);
  }

  private boolean isDefenderInRangeOfAttacker(Attacker attacker, Defender defender) {
    for (Unit enemyInRange : map.getEnemiesInRangeOf(attacker)) {
      if (enemyInRange == defender) return true;
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
