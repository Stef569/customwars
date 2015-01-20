package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.GameInformation;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;

import java.util.List;

/**
 * Check if the current unit must be healed/repaired.
 */
public class HealRoutine implements AIRoutine {
  private final GameInformation data;
  private final Unit unit;

  public HealRoutine(Unit unit, GameInformation data) {
    this.unit = unit;
    this.data = data;
  }

  /**
   * A unit must be healed if
   * 1. The hp is lower then 40%
   * 2. A city in range can heal the unit
   */
  @Override
  public RoutineResult think() {
    if (unit.getHpPercentage() < 40) {
      List<City> citiesInRange = data.getCitiesInRangeOf(unit);

      if (citiesInRange != null) {
        for (City city : citiesInRange) {
          if (city.canHeal(unit)) {
            return new RoutineResult(Fuz.UNIT_ORDER.HEAL, unit.getLocation(), city.getLocation());
          }
        }
      }
    }

    return null;
  }
}
