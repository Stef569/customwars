package com.customwars.client.action.game;

import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * This action allows the user to delete 1 or more units.
 * The cursor will change into a hammer.
 * Selecting a unit will destroy that unit.
 */
public class StartDeleteUnitAction extends DirectAction {
  private CursorController cursorController;
  private InGameContext inGameContext;
  private GameRenderer gameRenderer;

  public StartDeleteUnitAction() {
    super("Start delete unit", false);
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameRenderer = inGameContext.getObj(GameRenderer.class);
    cursorController = inGameContext.getObj(CursorController.class);
  }

  @Override
  protected void invokeAction() {
    inGameContext.clearUndoHistory();
    Location previousCursorLocation = gameRenderer.getCursorLocation();
    cursorController.activateCursor("HAMMER");
    cursorController.moveCursor(previousCursorLocation);
    inGameContext.setInputMode(InGameContext.INPUT_MODE.UNIT_DELETE);
  }
}
