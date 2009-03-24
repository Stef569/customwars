package slick;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Show information about the next turn
 */
public class EndTurnState extends CWState {
  private static final int END_TURN_DELAY = 250;
  private int time;
  private Game game;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    game = stateSession.getGame();
  }

  public void render(GameContainer container, org.newdawn.slick.Graphics g) throws SlickException {
    Color originalColor = g.getColor();
    g.setColor(Color.white);

    Player nextActivePlayer = game.getNextActivePlayer(game.getActivePlayer());
    int nextDay = game.getDay(game.getTurn() + 1);
    g.drawString("Day " + nextDay + " " + nextActivePlayer.getName() + " Make your moves", 150, 150);
    g.setColor(originalColor);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    time += delta;
    if (time >= END_TURN_DELAY) {
      changeGameState("IN_GAME");
      game.endTurn();
      time = 0;
    }
  }

  public int getID() {
    return 4;
  }
}
