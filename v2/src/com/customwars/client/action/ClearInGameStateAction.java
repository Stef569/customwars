package com.customwars.client.action;

import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * Reset everything in the in game state to default.
 *
 * @author stefan
 */
public class ClearInGameStateAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private Game game;
  private HUD hud;
  private CursorController cursorControl;

  public ClearInGameStateAction() {
    super("Clear in game state", false);
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.game = context.getGame();
    this.mapRenderer = context.getMapRenderer();
    this.hud = context.getHud();
    this.cursorControl = context.getCursorController();
  }

  protected void invokeAction() {
    context.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    context.clearUndoHistory();
    context.clearClickHistory();
    context.setMoving(false);
    context.setTrapped(false);
    context.clearDropLocations();
    context.getContainer().getInput().resume();

    cursorControl.activateCursor("Select");
    cursorControl.stopCursorTraversal();
    cursorControl.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setActiveUnit(null);

    hud.hidePopup();

    // Avoid unnecesarry events to be fired
    if (game.getActiveUnit() != null)
      game.setActiveUnit(null);
  }
}
