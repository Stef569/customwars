package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.gameobject.Unit;

import java.util.EnumSet;

/**
 * Controls a sub in the game.
 */
public class SubMarineRoutine implements AIRoutine {
  private Unit unit;

  public SubMarineRoutine(Unit unit) {
    this.unit = unit;
  }

  /**
   * A sub must surface when it's supplies are < 30
   * A sub must dive if it has more 30 supplies
   */
  @Override
  public RoutineResult think() {
    if (unit.canDive()) {
      int supplies = unit.getSuppliesPercentage();

      if (supplies < 30) {
        return new RoutineResult(Fuz.UNIT_ORDER.SURFACE, unit.getLocation(), unit.getLocation());
      } else {
        if (!unit.isSubmerged()) {
          return new RoutineResult(Fuz.UNIT_ORDER.SUBMERGE, unit.getLocation(), unit.getLocation());
        }
      }
    }

    return null;
  }

  @Override
  public EnumSet<Fuz.UNIT_ORDER> getSupportedOrders() {
    return EnumSet.of(Fuz.UNIT_ORDER.SUBMERGE, Fuz.UNIT_ORDER.SURFACE);
  }
}
