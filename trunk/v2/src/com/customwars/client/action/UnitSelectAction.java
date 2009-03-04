package com.customwars.client.action;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;

/**
 * Select a unit and make it the active unit in the game. This is the first action
 * for a unit.
 *
 * @author stefan
 */
public class UnitSelectAction extends CWAction {
  private MapRenderer mapRenderer;
  private InGameSession inGameSession;
  private Game game;

  protected UnitSelectAction(Game game, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Select");
    this.game = game;
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
  }

  public void doActionImpl() {
    Tile clicked = (Tile) mapRenderer.getCursorLocation();
    inGameSession.discartAllEdits();
    inGameSession.setClick(1, clicked);
    selectUnit((Unit) clicked.getLastLocatable());
    inGameSession.setMode(InGameSession.MODE.UNIT_SELECT);
  }

  private void selectUnit(Unit selectedUnit) {
    game.setActiveUnit(selectedUnit);
    mapRenderer.setActiveUnit(selectedUnit);
    mapRenderer.removeZones();
    mapRenderer.showMoveZone();
    mapRenderer.showArrows(true);
  }

  void undoAction() {
    deselectActiveUnit();
    inGameSession.setClick(1, null);
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
  }

  private void deselectActiveUnit() {
    game.setActiveUnit(null);
    mapRenderer.setActiveUnit(null);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);
  }
}
