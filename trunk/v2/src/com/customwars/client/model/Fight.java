package com.customwars.client.model;

/**
 * Allows an Attacker to attack a Defender
 * When canCounterAttack returns true the defender will counterAttack the attacker
 * A counter attack can only be performed once.
 *
 * @author stefan
 */
public abstract class Fight {
  protected Attacker attacker;
  protected Defender defender;

  public enum FightType {
    ATTACK, COUNTER_ATTACK
  }

  public enum WeaponType {
    PRIMARY, SECONDARY
  }

  private FightType type;

  public void initAttack(Attacker attacker, Defender defender) {
    this.type = FightType.ATTACK;
    this.attacker = attacker;
    this.defender = defender;
  }

  public abstract int calcAttackDamagePercentage(Defender defender);

  /**
   * Attacker has performed an attack, can the defender counter attack?
   */
  public boolean canCounterAttack(Attacker attacker, Defender defender) {
    return defender instanceof Attacker && attacker instanceof Defender &&
            type != FightType.COUNTER_ATTACK;
  }

  public void counterAttack() {
    swap();
    setType(FightType.COUNTER_ATTACK);
  }

  /**
   * Swaps attacker to defender and defender to attacker
   * The calcPercentage method will return different results after the swap.
   * The swap is only safe to perform when canCounterAttack returns true.
   */
  protected void swap() {
    Attacker attackerCopy = attacker;
    attacker = (Attacker) defender;
    defender = (Defender) attackerCopy;
  }

  protected void setType(FightType type) {
    this.type = type;
  }

  public FightType getType() {
    return type;
  }

  public abstract WeaponType getBestAttackWeaponType();
}
