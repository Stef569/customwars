package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.GameInformation;
import com.customwars.client.model.gameobject.Unit;

import java.util.EnumSet;

/**
 * Checks to see if the co power can be executed
 */
public class CoRoutine implements AIRoutine {
  private final Unit unit;
  private final GameInformation data;

  public CoRoutine(Unit unit, GameInformation data) {
    this.unit = unit;
    this.data = data;
  }

  @Override
  public RoutineResult think() {
    return null;
  }

  @Override
  public EnumSet<Fuz.UNIT_ORDER> getSupportedOrders() {
    return EnumSet.of(Fuz.UNIT_ORDER.DO_CO_POWER);
  }
}
