package com.customwars.client.action.game;

import com.customwars.client.action.DirectAction;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;

/**
 * End the current game
 */
public class EndGameAction extends DirectAction {
  private StateChanger stateChanger;

  public EndGameAction() {
    super("End game");
  }

  @Override
  protected void init(InGameContext context) {
    stateChanger = context.getStateChanger();
  }

  @Override
  protected void invokeAction() {
    stateChanger.changeTo("GAME_OVER");
  }
}
