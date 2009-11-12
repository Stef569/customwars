package com.customwars.client.controller;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Handles interaction with game objects in map editor mode
 */
public interface MapEditorControl {
  /**
   * Add a new game object of ID to the given Tile t owned by the player p
   * The impl will interpret the ID
   */
  void addToTile(Tile t, int ID, Player p);

  /**
   * Remove a game object from the Tile t
   */
  void removeFromTile(Tile t);

  /**
   * Completely fill a map with game objects
   */
  void fillMap(Map<Tile> map, int id);

  /**
   * @return true when the impl supports the gameobject of type c
   */
  boolean isTypeOf(Class c);
}
