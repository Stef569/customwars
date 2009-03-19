package com.customwars.client.model.gameobject;

import tools.Args;

/**
 * A weapon has an amount of ammo, it can fire within a fire range
 *
 * @author Stefan
 */
public class Weapon extends GameObject {
  public static int UNLIMITED_AMMO = 99;
  private int id;
  private String name;
  private String description;
  private int price;
  private int minFireRange;
  private int maxFireRange;
  private int maxAmmo;
  private boolean balistic;   // Gives this weapon indirect firing ability even after moving
  private int ammo;

  public Weapon(int id, String name, String description, int price, int minRange, int maxRange, int maxAmmo, boolean balistic) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.minFireRange = minRange;
    this.maxFireRange = maxRange;
    this.maxAmmo = maxAmmo;
    this.balistic = balistic;
    init();
  }

  public void init() {
    Args.validate(minFireRange < 0, "minRange should be positive");
    Args.validate(maxFireRange < 0, "maxRange should be positive");
    Args.validate(maxFireRange < minFireRange, "minRange should be smaller then maxRange");
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
    price = otherWeapon.price;
    minFireRange = otherWeapon.minFireRange;
    maxFireRange = otherWeapon.maxFireRange;
    maxAmmo = otherWeapon.maxAmmo;
    balistic = otherWeapon.balistic;
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

  public int getPrice() {
    return price;
  }

  public int getMinRange() {
    return minFireRange;
  }

  public int getMaxRange() {
    return maxFireRange;
  }

  public int getMaxAmmo() {
    return maxAmmo;
  }

  public int getAmmo() {
    return ammo;
  }

  public boolean isWithinRange(int range) {
    return range >= minFireRange && range <= maxFireRange;
  }

  /**
   * @return true if this weapon has no ammo left
   */
  public boolean isDepleted() {
    return (ammo <= 0);
  }

  /**
   * @return the price it would cost to restock the ammo for this weapon.
   */
  public int getRestockPrice() {
    return ((maxAmmo - ammo) * price);
  }

  public int getAmmoPercentage() {
    int percentage;
    if (maxAmmo <= 0) {
      percentage = 100;
    } else {
      double divide = (double) ammo / maxAmmo;
      percentage = (int) Math.round(divide * 100);
    }
    return percentage;
  }

  public boolean isBalistic() {
    return balistic;
  }

  @Override
  public String toString() {
    return "[name=" + name + " id=" + id + " ammo=" + ammo + "]";
  }
}
