package com.client.model.api.fight;

import com.client.model.api.ArmyBranch;
import com.client.model.api.map.Location;
import com.client.model.api.map.Range;

import java.util.List;

/**
 * Defines objects that can attack defenders
 */
public interface Attacker {
  void attack(Defender defender, Fight fight);

  boolean canAttack(Defender defender);

  ArmyBranch getArmyBranch();

  Range getAttackRange();

  List<Location> getAttackZone();

  void setAttackZone(List<Location> attackZone);

  boolean isWithinAttackZone(Location location);

  Location getLocation();

  boolean isWithinMoveZone(Location location);

  /**
   * @return A list of locations where this unit can move in
   */
  List<Location> getMoveZone();
}
