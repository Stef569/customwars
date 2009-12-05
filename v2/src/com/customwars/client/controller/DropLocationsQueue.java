package com.customwars.client.controller;

import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A Queue of drop locations
 *
 * Usage:
 * DropLocationsQueue queue = new DropLocationsQueue();
 * queue.addDropLocation(location, unit);
 * Tile t = queue.removeNextDropLocation ();
 * Unit u = queue.removeNextUnitToBeDropped ();
 */
public class DropLocationsQueue {
  private final LinkedList<Location> dropLocations;
  private final LinkedList<Unit> unitsToBeDropped;

  public DropLocationsQueue() {
    unitsToBeDropped = new LinkedList<Unit>();
    dropLocations = new LinkedList<Location>();
  }

  public void addDropLocation(Location location, Unit unit) {
    unitsToBeDropped.add(unit);
    dropLocations.add(location);
  }

  public boolean isUnitDropped(Unit unit) {
    return unitsToBeDropped.contains(unit);
  }

  public boolean isDropLocationTaken(Tile dropLocation) {
    return dropLocations.contains(dropLocation);
  }

  public void clearDropLocations() {
    dropLocations.clear();
    unitsToBeDropped.clear();
  }

  public Location removeNextDropLocation() {
    return dropLocations.removeFirst();
  }

  public Unit removeNextUnitToBeDropped() {
    return unitsToBeDropped.removeFirst();
  }

  public List<Unit> getUnitsToBeDropped() {
    return Collections.unmodifiableList(unitsToBeDropped);
  }

  public List<Location> getDropLocations() {
    return Collections.unmodifiableList(dropLocations);
  }
}
