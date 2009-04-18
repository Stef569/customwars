package com.customwars.client.MapMaker;

import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * @author stefan
 */
public interface MapEditorControl {
  void addToTile(Tile t, int id);

  void removeFromTile(Tile t);

  void fillMap(Map<Tile> map, int id);

  boolean isTypeOf(Class c);
}
