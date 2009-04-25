package com.customwars.client.MapMaker.control;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

public interface MapEditorControl {
  void addToTile(Tile t, int id, Player player);

  void removeFromTile(Tile t);

  void fillMap(Map<Tile> map, int id);

  boolean isTypeOf(Class c);
}
