package slick;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Show information about the next turn
 * and end the current turn when the delay has passed
 */
public class EndTurnState extends CWState {
  private static final int END_TURN_DELAY = 250;
  private int timeTaken;
  private Game game;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    game = stateSession.game;
    g.setColor(Color.white);

    Player nextActivePlayer = game.getNextActivePlayer(game.getActivePlayer());
    int turnCount = game.getTurn() + 1;
    g.drawString("Day " + game.getDay(turnCount) + " " + nextActivePlayer.getName() + " Make your moves", 150, 150);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    timeTaken += delta;
    if (timeTaken >= END_TURN_DELAY) {
      changeGameState("IN_GAME");
      game.endTurn();
      timeTaken = 0;
    }
  }

  public int getID() {
    return 4;
  }
}
