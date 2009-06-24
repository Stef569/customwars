package com.customwars.client.model.map;

import com.customwars.client.model.Observable;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import tools.Args;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * A Tile is a location that contains a Terrain(required) and
 * can can be fogged. It can contain multiple locatables
 *
 * @author stefan
 */
public class Tile implements Location, Observable {
  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
  private final int col, row;
  private boolean fogged;
  private Terrain terrain;
  private List<Locatable> locatables;

  public Tile(int col, int row, Terrain terrain, boolean fogged) {
    this(col, row, terrain);
    this.fogged = fogged;
  }

  public Tile(int col, int row, Terrain terrain) {
    this.col = col;
    this.row = row;
    setTerrain(terrain);
    locatables = new ArrayList<Locatable>();
  }

  public boolean canAdd(Locatable locatable) {
    return locatable != null && !contains(locatable);
  }

  public boolean remove(Locatable locatable) {
    if (!contains(locatable) || locatable == null) {
      return false;
    } else {
      locatables.remove(locatable);
      firePropertyChange("locatable", locatable, null);
    }
    return true;
  }

  public boolean contains(Locatable locatable) {
    return locatables.contains(locatable);
  }

  public void add(Locatable locatable) {
    if (canAdd(locatable)) {
      locatables.add(locatable);
      locatable.setLocation(this);    // Keep locatable and tile in sync
    }
    firePropertyChange("locatable", null, locatable);
  }

  public void setTerrain(Terrain terrain) {
    Args.checkForNull(terrain, "Terrain is required");
    Terrain oldVal = this.terrain;
    this.terrain = terrain;
    firePropertyChange("terrain", oldVal, terrain);
  }

  public void setFogged(boolean fogged) {
    boolean oldVal = this.fogged;
    this.fogged = fogged;
    firePropertyChange("fog", oldVal, fogged);
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }

  public boolean isFogged() {
    return fogged;
  }

  public Terrain getTerrain() {
    return terrain;
  }

  public int getLocatableCount() {
    return locatables.size();
  }

  public Locatable getLastLocatable() {
    if (!locatables.isEmpty())
      return locatables.get(locatables.size() - 1);
    else
      return null;
  }

  public Locatable getLocatable(int index) {
    if (!locatables.isEmpty())
      return locatables.get(index);
    else
      return null;
  }

  public String getLocationString() {
    return col + "," + row;
  }

  public String toString() {
    return String.format("[(%s) fog=%s terrain=%s locatables=%s", getLocationString(), fogged, terrain, locatables);
  }

  void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }
}
