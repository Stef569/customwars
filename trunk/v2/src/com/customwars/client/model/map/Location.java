package com.customwars.client.model.map;

import com.customwars.client.model.gameobject.Locatable;

/**
 * 2D location, containing locatables
 *
 * @author stefan
 */
public interface Location {
  /**
   * Checks if the specified Locatable may be added to this
   * <code>Location</code>.
   *
   * @param locatable The <code>Locatable</code> to add.
   * @return if the locatable can be added
   */
  public boolean canAdd(Locatable locatable);

  public void add(Locatable locatable);

  /**
   * Removes a <code>Locatable</code> from this Location.
   * When the <code>Locatable</code> is not on this location, nothing happens
   *
   * @return True if a Locatable has been removed, false if not
   */
  public boolean remove(Locatable locatable);

  /**
   * True if the locatable is on this Location
   */
  public boolean contains(Locatable locatable);

  /**
   * Last in first out
   *
   * @return The last added Locatable on this Location,
   *         null if there is no Locatable on this Location.
   */
  public Locatable getLastLocatable();

  /**
   * @return The Locatable on this Location by array index
   *         null if there are no Locatables on this Location.
   */
  public Locatable getLocatable(int index);

  public int getLocatableCount();

  public int getCol();

  public int getRow();
}
