package test.slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Shows a menu on the screen
 * when the menu is shown a sound is played
 * on mouse click another sound is played
 */
public class TestMenuMusic extends BasicGameState {

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.drawString("Now in Test Menu and Music state", 80, 80);
  }

  public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {

  }

  public int getID() {
    return 0;
  }
}
