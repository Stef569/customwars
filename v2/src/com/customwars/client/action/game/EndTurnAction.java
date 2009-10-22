package com.customwars.client.action.game;

import com.customwars.client.action.DirectAction;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;

/**
 * End the current turn
 *
 * @author stefan
 */
public class EndTurnAction extends DirectAction {
  private StateChanger stateChanger;

  public EndTurnAction(StateChanger stateChanger) {
    super("End Turn", false);
    this.stateChanger = stateChanger;
  }

  protected void init(InGameContext context) {
  }

  protected void invokeAction() {
    stateChanger.changeTo("END_TURN");
  }
}
