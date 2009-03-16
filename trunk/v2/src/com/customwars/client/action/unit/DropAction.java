package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameSession;

/**
 * Drop next unit from the inGameSession within the active unit(the transport)
 * to the next inGameSession drop location
 *
 * @author stefan
 */
public class DropAction extends AbstractCWAction {
  private InGameSession inGameSession;
  private Game game;

  public DropAction(Game game, InGameSession inGameSession) {
    super("Drop", false);
    this.game = game;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    if (inGameSession.isTrapped()) return;

    Unit transporter = (Unit) inGameSession.getClick(2).getLastLocatable();
    Tile dropLocation = inGameSession.getNextDropLocation();
    Unit unitInTransport = inGameSession.getNextUnitToBeDropped();

    transporter.remove(unitInTransport);
    dropLocation.add(unitInTransport);
    game.setActiveUnit(unitInTransport);
  }
}
