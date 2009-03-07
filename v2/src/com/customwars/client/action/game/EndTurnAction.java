package com.customwars.client.action.game;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;

/**
 * End the current turn
 *
 * @author stefan
 */
public class EndTurnAction extends CWAction {
  private Game game;

  public EndTurnAction(Game game) {
    super("End Turn", false);
    this.game = game;
  }

  public void doActionImpl() {
    game.endTurn();
  }
}
