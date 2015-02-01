package com.customwars.client.model.ai.unit;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.map.Location;

public class UnitOrder {
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
   * If the unit should not move, the move destination is the location of the unit.
   */
  public final Location moveDestination;

  /**
   * A special target destination after moving.
   * For example:
   * The destination of the rocket fired from a silo
   * The unit to attack for indirects
   */
  public final Location target;

  public UnitOrder(Fuz.UNIT_ORDER order, Location unitLocation, Location moveDestination) {
    this(order, unitLocation, moveDestination, null);
  }

  public UnitOrder(Fuz.UNIT_ORDER order, Location unitLocation, Location moveDestination, Location target) {
    this.order = order;
    this.unitLocation = unitLocation;
    this.moveDestination = moveDestination;
    this.target = target;
  }

}
