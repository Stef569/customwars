package com.customwars.client.action.city;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Shows a Silo cursor
 */
public class StartLaunchRocketAction extends DirectAction {
  private InGameContext inGameContext;
  private CursorController cursorControl;
  private MapRenderer mapRenderer;

  public StartLaunchRocketAction() {
    super("Start launch rocket", true);
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    cursorControl = inGameContext.getObj(CursorController.class);
    mapRenderer = inGameContext.getObj(MapRenderer.class);
  }

  @Override
  protected void invokeAction() {
    inGameContext.setInputMode(InGameContext.INPUT_MODE.LAUNCH_ROCKET);
    cursorControl.activateCursor("SILO");

    // Don't show arrows when choosing a rocket destination
    mapRenderer.showArrows(false);
  }

  @Override
  public void undo() {
    inGameContext.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    cursorControl.activateCursor("SELECT");
    mapRenderer.showArrows(true);
  }
}
