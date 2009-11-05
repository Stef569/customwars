package com.customwars.client.model.fight;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Location;

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