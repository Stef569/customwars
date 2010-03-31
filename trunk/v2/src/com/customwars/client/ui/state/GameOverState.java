package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.action.game.SaveReplayAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.MessageSenderFactory;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.GameOverRenderer;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Show the statistics as a table
 * In SP and MP mode:
 * The continue button takes the user back to the MAIN_MENU and the session is cleared.
 *
 * In MP Snail mode:
 * Send a game over message to the server
 */
public class GameOverState extends CWState {
  private static final Logger logger = Logger.getLogger(GameOverState.class);
  private MessageSender messageSender;
  private GameOverRenderer gameOverRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    gameOverRenderer = new GameOverRenderer();
    gameOverRenderer.load(this);
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    Game game = stateSession.game;
    gameOverRenderer.buildGUI(game);
    messageSender = MessageSenderFactory.getInstance().createMessageSender();
    logger.info("Game Over");

    if (App.isMultiplayer() && game.isGameOver()) {
      sendEndGameToServer();
    }

    if (App.getBoolean("game.recordreplay.prompt")) {
      if (GUI.showConfirmationDialog("Save replay", "save") == GUI.YES_OPTION) {
        new SaveReplayAction(stateSession.replay).invoke(null);
      }
    }
  }

  private void sendEndGameToServer() {
    try {
      messageSender.endGame();
    } catch (NetworkException ex) {
      logger.warn("Could not send end game", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendEndGameToServer();
      }
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    gameOverRenderer.leave();
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    gameOverRenderer.render(g);
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  public void continueToNextState() {
    stateSession.clear();
    stateChanger.resumeRecordingStateHistory();
    changeToState("MAIN_MENU");
  }

  public int getID() {
    return 16;
  }
}
