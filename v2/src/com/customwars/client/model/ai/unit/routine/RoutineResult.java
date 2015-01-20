package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.map.Location;

/**
 * The result of a routine
 */
public class RoutineResult {
  public final Fuz.UNIT_ORDER order;
  public final Location unitLocation;
  public final Location destination;
  public final Location destination2;
  public String debug;

  public RoutineResult(Fuz.UNIT_ORDER order, Location unitLocation, Location destination, Location destination2) {
    this.order = order;
    this.unitLocation = unitLocation;
    this.destination = destination;
    this.destination2 = destination2;
  }

  public RoutineResult(Fuz.UNIT_ORDER order, Location unitLocation, Location destination) {
    this(order, unitLocation, destination, null);
  }

  @Override
  public String toString() {
    return unitLocation.getLastLocatable() + " " +
        order + " " +
        destination.getLocationString() + " " +
        (destination2 != null ? destination2.getLocationString() : "");
  }
}
