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
 * End the current game
 *
 * In SP mode:
 * goto the GAME_OVER state
 *
 * In MP Snail game mode:
 * Destroy the active player, End the turn
 * goto the GAME_OVER state
 */
public class EndGameAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(EndGameAction.class);
  private StateChanger stateChanger;
  private NetworkManager networkManager;
  private StateSession session;

  public EndGameAction() {
    super("End game", false);
  }

  @Override
  protected void init(InGameContext context) {
    stateChanger = context.getStateChanger();
    session = context.getSession();
    networkManager = NetworkManagerSingleton.getInstance();
  }

  @Override
  protected void invokeAction() {
    if (App.isSinglePlayerGame()) {
      stateChanger.changeTo("GAME_OVER");
    } else if (App.isMultiplayerSnailGame()) {
      yieldInSnailGameMode();
    }
  }

  private void yieldInSnailGameMode() {
    final String serverGameName = session.serverGameName;
    final String userName = session.user.getName();
    final String userPassword = session.user.getPassword();
    final Game game = session.game;

    destroyPlayer(serverGameName, userName, userPassword, game);
    endTurn(serverGameName, userName, userPassword, game);
    stateChanger.changeTo("GAME_OVER");
  }

  private void destroyPlayer(String serverGameName, String userName, String userPassword, Game game) {
    Player activePlayer = game.getActivePlayer();
    Player neutralPlayer = game.getMap().getNeutralPlayer();
    activePlayer.destroy(neutralPlayer);

    try {
      networkManager.destroyPlayer(game, activePlayer, serverGameName, userName, userPassword);
    } catch (NetworkException ex) {
      logger.warn("Could not yield", ex);
      GUI.showExceptionDialog("Could not yield", ex);
    }
  }

  private void endTurn(String serverGameName, String userName, String userPassword, Game game) {
    if (!game.isGameOver()) {
      game.endTurn();
    }

    try {
      networkManager.endTurn(game, serverGameName, userName, userPassword);
    } catch (NetworkException ex) {
      logger.warn("Could not end turn for " + userName, ex);
      GUI.showExceptionDialog("Could not end your turn", ex);
    }
  }
}