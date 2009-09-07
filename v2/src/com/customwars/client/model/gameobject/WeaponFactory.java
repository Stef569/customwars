package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * A database(cache) of weapons. each weapon is mapped to an unique weapon ID.
 * The weapons in the cache contain values that always remain the same(static values) like: maxAmmo,...
 * getWeapon(id) will create a deep copy of the weapon in the cache and return it.
 *
 * When weapons are added init is invoked on them, this allows the weapon to validate it's values before it is used.
 * Each time a weapon is retrieved from this Factory reset is invoked, this allows the weapon to
 * put dynamic values to max and put values to default.
 *
 * @author stefan
 */
public class WeaponFactory {
  private static HashMap<Integer, Weapon> weapons = new HashMap<Integer, Weapon>();
  private static final Comparator<Weapon> SORT_WEAPON_ON_ID = new Comparator<Weapon>() {
    public int compare(Weapon weaponA, Weapon weaponB) {
      return weaponA.getID() - weaponB.getID();
    }
  };

  public static void addWeapons(Collection<Weapon> weapons) {
    for (Weapon weapon : weapons) {
      addWeapon(weapon);
    }
  }

  public static void addWeapon(Weapon weapon) {
    int weaponID = weapon.getID();
    if (weapons.containsKey(weaponID)) {
      throw new IllegalArgumentException("Weapon ID " + weaponID + " is already used by " + getWeapon(weaponID));
    }
    weapon.init();
    weapons.put(weaponID, weapon);
  }

  /**
   * Try to get a weapon based on a String value
   * both weapon name and ID values are supported.
   *
   * @param id the Identifier(weapon name or weapon id)
   * @return the Weapon or null if the weapon was not found
   */
  public static Weapon getWeapon(String id) {
    Weapon weapon = null;
    if (id == null) {
      return null;
    }
    if (id.trim().length() == 0) {
      return null;
    }

    Scanner scanner = new Scanner(id);
    if (scanner.hasNext()) {
      if (scanner.hasNextInt()) { // By Number
        weapon = getWeapon(scanner.nextInt());
      } else {                    // By String
        weapon = getWeaponByName(id.trim());
      }
    }
    return weapon;
  }

  public static Weapon getWeaponByName(String name) {
    for (Weapon weapon : weapons.values()) {
      if (weapon.getName().equalsIgnoreCase(name)) {
        return getWeapon(weapon.getID());
      }
    }
    return null;
  }

  public static Weapon getWeapon(int id) {
    if (!weapons.containsKey(id)) {
      throw new IllegalArgumentException("Weapon ID " + id + " is not cached " + weapons);
    }
    Weapon weapon = new Weapon(weapons.get(id));
    weapon.reset();
    return weapon;
  }

  /**
   * @return A Collection of all the weapons in this Factory sorted on weaponID
   */
  public static Collection<Weapon> getAllWeapons() {
    List<Weapon> weaponCopies = new ArrayList<Weapon>();
    for (Weapon weapon : weapons.values()) {
      weaponCopies.add(getWeapon(weapon.getID()));
    }
    Collections.sort(weaponCopies, SORT_WEAPON_ON_ID);
    return Collections.unmodifiableList(weaponCopies);
  }

  public static Weapon getRandomWeapon() {
    int rand = (int) (Math.random() * weapons.size());
    return getWeapon(rand);
  }

  public static int countWeapons() {
    return weapons.size();
  }

  public static void clear() {
    weapons.clear();
  }
}
