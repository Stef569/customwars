package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.ai.fuzzy.Fuz;

import java.util.EnumSet;

/**
 * A Routine is an object that can think about a situation and return the best result.
 *
 * In the case of CW AI it will think about the surroundings of a single unit.
 */
public interface AIRoutine {

  /**
   * The AI will evaluate the map and return the best result for the unit.
   * For example Capture city @ 1,1 with infantry @ 1,2
   *
   * @return the routine result or NULL if no result could be found
   */
  RoutineResult think();

  /**
   * Returns the unit orders enums that this AI routine can create.
   *
   * @return The unit orders that this AI routine supports
   */
  EnumSet<Fuz.UNIT_ORDER> getSupportedOrders();
}
