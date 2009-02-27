package com.customwars.client.model.gameobject;

/**
 * The different states a gameObject can be in, at all times a gameObject is in one of these states
 * Some GameObjects never go out of the IDLE state(map) some will at some time be in a different state(unit, game)
 *
 * @author stefan
 */
public enum GameObjectState {
  IDLE,       // Created, default state
  ACTIVE,     // Allows input
  DESTROYED,  // Can be removed, ended
}
