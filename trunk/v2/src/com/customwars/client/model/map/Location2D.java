package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.Locatable;

/**
 * A Location that has a Col and Row
 */
public class Location2D extends AbstractLocation {
  private static final long serialVersionUID = 1L;

  public Location2D(int col, int row) {
    super(col, row);
  }

  public boolean canAdd(Locatable locatable) {
    return false;
  }

  public void add(Locatable locatable) {
  }

  public boolean remove(Locatable locatable) {
    return false;
  }

  public boolean contains(Locatable locatable) {
    return false;
  }

  public Locatable getLastLocatable() {
    return null;
  }

  public Locatable getLocatable(int index) {
    return null;
  }

  public int getLocatableCount() {
    return 0;
  }
}