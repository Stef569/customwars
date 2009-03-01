package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Location;

public interface MoveStrategy {
  /**
   * Get the movement cost required to traverse a particular location with a mover.
   *
   * @return the cost required for the current mover to traverse tile at (col, row).
   */
  public int getMoveCost(Location location);
}
