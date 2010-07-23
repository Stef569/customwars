package com.customwars.client.model.fight;

/**
 * This class allows An Attacker and Defender to fight each other.
 * <p/>
 * Note that getAttackDamagePercentage() will return
 * a different value or null after startFight() is invoked.
 */
public interface Fight {

  public enum WeaponType {
    PRIMARY, SECONDARY, NONE
  }

  /**
   * Start the attack - counter attack sequence
   */
  void startFight();

  /**
   * @return The damage the attacker will do against the defender as a percentage.
   *         The returned value is always positive.
   */
  int getAttackDamagePercentage();

  /**
   * @return The weapon type that has the best attack value against the defender
   *         NONE when the attack value is 0
   */
  WeaponType getBestAttackWeaponType();
}
