package com.customwars.client.model.gameobject;

/**
 * Different states a unit can be in, in one turn
 * At the start of each player turn the unit state is put back to IDLE for each owned unit.
 *
 * Transporting is not a unit state because it remains active at all times.
 */
public class UnitState {
  public static final int IDLE = 0;
  public static final int CAPTURING = 1;
  public static final int SUBMERGED = 2;
  public static final int STEALTH = 3;
}