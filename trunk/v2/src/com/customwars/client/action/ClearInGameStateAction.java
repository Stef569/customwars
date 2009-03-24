package com.customwars.client.action;

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

  public ClearInGameStateAction() {
    super("Clear in game state", false);
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.game = context.getGame();
    this.mapRenderer = context.getMapRenderer();
    this.hud = context.getHud();
  }

  protected void invokeAction() {
    context.setMode(InGameContext.MODE.DEFAULT);
    context.discartAllEdits();
    context.clearClicks();
    context.setMoving(false);
    context.setTrapped(false);
    context.clearDropLocations();

    mapRenderer.activateCursor("Select");
    mapRenderer.stopCursorTraversal();
    mapRenderer.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setActiveUnit(null);

    hud.hidePopup();

    // Avoid unnecesarry events to be fired
    if (game.getActiveUnit() != null)
      game.setActiveUnit(null);
  }
}
