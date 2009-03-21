package com.customwars.client.action.game;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.state.InGameContext;

/**
 * End the current turn
 *
 * @author stefan
 */
public class EndTurnAction extends DirectAction {
  private Game game;

  public EndTurnAction() {
    super("End Turn", false);
  }

  protected void init(InGameContext context) {
    game = context.getGame();
  }

  protected void invokeAction() {
    game.endTurn();
  }
}
