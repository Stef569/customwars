package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Load the active unit in the transport on the selected tile
 *
 * The active unit and the transport are both on the same tile
 * Transport is the first unit
 * active unit the second unit
 *
 * @author stefan
 */
public class LoadAction extends CWAction {
  private Game game;
  private InGameSession inGameSession;
  private CWAction waitAction;
  private MapRenderer mapRenderer;

  public LoadAction(Game game, InGameSession inGameSession, CWAction waitAction, MapRenderer mapRenderer) {
    super("Load", false);
    this.mapRenderer = mapRenderer;
    this.game = game;
    this.inGameSession = inGameSession;
    this.waitAction = waitAction;
  }

  protected void doActionImpl() {
    Tile selected = inGameSession.getClick(2);
    Unit transport = (Unit) selected.getLocatable(0);
    Unit activeUnit = game.getActiveUnit();

    waitAction.doAction();
    waitAction.setActionCompleted(false);
    selected.remove(activeUnit);
    transport.add(activeUnit);
    mapRenderer.removeUnit(activeUnit);
  }
}
