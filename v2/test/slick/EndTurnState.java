package slick;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Show information about the next turn
 * end the current turn when the delay has passed
 */
public class EndTurnState extends CWState {
  private static final int END_TURN_DELAY = 250;
  private int timeTaken;
  private Game game;
  private Player nextPlayer;
  private int nextDay;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    game = stateSession.game;
    nextPlayer = game.getNextActivePlayer(game.getActivePlayer());
    int turnCount = game.getTurn() + 1;
    nextDay = game.getDay(turnCount);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (entered) {
      g.drawString("Day " + nextDay + " " + nextPlayer.getName() + " Make your moves", 150, 150);
    }
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
