package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

/**
 * Load a CO into a Unit
 */
public class LoadCOAction extends DirectAction {
  private final Unit unit;

  public LoadCOAction(Unit unit) {
    super("Load CO", false);
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext inGameContext) {
  }

  @Override
  protected void invokeAction() {
    unit.loadCO();
    unit.setExperience(unit.getStats().getMaxExperience());
    unit.getOwner().addToBudget(-unit.getPrice() / 2);
  }
}
