package com.customwars.client.model.gameobject;

/**
 * @author stefan
 */
public enum GameObjectState {
  IDLE,       // Created, not in another state
  ACTIVE,     // Can be controlled by the player
  DESTROYED,  // Can be removed
}
