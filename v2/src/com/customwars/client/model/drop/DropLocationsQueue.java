package com.customwars.client.model.drop;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Queue of drop locations
 *
 * Usage:
 * DropLocationsQueue queue = new DropLocationsQueue();
 * queue.addDropLocation(location, unit);
 * queue.addDropLocation(location, unit);
 * List<DropLocation> dropLocations = queue.getDropLocations();
 */
public class DropLocationsQueue {
  private final List<DropLocation> dropLocations;

  public DropLocationsQueue() {
    dropLocations = new ArrayList<DropLocation>(4);
  }

  /**
   * Queue the unit - droplocation pair
   *
   * @param location The location where the unit is to be dropped on
   * @param unit     The unit that is to be dropped on the location
   */
  public void addDropLocation(Location location, Unit unit) {
    dropLocations.add(new DropLocation(unit, location));
  }

  /**
   * Is the given unit already assigned to be dropped on a tile
   */
  public boolean isUnitDropped(Unit unit) {
    for (DropLocation dropLocation : dropLocations) {
      if (dropLocation.containsUnit(unit)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Is the dropLocation already taken by a previous drop
   */
  public boolean isDropLocationTaken(Tile location) {
    for (DropLocation dropLocation : dropLocations) {
      if (dropLocation.containsTile(location)) {
        return true;
      }
    }
    return false;
  }

  public List<DropLocation> getDropLocations() {
    return Collections.unmodifiableList(dropLocations);
  }

  public List<Location> getDropTiles() {
    List<Location> dropTiles = new ArrayList<Location>(dropLocations.size());

    for (DropLocation dropLocation : dropLocations) {
      dropTiles.add(dropLocation.getLocation());
    }
    return dropTiles;
  }

  public void clearDropLocations() {
    dropLocations.clear();
  }
}
