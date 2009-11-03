package com.client.model.api.fight;

import com.client.model.api.ArmyBranch;
import com.client.model.api.gameobject.Player;
import com.client.model.api.map.Location;

/**
 * Defines objects that can take damage and can counter attack
 */
public interface Defender {
  void defend(Attacker attacker, Fight fight);

  boolean canCounterAttack(Attacker attacker);

  boolean isDestroyed();

  Player getOwner();

  ArmyBranch getArmyBranch();

  Location getLocation();
}
