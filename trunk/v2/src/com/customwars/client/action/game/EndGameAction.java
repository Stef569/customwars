package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;

/**
 * End the current game
 *
 * In SP mode:
 * goto the GAME_OVER state
 *
 * In MP mode:
 * Destroy the active player, End the turn
 * goto the GAME_OVER state
 */
public class EndGameAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(EndGameAction.class);
  private StateChanger stateChanger;
  private Game game;
  private MessageSender messageSender;

  public EndGameAction() {
    super("End game", false);
  }

  @Override
  protected void init(InGameContext inGameContext) {
    stateChanger = inGameContext.getObj(StateChanger.class);
    game = inGameContext.getObj(StateSession.class).game;
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    stateChanger.changeTo("GAME_OVER");
    destroyPlayer();
    endTurn();
    if (App.isMultiplayer()) sendYield();
  }

  private void destroyPlayer() {
    Player activePlayer = game.getActivePlayer();
    Player neutralPlayer = game.getMap().getNeutralPlayer();
    activePlayer.destroy(neutralPlayer);
  }

  private void endTurn() {
    if (!game.isGameOver()) {
      game.endTurn();
    }
  }

  private void sendYield() {
    try {
      messageSender.destroyPlayer(game.getActivePlayer());
      messageSender.endTurn(game);
    } catch (NetworkException ex) {
      logger.warn("Could not send yield", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendYield();
      }
    }
  }
}
