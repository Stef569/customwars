package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.NetworkManagerSingleton;
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
 * IN MP SNAIL GAME mode:
 * update the server, remove any destroyed players and goto the MAIN_MENU state
 *
 * @author stefan
 */
public class EndTurnAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(EndTurnAction.class);
  private StateChanger stateChanger;
  private NetworkManager networkManager;
  private StateSession session;

  public EndTurnAction() {
    super("End Turn", false);
  }

  protected void init(InGameContext context) {
    this.stateChanger = context.getStateChanger();
    this.networkManager = NetworkManagerSingleton.getInstance();
    this.session = context.getSession();
  }

  protected void invokeAction() {
    switch (App.getGameMode()) {
      case SINGLE_PLAYER:
        stateChanger.changeTo("END_TURN");
        break;
      case NETWORK_SNAIL_GAME:
        endTurnInSnailGameMode();
        break;
      case REPLAY:
        session.game.endTurn();
        break;
    }
  }

  private void endTurnInSnailGameMode() {
    final String serverGameName = session.serverGameName;
    final String userName = session.user.getName();
    final String userPassword = session.user.getPassword();
    final Game game = session.game;

    stateChanger.resumeRecordingStateHistory();
    stateChanger.changeTo("MAIN_MENU");

    removeDestroyedPlayers(game, serverGameName, userName, userPassword);
    endTurn(game, serverGameName, userName, userPassword);
  }

  private void removeDestroyedPlayers(Game game, String serverGameName, String userName, String userPassword) {
    for (Player player : game.getAllPlayers()) {
      if (player.isDestroyed()) {
        try {
          networkManager.destroyPlayer(game, player, serverGameName, userName, userPassword);
        } catch (NetworkException ex) {
          logger.warn("Could not destroy player", ex);
          GUI.showExceptionDialog("Could not destroy player", ex);
        }
      }
    }
  }

  private void endTurn(Game game, String serverGameName, String userName, String userPassword) {
    try {
      game.endTurn();
      networkManager.endTurn(game, serverGameName, userName, userPassword);
    } catch (NetworkException ex) {
      logger.warn("Could not end turn for " + userName + " in game " + serverGameName, ex);
      GUI.showExceptionDialog("Could not end your turn", ex);
    }
  }
}
