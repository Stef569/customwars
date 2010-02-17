package com.customwars.client.model.drop;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;

/**
 * Holds a reference to the drop location and the unit that is to be dropped on that location.
 */
public class DropLocation {
  private final Unit unitToBeDropped;
  private final Location dropLocation;

  public DropLocation(Unit unitToBeDropped, Location dropLocation) {
    this.unitToBeDropped = unitToBeDropped;
    this.dropLocation = dropLocation;
  }

  public Unit getUnit() {
    return unitToBeDropped;
  }

  public Location getLocation() {
    return dropLocation;
  }

  public boolean containsUnit(Unit unit) {
    return unitToBeDropped.equals(unit);
  }

  public boolean containsTile(Tile t) {
    return dropLocation.equals(t);
  }
}