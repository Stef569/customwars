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
   *         The returned value is always positive [0..100].
   */
  int getAttackDamagePercentage();

  /**
   * The most basic damage calculation it has no influences from the game.
   * In other words the base or alt damage.
   * This gives a rough idea how much dmg the attacker will do while not giving away the exact number.
   *
   * @return The damage the attacker will do against the defender as a percentage.
   *         The returned value is always positive [0..100].
   */
  int getBasicAttackDamagePercentage();

  /**
   * @return The weapon type that has the best attack value against the defender
   *         NONE when the attack value is 0.
   */
  WeaponType getBestAttackWeaponType();

  /**
   * @return Can the attacking unit use his primary weapon.
   *         This means that the primary weapon has ammo left and
   *         that it will do some dmg against the defender.
   */
  boolean canUsePrimaryWeapon();

  /**
   * @return Can the attacking unit use his secondary weapon.
   *         This means that the secondary weapon has ammo left and
   *         that it will do some dmg against the defender.
   */
  boolean canUseSecondaryWeapon();
}
