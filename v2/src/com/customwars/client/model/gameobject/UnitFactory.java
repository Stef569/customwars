package com.customwars.client.model.gameobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database(cache) of units that can be used in the game. each unit is mapped to an unique Unit ID.
 * The units in the cache contain values that always remain the same(static values) like: description, maxHP,...
 * getUnit(id) will create a deep copy of the unit in the cache and return it.
 *
 * When units are added init is invoked on them, this allows the unit to validate it's values before it is used.
 * Each time a unit is retrieved from this Factory reset is invoked, this allows the unit to
 * put dynamic values to max and put values to default.
 *
 * @author stefan
 */
public class UnitFactory {
  private static final Map<Integer, Unit> units = new HashMap<Integer, Unit>();
  private static final Comparator<Unit> SORT_UNIT_ON_ID = new Comparator<Unit>() {
    public int compare(Unit unitA, Unit unitB) {
      return unitA.getStats().getID() - unitB.getStats().getID();
    }
  };

  public static void addUnits(Collection<UnitStats> unitStats) {
    for (UnitStats unitStat : unitStats) {
      Unit unit = new Unit(unitStat);
      armUnit(unit);
      addUnit(unit);
    }
  }

  private static void armUnit(Unit unit) {
    String priWeaponName = unit.getStats().getPrimaryWeaponName();
    String secWeaponName = unit.getStats().getSecondaryWeaponName();

    if (WeaponFactory.hasWeapon(priWeaponName)) {
      Weapon primaryWeapon = WeaponFactory.getWeapon(priWeaponName);
      unit.setPrimaryWeapon(primaryWeapon);
    }

    if (WeaponFactory.hasWeapon(secWeaponName)) {
      Weapon secondaryWeapon = WeaponFactory.getWeapon(secWeaponName);
      unit.setSecondaryWeapon(secondaryWeapon);
    }
  }

  public static void addUnit(Unit unit) {
    int unitID = unit.getStats().getID();
    if (units.containsKey(unitID)) {
      throw new IllegalArgumentException("Unit ID " + unitID + " is already used by " + getUnit(unitID));
    }
    unit.init();
    units.put(unit.getStats().getID(), unit);
  }

  public static Unit getUnit(int id) {
    if (!units.containsKey(id)) {
      throw new IllegalArgumentException("Unit ID " + id + " is not cached " + units);
    }
    Unit unit = new Unit(units.get(id));
    unit.reset();
    return unit;
  }

  /**
   * @return A Collection of all the units in this Factory sorted on unitID
   */
  public static List<Unit> getAllUnits() {
    List<Unit> unitCopies = new ArrayList<Unit>(units.values().size());

    for (Unit unit : units.values()) {
      unitCopies.add(getUnit(unit.getStats().getID()));
    }
    Collections.sort(unitCopies, SORT_UNIT_ON_ID);
    return Collections.unmodifiableList(unitCopies);
  }

  /**
   * @return a random unit
   *         Only works if unit ID's are linear starting from 0
   */
  public static Unit getRandomUnit() {
    int rand = (int) (Math.random() * units.size());
    return getUnit(rand);
  }

  public static boolean hasUnitForID(int unitID) {
    return units.containsKey(unitID);
  }

  public static int countUnits() {
    return units.size();
  }

  public static void clear() {
    units.clear();
  }
}
