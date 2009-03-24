package com.customwars.client.action.game;

import com.customwars.client.action.DirectAction;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateLogic;

/**
 * End the current turn
 *
 * @author stefan
 */
public class EndTurnAction extends DirectAction {
  private StateLogic stateLogic;

  public EndTurnAction(StateLogic statelogic) {
    super("End Turn", false);
    this.stateLogic = statelogic;
  }

  protected void init(InGameContext context) {
  }

  protected void invokeAction() {
    stateLogic.changeTo("END_TURN");
  }
}
