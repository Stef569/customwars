package com.customwars.client.model.gameobject;

import com.customwars.client.tools.StringUtil;
import com.customwars.client.tools.UCaseMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
  private static final Map<String, Weapon> weapons = new UCaseMap<Weapon>();

  public static void addWeapons(Collection<Weapon> weapons) {
    for (Weapon weapon : weapons) {
      addWeapon(weapon);
    }
  }

  public static void addWeapon(Weapon weapon) {
    String weaponID = weapon.getName();
    if (weapons.containsKey(weaponID)) {
      throw new IllegalArgumentException("Weapon ID " + weaponID + " is already used by " + getWeapon(weaponID));
    }
    weapon.init();
    weapons.put(weaponID, weapon);
  }

  /**
   * Try to get a weapon based on a String value
   *
   * @param weaponID the name of the weapon to retrieve, case insensitive
   * @return the Weapon by ID
   */
  public static Weapon getWeapon(String weaponID) {
    if (StringUtil.hasContent(weaponID) && weapons.containsKey(weaponID)) {
      Weapon weapon = new Weapon(weapons.get(weaponID));
      weapon.reset();
      return weapon;
    } else {
      throw new IllegalArgumentException("Weapon ID " + weaponID + " is not cached " + weapons.keySet());
    }
  }

  /**
   * Determine if a weapon is present for weaponName
   *
   * @param weaponID the name of the weapon to check, case insensitive
   * @return if the weapon keyed by weaponName is present in this Factory
   */
  public static boolean hasWeapon(String weaponID) {
    return weapons.containsKey(weaponID);
  }

  /**
   * @return A Collection of all the weapons in this Factory
   */
  public static Collection<Weapon> getAllWeapons() {
    List<Weapon> weaponCopies = new ArrayList<Weapon>();
    for (Weapon weapon : weapons.values()) {
      weaponCopies.add(getWeapon(weapon.getName()));
    }
    return Collections.unmodifiableList(weaponCopies);
  }

  public static int countWeapons() {
    return weapons.size();
  }

  public static void clear() {
    weapons.clear();
  }
}
