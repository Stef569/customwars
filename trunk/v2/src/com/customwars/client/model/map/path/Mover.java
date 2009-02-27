package com.customwars.client.model.map.path;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;

import java.util.List;

/**
 * Defines Objects that can move between tiles
 *
 * @author stefan
 */
public interface Mover extends Locatable {
  /**
   * Could be: Tires, Tread, Naval, Ship, Transport,...
   * This can be used to lookup to moveCost for this Mover
   *
   * @return The number representing the movementType
   */
  int getMovementType();

  /**
   * @return the amount of tiles that this Mover can move
   */
  int getMovement();

  void addPathMoveCost(int moveCost);

  /**
   * @return A list of Locations in which the Mover can make a move
   */
  List<Location> getMoveZone();

  void setMoveZone(List<Location> moveZone);

  void setOrientation(Direction direction);

  MovementCost getMoveStrategy();

  boolean isWithinMoveZone(Location location);

  Player getOwner();
}
