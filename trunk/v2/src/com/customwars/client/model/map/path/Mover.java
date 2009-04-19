package com.customwars.client.model.map.path;

import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;

import java.util.List;

/**
 * An Objects that can move between tiles
 *
 * @author stefan
 */
public interface Mover extends Locatable {
  /**
   * Could be: Tires, Tread, Naval, Ship, Transport,...
   * This can be used to lookup the moveCost for this Mover
   *
   * @return The number representing the movementType
   */
  int getMovementType();

  /**
   * @return the amount of tiles that this Mover can move
   */
  int getMovePoints();

  /**
   * @param cost the cost to make a move in a path
   */
  void addPathMoveCost(int cost);

  /**
   * This method always returns a valid List, it will never return null
   *
   * @return A list of Locations in which the Mover can make a move
   *         Including the mover Location
   */
  List<Location> getMoveZone();

  void setMoveZone(List<Location> moveZone);

  void setOrientation(Direction direction);

  MoveStrategy getMoveStrategy();

  boolean isWithinMoveZone(Location location);

  boolean canMove();

  /**
   * @return if the location contains a trapper
   */
  boolean hasTrapperOn(Location location);
}
