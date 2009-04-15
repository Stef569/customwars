package com.customwars.client.model.gameobject;

/**
 * Different states a unit can be in, in one turn
 * Transporting is not a unit state because it remains active while transporting.
 */
public enum UnitState {
  IDLE, CAPTURING, SUBMERGED, STEALTH
}