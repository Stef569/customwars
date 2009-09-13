package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

public class DiveAction extends DirectAction {
  private InGameContext context;
  private final Unit unit;

  public DiveAction(Unit unit) {
    super("Dive", false);
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      dive();
    }
  }

  private void dive() {
    unit.dive();
    SFX.playSound("dive");
  }
}
