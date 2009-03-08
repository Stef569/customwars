package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Select a unit and make it the active unit in the game. This is the first action
 * for a unit.
 *
 * @author stefan
 */
public class SelectAction extends CWAction {
  private MapRenderer mapRenderer;
  private InGameSession inGameSession;
  private Game game;

  public SelectAction(Game game, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Select");
    this.game = game;
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
  }

  public void doActionImpl() {
    Tile clicked = inGameSession.getClick(1);
    selectUnit((Unit) clicked.getLastLocatable());
    inGameSession.setMode(InGameSession.MODE.UNIT_SELECT);

    Tile secondClick = inGameSession.getClick(2);
    if (secondClick != null) {
      mapRenderer.moveCursor(secondClick);
    }
  }

  private void selectUnit(Unit selectedUnit) {
    game.setActiveUnit(selectedUnit);
    mapRenderer.setActiveUnit(selectedUnit);
    mapRenderer.removeZones();
    mapRenderer.showMoveZone();
    mapRenderer.showArrows(true);
  }

  public void undoAction() {
    deselectActiveUnit();
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
  }

  private void deselectActiveUnit() {
    game.setActiveUnit(null);
    mapRenderer.setActiveUnit(null);
    mapRenderer.removeZones();
    mapRenderer.showArrows(false);

    Tile firstClick = inGameSession.getClick(1);
    if (firstClick != null) {
      mapRenderer.moveCursor(firstClick);
    }
  }
}
