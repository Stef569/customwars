package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;

import java.util.EnumSet;

/**
 * Checks to see if the co power can be executed
 */
public class CoRoutine implements AIRoutine {
  private final Unit unit;

  public CoRoutine(Unit unit) {
    this.unit = unit;
  }

  @Override
  public RoutineResult think() {
    Player player = unit.getOwner();

    if (player.isCOLoaded()) {
      CO co = player.getCO();

      if (co.canDoPower()) {
        return new RoutineResult(Fuz.UNIT_ORDER.DO_CO_POWER, null, null);
      }
    }

    return null;
  }

  @Override
  public EnumSet<Fuz.UNIT_ORDER> getSupportedOrders() {
    return EnumSet.of(Fuz.UNIT_ORDER.DO_CO_POWER);
  }
}
