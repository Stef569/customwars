package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * @author stefan
 */
public class ShowAttackZoneAction extends CWAction {
  private MapRenderer mapRenderer;
  private InGameSession inGameSession;
  private Game game;

  public ShowAttackZoneAction(Game game, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Show Attack zone");
    this.game = game;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    inGameSession.discartAllEdits();
    Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();

    // temporarely make the selectedUnit the active unit in map renderer
    // if the unit stays active in the game, then selecting a unit is messed up.
    // Showing the attackzone does not select the unit, only shows a visible zone
    mapRenderer.setActiveUnit((Unit) cursorLocation.getLastLocatable());
    mapRenderer.removeZones();
    mapRenderer.showAttackZone();
    mapRenderer.setActiveUnit(null);
    game.setActiveUnit(null);
  }


  public void undoAction() {
    mapRenderer.removeAttackZone();
  }
}
