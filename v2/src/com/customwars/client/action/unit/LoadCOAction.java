package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.ui.state.InGameContext;

import java.util.Collection;

/**
 * Load a CO into a Unit
 */
public class LoadCOAction extends DirectAction {
  private final Unit unit;
  private Map map;

  public LoadCOAction(Unit unit) {
    super("Load CO", false);
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    map = inGameContext.getObj(Game.class).getMap();
  }

  @Override
  protected void invokeAction() {
    unit.loadCO();
    unit.setExperience(unit.getStats().getMaxExperience());
    unit.getOwner().addToBudget(-unit.getPrice() / 2);
    updateCOZone();
  }

  private void updateCOZone() {
    Player unitOwner = unit.getOwner();
    int zoneRange = unitOwner.getCO().getZoneRange();
    Collection<Location> coZone = map.buildCOZone(unit, zoneRange);
    unitOwner.setCoZone(coZone);
  }
}
