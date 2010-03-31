package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.TurnBasedGame;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;

/**
 * End the current turn
 *
 * In SP mode:
 * change to the END_TURN state
 *
 * In MP SNAIL GAME mode:
 * update the server, remove any destroyed players and goto the MAIN_MENU state
 *
 * @author stefan
 */
public class EndTurnAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(EndTurnAction.class);
  private StateChanger stateChanger;
  private StateSession session;
  private MessageSender messageSender;

  public EndTurnAction() {
    super("End Turn", false);
  }

  protected void init(InGameContext inGameContext) {
    this.stateChanger = inGameContext.getStateChanger();
    this.session = inGameContext.getSession();
    this.messageSender = inGameContext.getMessageSender();
  }

  protected void invokeAction() {
    switch (App.getGameMode()) {
      case SINGLE_PLAYER:
        stateChanger.changeTo("END_TURN");
        break;
      case NETWORK_SNAIL_GAME:
        session.game.endTurn();
        sendDestroyedPlayers(session.game);
        sendEndTurn();
        gotoMainMenu();
        break;
      case REPLAY:
        session.game.endTurn();
        break;
    }
  }

  private void sendDestroyedPlayers(TurnBasedGame game) {
    for (Player player : game.getAllPlayers()) {
      if (player.isDestroyed()) {
        try {
          messageSender.destroyPlayer(player);
        } catch (NetworkException ex) {
          logger.warn("Could not send destroy player", ex);
          if (GUI.askToResend(ex) == GUI.YES_OPTION) {
            sendDestroyedPlayers(game);
          }
        }
      }
    }
  }

  private void sendEndTurn() {
    try {
      messageSender.endTurn(session.game);
    } catch (NetworkException ex) {
      logger.warn("Could not send end turn", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendEndTurn();
      }
    }
  }

  private void gotoMainMenu() {
    stateChanger.resumeRecordingStateHistory();
    stateChanger.changeTo("MAIN_MENU");
  }
}
