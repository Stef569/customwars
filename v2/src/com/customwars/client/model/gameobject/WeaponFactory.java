package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Holds every weapon used in the game, getWeapon returns deep copies
 * Weapons are inited and reset so they are ready to use.
 *
 * @author stefan
 */
public class WeaponFactory {
  private static HashMap<Integer, Weapon> weapons = new HashMap<Integer, Weapon>();

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

  public static Collection<Weapon> getAllWeapons() {
    List<Weapon> unitCopies = new ArrayList<Weapon>();
    for (Weapon weapon : weapons.values()) {
      unitCopies.add(getWeapon(weapon.getID()));
    }
    return Collections.unmodifiableList(unitCopies);
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
