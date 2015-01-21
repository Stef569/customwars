package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;

/**
 * Check if a join can be performed
 */
public class JoinRoutine implements AIRoutine {
  private final Game game;
  private Unit unit;

  public JoinRoutine(Game game, Unit unit) {
    this.game = game;
    this.unit = unit;
  }

  /**
   * A unit must be Joined if
   * 1. The hp is lower then 50%
   * 2. Another active unit of the same type is in range
   */
  @Override
  public RoutineResult think() {
    for (Unit otherUnit : game.getActivePlayer().getArmy()) {
      if (unit.getStats().getID() == otherUnit.getStats().getID()) {
        if (unit != otherUnit) {
          if (unit.getMoveZone().contains(otherUnit.getLocation())) {
            if (unit.isActive() && otherUnit.isActive()) {
              boolean unitIsDamaged = unit.getHpPercentage() < 50;
              boolean otherUnitIsDamaged = otherUnit.getHpPercentage() < 50;

              if (unitIsDamaged || otherUnitIsDamaged) {
                return new RoutineResult(Fuz.UNIT_ORDER.JOIN, unit.getLocation(), otherUnit.getLocation());
              }
            }
          }
        }
      }
    }

    return null;
  }
}
