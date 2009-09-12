package com.customwars.client.MapMaker.control;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import tools.MapUtil;

public class UnitMapEditorControl implements MapEditorControl {
  private final TileMap<Tile> map;

  public UnitMapEditorControl(TileMap<Tile> map) {
    this.map = map;
  }

  public void addToTile(Tile t, int id, Player player) {
    MapUtil.addUnitToMap(map, t, id, player);
  }

  public void removeFromTile(Tile t) {
    Unit unit = (Unit) t.getLastLocatable();
    unit.destroy();
  }

  public void fillMap(Map<Tile> map, int id) {
    // map can't be filled with units
  }

  public boolean isTypeOf(Class c) {
    return c == Unit.class;
  }
}
