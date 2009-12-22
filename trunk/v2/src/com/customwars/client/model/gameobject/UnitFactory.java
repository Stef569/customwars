package com.customwars.client.model.gameobject;

import com.customwars.client.tools.UCaseMap;
import org.apache.log4j.Logger;

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
  private static final Logger logger = Logger.getLogger(UnitFactory.class);
  private static final Map<Integer, Unit> unitsByID = new HashMap<Integer, Unit>();
  private static final Map<String, Unit> unitsByName = new UCaseMap<Unit>();
  private static final Comparator<Unit> SORT_UNIT_ON_ID = new Comparator<Unit>() {
    public int compare(Unit unitA, Unit unitB) {
      return unitA.getStats().getID() - unitB.getStats().getID();
    }
  };

  public static void addUnits(Collection<UnitStats> unitStats) {
    for (UnitStats unitStat : unitStats) {
      Unit unit = new Unit(unitStat);
      addUnit(unit);
    }
  }

  public static void addUnit(Unit unit) {
    int unitID = unit.getStats().getID();
    String unitName = unit.getStats().getName();
    searchForDuplicates(unitName, unitID);
    armUnit(unit);
    unit.init();
    addUnit(unitName, unitID, unit);
  }

  private static void searchForDuplicates(String unitName, int unitID) {
    if (unitsByID.containsKey(unitID)) {
      Unit duplicateUnit = getUnit(unitID);
      throw new IllegalArgumentException("Unit ID " + unitID + " is already used by " + duplicateUnit);
    }

    if (unitsByName.containsKey(unitName)) {
      Unit duplicateUnit = getUnit(unitName);
      throw new IllegalArgumentException("Unit name " + unitName + " is already used by " + duplicateUnit);
    }
  }

  private static void armUnit(Unit unit) {
    String priWeaponName = unit.getStats().getPrimaryWeaponName();
    String secWeaponName = unit.getStats().getSecondaryWeaponName();

    if (WeaponFactory.hasWeapon(priWeaponName)) {
      Weapon primaryWeapon = WeaponFactory.getWeapon(priWeaponName);
      unit.setPrimaryWeapon(primaryWeapon);
    } else if (priWeaponName != null) {
      logger.warn("The primary weapon " + priWeaponName + " could not be found in the WeaponFactory");
    }

    if (WeaponFactory.hasWeapon(secWeaponName)) {
      Weapon secondaryWeapon = WeaponFactory.getWeapon(secWeaponName);
      unit.setSecondaryWeapon(secondaryWeapon);
    } else if (secWeaponName != null) {
      logger.warn("The secondary weapon " + secWeaponName + " could not be found in the WeaponFactory");
    }
  }

  private static void addUnit(String unitName, int unitID, Unit unit) {
    unitsByID.put(unitID, unit);
    unitsByName.put(unitName, unit);
  }

  public static Unit getUnit(int id) {
    if (!unitsByID.containsKey(id)) {
      throw new IllegalArgumentException("Unit ID " + id + " is not cached " + unitsByID.keySet());
    }
    Unit unit = new Unit(unitsByID.get(id));
    unit.reset();
    return unit;
  }

  public static Unit getUnit(String unitName) {
    if (!unitsByName.containsKey(unitName)) {
      throw new IllegalArgumentException("Unit name " + unitName + " is not cached " + unitsByName.keySet());
    }
    Unit unit = new Unit(unitsByName.get(unitName));
    unit.reset();
    return unit;
  }

  /**
   * @return A Collection of all the units in this Factory sorted on unitID
   */
  public static List<Unit> getAllUnits() {
    List<Unit> unitCopies = new ArrayList<Unit>(unitsByID.values().size());

    for (Unit unit : unitsByID.values()) {
      int unitID = unit.getStats().getID();
      unitCopies.add(getUnit(unitID));
    }
    Collections.sort(unitCopies, SORT_UNIT_ON_ID);
    return Collections.unmodifiableList(unitCopies);
  }

  /**
   * @return a random unit
   *         Only works if unit ID's are linear starting from 0
   */
  public static Unit getRandomUnit() {
    int rand = (int) (Math.random() * unitsByID.size());
    return getUnit(rand);
  }

  public static boolean hasUnitForID(int unitID) {
    return unitsByID.containsKey(unitID);
  }

  public static int countUnits() {
    return unitsByID.size();
  }

  public static void clear() {
    unitsByID.clear();
    unitsByName.clear();
  }
}
