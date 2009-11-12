package com.customwars.client.action.city;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.ui.state.InGameContext;

/**
 * Shows a Silo cursor
 *
 * @author stefan
 */
public class StartLaunchRocketAction extends DirectAction {
  private InGameContext context;
  private CursorController cursorControl;

  public StartLaunchRocketAction() {
    super("Start launch rocket", true);
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    cursorControl = context.getCursorController();
  }

  @Override
  protected void invokeAction() {
    context.setInputMode(InGameContext.INPUT_MODE.LAUNCH_ROCKET);
    cursorControl.activateCursor("SILO");
  }

  @Override
  public void undo() {
    context.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    cursorControl.activateCursor("SELECT");
  }
}
