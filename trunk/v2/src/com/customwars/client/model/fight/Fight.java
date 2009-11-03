package com.client.model.api.fight;

/**
 * This class allows An Attacker and Defender to fight each other.
 * It can be used in 2 ways:
 * #1 Fight
 * initFight(attacker, defender)
 * startFight()
 *
 * #2 get attack damage as percentage
 * initFight(attacker, defender)
 * getAttackDamagePercentage()
 *
 * Note that getAttackDamagePercentage() will return
 * a different value or null after startFight() is invoked.
 *
 * @author stefan
 */
public interface Fight {

  public enum WeaponType {
    PRIMARY, SECONDARY, NONE
  }

  void initFight(Attacker attacker, Defender defender);

  /**
   * Start the attack - counter attack sequense
   */
  void startFight();

  /**
   * @return The damage the attacker will do against the defender
   */
  int getAttackDamagePercentage();

  /**
   * @return The weapon type that has the best attack value against the defender
   *         NONE when the attack value is 0
   */
  WeaponType getBestAttackWeaponType();
}
