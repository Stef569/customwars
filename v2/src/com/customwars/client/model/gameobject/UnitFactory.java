package com.customwars.client.model.gameobject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Holds every unit in the game, getUnit returns deep copies
 *
 * @author stefan
 */
public class UnitFactory {
  private static HashMap<Integer, Unit> units = new HashMap<Integer, Unit>();

  public static void addUnits(Collection<Unit> units) {
    for (Unit unit : units) {
      addUnit(unit);
    }
  }

  public static void addUnit(Unit unit) {
    int unitID = unit.getID();
    if (units.containsKey(unitID)) {
      throw new IllegalArgumentException("Unit ID " + unitID + " is already used by " + getUnit(unitID));
    }
    unit.init();
    units.put(unit.getID(), unit);
  }

  public static Unit getUnit(int id) {
    if (!units.containsKey(id)) {
      throw new IllegalArgumentException("Unit ID " + id + " is not cached " + units);
    }
    Unit unit = new Unit(units.get(id));
    unit.reset();
    return unit;
  }

  public static Collection<Unit> getAllUnits() {
    return Collections.unmodifiableCollection(units.values());
  }

  /**
   * Retrieve a random unit
   * Only works if unit ID's are linear starting from 0
   */
  public static Unit getRandomUnit() {
    int rand = (int) (Math.random() * units.size());
    return getUnit(rand);
  }

  public static int countUnits() {
    return units.size();
  }

  public static void clear() {
    units.clear();
  }
}
