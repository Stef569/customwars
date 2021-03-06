package com.customwars.client.model.fight;

import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Range;

import java.util.List;

/**
 * Defines objects that can attack defenders
 */
public interface Attacker {
  void attack(Defender defender, Fight fight);

  boolean canAttack(Defender defender);

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

  boolean isInTransport();
}
