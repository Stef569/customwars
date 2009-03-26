package com.customwars.client.model;

import com.customwars.client.model.game.Player;

/**
 * Defines objects that can take damage and can defend themselfs from an attack
 * when canCounterAttack return true a counter attack is performed.
 *
 * @author stefan
 */
public interface Defender {
  void defend(Attacker attacker, Fight fight);

  boolean canCounterAttack();

  boolean isDestroyed();

  Player getOwner();
}
