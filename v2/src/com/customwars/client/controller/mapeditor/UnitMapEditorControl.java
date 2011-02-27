package com.customwars.client.controller.mapeditor;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.MapUtil;

import java.awt.Color;

/**
 * Add/Remove units from a map in map editor mode
 */
public class UnitMapEditorControl implements MapEditorControl {
  private final Map map;

  public UnitMapEditorControl(Map map) {
    this.map = map;
  }

  public void addToTile(Tile t, int unitID, Color color) {
    Player unitOwner = map.getPlayer(color);
    removePreviousUnit(t);
    MapUtil.addUnitToMap(map, t, unitID, unitOwner);
  }

  private void removePreviousUnit(Tile t) {
    if (t.getLocatableCount() > 0) {
      Unit unit = map.getUnitOn(t);
      unit.getOwner().removeUnit(unit);
      t.remove(unit);
    }
  }

  public boolean removeFromTile(Tile t) {
    if (t.getLocatableCount() > 0) {
      Unit unit = map.getUnitOn(t);
      unit.destroy(true);
      return true;
    } else {
      return false;
    }
  }

  public void fillMap(Map map, int id) {
    // map can't be filled with units
  }

  public boolean isTypeOf(Class c) {
    return c == Unit.class;
  }
}
