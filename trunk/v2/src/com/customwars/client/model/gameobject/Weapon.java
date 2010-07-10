package com.customwars.client.model.gameobject;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.map.Range;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A weapon has an amount of ammo, it can fire within a fire range
 * It can only attack ArmyBranches defined in attacks
 *
 * @author Stefan
 */
public class Weapon extends GameObject {
  public static final int UNLIMITED_AMMO = 99;
  private final String name;
  private final String description;
  private final Range fireRange;
  private final int maxAmmo;
  private final boolean balistic;             // Gives this weapon indirect firing ability even after moving
  private final List<ArmyBranch> attacks;     // The Armybranches this weapon can attack
  private int ammo;

  public Weapon(String name, String description, Range fireRange, int maxAmmo, boolean balistic, List<ArmyBranch> attacks) {
    this.name = name;
    this.description = description;
    this.fireRange = fireRange;
    this.maxAmmo = maxAmmo;
    this.balistic = balistic;
    this.attacks = attacks;
    init();
  }

  public void init() {
    String weaponName = "Weapon " + name;
    Args.checkForNull(attacks, weaponName + " cannot attack any armybranch");
    Args.checkForNull(fireRange, weaponName + " needs a range");
    Args.validate(fireRange.getMinRange() < 0, weaponName + " minRange should be positive");
    Args.validate(fireRange.getMaxRange() < 0, weaponName + " maxRange should be positive");
    Args.validate(maxAmmo < 0, weaponName + " maxAmmo should be positive");
    Args.checkForNull(description, "Please provide a description for " + weaponName);
  }

  /**
   * Copy constructor
   *
   * @param otherWeapon the weapon to be copied
   */
  Weapon(Weapon otherWeapon) {
    name = otherWeapon.name;
    description = otherWeapon.description;
    fireRange = otherWeapon.fireRange;
    maxAmmo = otherWeapon.maxAmmo;
    balistic = otherWeapon.balistic;
    attacks = new ArrayList<ArmyBranch>(otherWeapon.attacks);
    ammo = otherWeapon.ammo;
  }

  public void reset() {
    restock();
  }

  public void fire(int shots) {
    addAmmo(-shots);
  }

  public void restock() {
    setAmmo(maxAmmo);
  }

  public void addAmmo(int ammo) {
    setAmmo(this.ammo + ammo);
  }

  public void setAmmo(int ammo) {
    int oldAmmo = this.ammo;
    this.ammo = Args.getBetweenZeroMax(ammo, maxAmmo);
    firePropertyChange("ammo", oldAmmo, this.ammo);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Range getRange() {
    return fireRange;
  }

  public int getMaxAmmo() {
    return maxAmmo;
  }

  public int getAmmo() {
    return ammo;
  }

  public boolean canFireOn(ArmyBranch armyBranch) {
    return hasAmmoLeft() && attacks.contains(armyBranch);
  }

  /**
   * @return If this weapon has ammo and has the ability to attack. A flare for example is a a weapon that cannot attack.
   */
  public boolean canAttack() {
    return !attacks.isEmpty() && hasAmmoLeft();
  }

  public boolean hasAmmoLeft() {
    return ammo > 0;
  }

  public boolean isWithinRange(int x) {
    return fireRange.isInRange(x);
  }

  public int getAmmoPercentage() {
    return NumberUtil.calcPercentage(ammo, maxAmmo);
  }

  public boolean isBalistic() {
    return balistic;
  }

  /**
   * @return Can this weapon fire on adjacent enemies
   */
  public boolean isDirect() {
    return fireRange.isInRange(1);
  }

  /**
   * @return Can this weapon fire on enemies that are 1 or more tiles away
   */
  public boolean isInDirect() {
    return fireRange.getMinRange() >= 1 && fireRange.getMaxRange() > 1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Weapon)) return false;

    Weapon weapon = (Weapon) o;

    return name == weapon.name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return String.format("[name=%s ammo=%s/%s range=%s]", name, ammo, maxAmmo, fireRange);
  }
}
