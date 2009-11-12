package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

public class StartFlareAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private CursorController cursorController;

  public StartFlareAction() {
    super("Prepare to flare", true);
  }

  @Override
  protected void init(InGameContext context) {
    this.mapRenderer = context.getMapRenderer();
    this.cursorController = context.getCursorController();
    this.context = context;
  }

  @Override
  protected void invokeAction() {
    mapRenderer.showAttackZone();
    cursorController.activateCursor("SILO");
    context.setInputMode(InGameContext.INPUT_MODE.UNIT_FLARE);
  }

  @Override
  public void undo() {
    mapRenderer.removeZones();
    cursorController.activateCursor("SELECT");
    context.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
  }
}
