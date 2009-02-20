package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.rules.UnitRules;

/**
 * Base class for Fights between an Attacking unit and a Defending unit
 *
 * @author Stefan
 */
public class UnitFight {
  private static int[][] baseDMG;
  private static int[][] altDMG;
  private UnitRules rules;

  public enum FightType {
    Attack, CounterAttack
  }

  protected FightType type;
  protected Unit attacker;
  protected Unit defender;

  public void initAttack(Unit attacker, Unit defender) {
    this.type = UnitFight.FightType.Attack;
    this.attacker = attacker;
    this.defender = defender;
  }

  public int calcAttackDamage() {
    int attackDmgPercentage = calcAttackDamagePercentage();
    return (int) Math.floor((attackDmgPercentage * defender.getMaxHp()) / 100);
  }

  public int calcAttackDamagePercentage() {
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

  private int getBaseDamage(int attUnitType, int defUnitType) {
    if (baseDMG[attUnitType][defUnitType] != -1) {
      return baseDMG[attUnitType][defUnitType];
    } else {
      return altDMG[attUnitType][defUnitType];
    }
  }

  public boolean canCounterAttack(Unit attacker, Unit defender) {
    return type != FightType.CounterAttack &&
            rules.canCounterAttack(attacker, defender);
  }

  public void counterAttack(Unit attacker) {
    swap();
    setType(UnitFight.FightType.CounterAttack);
    attacker.attack(defender, this);
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

  public void setRules(UnitRules rules) {
    this.rules = rules;
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
}
