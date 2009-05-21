package com.customwars.client.MapMaker.control;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

public class UnitMapEditorControl implements MapEditorControl {

  public void addToTile(Tile t, int id, Player player) {
    Unit unit = UnitFactory.getUnit(id);
    player.addUnit(unit);
    unit.setLocation(t);
    t.add(unit);
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
