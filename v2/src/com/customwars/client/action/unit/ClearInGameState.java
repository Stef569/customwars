package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Reset everything in the in game state to default.
 * Clear any information stored about
 *
 * @author stefan
 */
public class ClearInGameState extends AbstractCWAction {
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private Game game;
  private HUD hud;

  public ClearInGameState(Game game, MapRenderer mapRenderer, InGameSession inGameSession, HUD hud) {
    super("Clear in game state", false);
    this.game = game;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
    this.hud = hud;
  }

  protected void doActionImpl() {
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
    inGameSession.discartAllEdits();
    inGameSession.clearClicks();
    inGameSession.setMoving(false);

    mapRenderer.activateCursor("Select");
    mapRenderer.stopCursorTraversal();
    mapRenderer.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setActiveUnit(null);

    hud.hidePopup();

    game.setActiveUnit(null);
  }
}
