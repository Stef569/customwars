package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Reset everything in the in game state to default.
 *
 * @author stefan
 */
public class ClearInGameState extends CWAction {
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private Game game;

  public ClearInGameState(Game game, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Clear in game state", false);
    this.game = game;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
    inGameSession.clearClicks();
    inGameSession.setMoving(false);

    mapRenderer.activateCursor("Select");
    mapRenderer.stopCursorTraversal();
    mapRenderer.setCursorLocked(false);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
    mapRenderer.setActiveUnit(null);

    game.setActiveUnit(null);
  }
}
