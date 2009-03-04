package com.customwars.client.model.map.path;

import com.customwars.client.model.map.Location;

public interface MoveStrategy {
  /**
   * @return the movement cost required to traverse a particular location.
   */
  public int getMoveCost(Location location);
}
