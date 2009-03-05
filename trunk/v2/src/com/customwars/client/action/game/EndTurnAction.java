package com.customwars.client.action.game;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * End the current turn, and clean up (selected unit, visible popup, undo actions,...)
 *
 * @author stefan
 */
public class EndTurnAction extends CWAction {
  private Game game;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private HUD hud;

  public EndTurnAction(Game game, InGameSession inGameSession, MapRenderer mapRenderer, HUD hud) {
    super("End Turn", false);
    this.hud = hud;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
    this.game = game;
  }

  public void doActionImpl() {
    game.endTurn();

    // Back to Defaults
    game.setActiveUnit(null);

    inGameSession.discartAllEdits();
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
    inGameSession.clearClicks();
    inGameSession.setMoving(false);

    mapRenderer.activateCursor("Select");
    mapRenderer.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    hud.hidePopup();
  }
}
