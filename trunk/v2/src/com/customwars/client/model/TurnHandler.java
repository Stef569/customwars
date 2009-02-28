package com.customwars.client.model;

import com.customwars.client.model.game.NotYourTurnException;
import com.customwars.client.model.game.Player;

/**
 * The interface for objects that perform an action when the turn starts and/or ends
 *
 * @author Stefan
 */
public interface TurnHandler {
  /**
   * Invoked when a turn starts
   *
   * @param currentPlayer the Player that is active in this turn
   */
  void startTurn(Player currentPlayer);

  /**
   * Invoked when a turn ends
   *
   * @param currentPlayer The player that is ending his turn
   * @throws NotYourTurnException When the invoker is not allowed to end this turn
   */
  void endTurn(Player currentPlayer) throws NotYourTurnException;
}