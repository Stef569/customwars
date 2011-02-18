package com.customwars.client.controller.mapeditor;

import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.awt.Color;

/**
 * Handles interaction with game objects in map editor mode
 */
public interface MapEditorControl {
  /**
   * Add a new game object of ID to the given Tile t in the color 'color'
   * The impl will turn the ID into an object
   */
  void addToTile(Tile t, int ID, Color color);

  /**
   * Remove a game object from the Tile t
   *
   * @return if the game object has been removed
   */
  boolean removeFromTile(Tile t);

  /**
   * Completely fill a map with game objects
   */
  void fillMap(Map map, int id);

  /**
   * @return true when the impl supports the gameobject of type c
   */
  boolean isTypeOf(Class c);
}
