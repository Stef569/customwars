package com.customwars.client.model.gameobject;

import com.customwars.client.model.map.Range;
import tools.Args;
import tools.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A weapon has an amount of ammo, it can fire within a fire range
 * It can only attack ArmyBranches defined in attacks
 *
 * @author Stefan
 */
public class Weapon extends GameObject {
  public static int UNLIMITED_AMMO = 99;
  private int id;
  private String name;
  private String description;
  private Range fireRange;
  private int maxAmmo;
  private boolean balistic;         // Gives this weapon indirect firing ability even after moving
  private List<Integer> attacks;    // The Armybranches this weapon can attack
  private int ammo;

  public Weapon(int id, String name, String description, Range fireRange, int maxAmmo, boolean balistic, List<Integer> attacks) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.fireRange = fireRange;
    this.maxAmmo = maxAmmo;
    this.balistic = balistic;
    this.attacks = attacks;
    init();
  }

  public void init() {
    Args.checkForNull(attacks, "Weapon " + name + " cannot attack any armybranch");
    Args.validate(attacks.size() == 0, "Weapon " + name + " cannot attack any armybranch");
    if (fireRange == null) fireRange = new Range(0, 0);
    Args.validate(fireRange.getMinRange() < 0, "minRange should be positive");
    Args.validate(fireRange.getMaxRange() < 0, "maxRange should be positive");
    Args.validate(maxAmmo < 0, "maxAmmo should be positive");
  }

  /**
   * Copy constructor
   *
   * @param otherWeapon the weapon to be copied
   */
  Weapon(Weapon otherWeapon) {
    id = otherWeapon.id;
    name = otherWeapon.name;
    description = otherWeapon.description;
    fireRange = otherWeapon.fireRange;
    maxAmmo = otherWeapon.maxAmmo;
    balistic = otherWeapon.balistic;
    attacks = new ArrayList<Integer>(otherWeapon.attacks);
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

  public int getID() {
    return id;
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

  public boolean canFire(int armyBranch) {
    return hasAmmoLeft() && attacks.contains(armyBranch);
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

  @Override
  public String toString() {
    return String.format("[name=%s id=%s ammo=%s/%s range=%s]", name, id, ammo, maxAmmo, fireRange);
  }
}
