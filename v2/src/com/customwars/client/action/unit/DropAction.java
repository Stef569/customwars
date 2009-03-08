package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameSession;

/**
 * Drop last unit within the active unit(the transport)
 * to the 3th tile
 *
 * @author stefan
 */
public class DropAction extends CWAction {
  private InGameSession inGameSession;
  private Game game;

  public DropAction(Game game, InGameSession inGameSession) {
    super("Drop", false);
    this.game = game;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    if (inGameSession.isTrapped()) return;

    Tile clicked = inGameSession.getClick(3);
    Unit transporter = game.getActiveUnit();
    Unit unitInTransport = (Unit) transporter.getLastLocatable();

    if (clicked.getLocatableCount() == 0) {
      transporter.remove(unitInTransport);
      clicked.add(unitInTransport);
      game.setActiveUnit(unitInTransport);
    }
  }
}
