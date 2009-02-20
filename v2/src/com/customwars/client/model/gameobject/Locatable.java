package com.customwars.client.model.gameobject;

import com.customwars.client.model.map.Location;

/**
 * Defines Objects that can be put on a Location
 */
public interface Locatable {
  /**
   * Set the location for this <code>Locatable</code>.
   *
   * @param newLocation The new <code>Location</code> for the <code>Locatable</code>.
   */
  void setLocation(Location newLocation);

  /**
   * Get the location of this <code>Locatable</code>.
   *
   * @return The location of this <code>Locatable</code>.
   */
  Location getLocation();
}
