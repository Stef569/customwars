package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.map.Location;

/**
 * The result of a routine
 */
public class RoutineResult {
  /**
   * The order the AI unit should execute
   */
  public final Fuz.UNIT_ORDER order;

  /**
   * The location of the unit
   */
  public final Location unitLocation;

  /**
   * The destination of the move.
   * The location of the unit if the unit should not move.
   */
  public final Location moveDestination;

  /**
   * A special target destination after moving.
   * For example
   * The destination of the silo rocket
   * The unit to attack for indirects
   */
  public final Location target;
  public String debug;

  public RoutineResult(Fuz.UNIT_ORDER order, Location unitLocation, Location moveDestination, Location target) {
    this.order = order;
    this.unitLocation = unitLocation;
    this.moveDestination = moveDestination;
    this.target = target;
  }

  public RoutineResult(Fuz.UNIT_ORDER order, Location unitLocation, Location moveDestination) {
    this(order, unitLocation, moveDestination, null);
  }

  @Override
  public String toString() {
    return unitLocation.getLastLocatable() + " " +
      order + " " +
      moveDestination.getLocationString() + " " +
      (target != null ? target.getLocationString() : "");
  }
}
