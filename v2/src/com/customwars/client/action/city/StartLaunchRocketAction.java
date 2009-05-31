package com.customwars.client.action.city;

import com.customwars.client.action.DirectAction;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Shows a Silo cursor
 *
 * @author stefan
 */
public class StartLaunchRocketAction extends DirectAction {
  MapRenderer mapRenderer;
  private InGameContext context;

  public StartLaunchRocketAction() {
    super("Start launch rocket", true);
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    mapRenderer = context.getMapRenderer();
  }

  @Override
  protected void invokeAction() {
    context.setInputMode(InGameContext.INPUT_MODE.LAUNCH_ROCKET);
    mapRenderer.activateCursor("SILO");
  }

  @Override
  public void undo() {
    context.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    mapRenderer.activateCursor("SELECT");
  }
}
