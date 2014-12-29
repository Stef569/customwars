package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.action.game.SaveReplayAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.MessageSenderFactory;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.GameOverRenderer;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;

/**
 * Show the statistics as a table
 * In SP and MP mode:
 * The continue button takes the user back to the MAIN_MENU and the session is cleared.
 * <p/>
 * In MP Snail mode:
 * Send a game over message to the server
 */
public class GameOverState extends CWState {
  private static final Logger logger = Logger.getLogger(GameOverState.class);
  private MessageSender messageSender;
  private GameOverRenderer gameOverRenderer;
  private Point gameOverPosition;
  private String gameOverMsg;
  private Font gameOverFont;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    gameOverRenderer = new GameOverRenderer();
    gameOverRenderer.load(this);
    gameOverPosition = new Point();
    gameOverMsg = "";
    gameOverFont = resources.getFont("menu");
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
      GUI.showConfirmationDialog("Save replay", "save", new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            new SaveReplayAction(stateSession.replay).invoke(null);
          }
        }
      });
    }

    Player winner = null;
    for (Player player : game.getActivePlayers()) {
      if (!player.isDestroyed()) {
        winner = player;
      }
    }

    gameOverMsg = winner.getName() + " wins!";
    int width = gameOverFont.getWidth(gameOverMsg);
    int height = gameOverFont.getHeight(gameOverMsg);

    gameOverPosition = GUI.getCenteredRenderPoint(width, height, container);
  }

  private void sendEndGameToServer() {
    try {
      messageSender.endGame();
    } catch (NetworkException ex) {
      logger.warn("Could not send end game", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendEndGameToServer();
          }
        }
      });
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    gameOverRenderer.leave();
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    gameOverFont.drawString(gameOverPosition.x, 5, gameOverMsg);
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
