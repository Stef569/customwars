package com.customwars.client.model;

import com.customwars.client.model.map.Location;

import java.util.List;

/**
 * Defines objects that can attack defenders
 * an attack can result in an counter attack
 *
 * @author stefan
 */
public interface Attacker {
  void attack(Defender defender, Fight fight);

  boolean canAttack(Defender defender);

  int getMinAttackRange();

  int getMaxAttackRange();

  List<Location> getAttackZone();

  void setAttackZone(List<Location> attackZone);

  boolean isWithinAttackZone(Location location);

  Location getLocation();

  boolean isWithinMoveZone(Location location);

  List<Location> getMoveZone();
}
