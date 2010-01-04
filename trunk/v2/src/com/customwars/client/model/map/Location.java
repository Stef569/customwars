package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.Locatable;

import java.io.Serializable;

/**
 * 2D location, containing locatables
 *
 * @author stefan
 */
public interface Location extends Serializable {
  /**
   * Checks if the given Locatable may be added to this Location.
   *
   * @param locatable The Locatable to add.
   * @return if the locatable can be added
   */
  public boolean canAdd(Locatable locatable);

  /**
   * Add the locatable to this location
   * If this location already contains locatables then locatable is added to the end
   *
   * Precondition: canAdd(Locatable) returns true
   * PostCondition: getLastLocatable() returns the given locatable
   */
  public void add(Locatable locatable);

  /**
   * Removes a Locatable from this Location
   *
   * @return True if a Locatable has been removed
   */
  public boolean remove(Locatable locatable);

  /**
   * @return True if the given locatable is on this Location
   */
  public boolean contains(Locatable locatable);

  /**
   * Last in first out
   *
   * @return The last added Locatable on this Location,
   *         null if there is no Locatable on this Location
   */
  public Locatable getLastLocatable();

  /**
   * @return The Locatable on this Location by index
   *         null if there is no Locatable at the given index
   */
  public Locatable getLocatable(int index);

  /**
   * @return the amount of locatables on this location
   */
  public int getLocatableCount();

  /**
   * @return the location on the x axis in tiles
   */
  public int getCol();

  /**
   * @return The location on the y axis in tiles
   */
  public int getRow();

  /**
   * @return a short string identifying this location
   */
  String getLocationString();
}
