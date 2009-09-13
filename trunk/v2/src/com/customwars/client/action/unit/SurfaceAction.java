package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

public class SurfaceAction extends DirectAction {
  private InGameContext context;
  private final Unit unit;

  public SurfaceAction(Unit unit) {
    super("Surface", false);
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      surface();
    }
  }

  private void surface() {
    unit.surface();
    SFX.playSound("surface");
  }
}
