package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.Unit;

/**
 * Handles Fights between 2 units
 *
 * @author Stefan
 */
public class UnitFight {
  private static int[][] baseDMG;
  private static int[][] altDMG;
  private Map<Tile> map;

  public enum FightType {
    Attack, CounterAttack
  }

  protected FightType type;
  protected Unit attacker;
  protected Unit defender;

  public UnitFight(Map<Tile> map) {
    this.map = map;
  }

  public void initAttack(Unit attacker, Unit defender) {
    this.type = UnitFight.FightType.Attack;
    this.attacker = attacker;
    this.defender = defender;
  }

  public int calcAttackDamage() {
    int attackDmgPercentage = calcAttackDamagePercentage();
    return (int) Math.floor((attackDmgPercentage * defender.getMaxHp()) / 100);
  }

  public final int calcAttackDamagePercentage() {
    int terrainDef = calcTerrainDefense(defender);
    int baseDmg = getBaseDamage(attacker.getID(), defender.getID());
    int special = calcAdditionalDamageCases();

    return (int) Math.floor(attacker.getHp() / (float) attacker.getMaxHp() * baseDmg - terrainDef - special);
  }

  public int calcTerrainDefense(Unit unit) {
    Tile t = (Tile) unit.getLocation();
    return t.getTerrain().getDefenseBonus();
  }

  protected int calcAdditionalDamageCases() {
    return 0;
  }

  private int getBaseDamage(int attUnitID, int defUnitID) {
    if (baseDMG[attUnitID][defUnitID] != -1) {
      return baseDMG[attUnitID][defUnitID];
    } else {
      return altDMG[attUnitID][defUnitID];
    }
  }

  /**
   * Attacker has performed an attack, can the defender counter attack?
   */
  public boolean canCounterAttack(Unit attacker, Unit defender) {
    return type != FightType.CounterAttack &&
            defender != null && defender.canCounterAttack() &&
            isAttackerInRangeOfDefender(attacker, defender);
  }

  private boolean isAttackerInRangeOfDefender(Unit attacker, Unit defender) {
    for (Unit enemyInRange : map.getEnemiesInRangeOf(defender)) {
      if (enemyInRange == attacker) return true;
    }
    return false;
  }

  public void counterAttack(Unit attacker) {
    swap();
    setType(UnitFight.FightType.CounterAttack);
    defender.attack(attacker, this);
  }

  /**
   * Swaps Attacker to Defender
   * The calcPercentage method will return different results after the swap.
   */
  protected void swap() {
    attacker = defender;
    defender = attacker;
  }

  protected void setType(FightType type) {
    this.type = type;
  }

  public static void setBaseDMG(int[][] baseDMG) {
    UnitFight.baseDMG = baseDMG;
  }

  public static void setAltDMG(int[][] altDMG) {
    UnitFight.altDMG = altDMG;
  }

  public FightType getType() {
    return type;
  }

  public void clear() {
    attacker = null;
    defender = null;
    type = null;
  }
}
