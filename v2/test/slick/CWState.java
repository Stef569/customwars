package slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A state that CW can be in, examples MainMenu, EndTurn, InGame
 * each state can listen for input commands(Select, Cancel, menuUp,menuDown etc)
 * by implementing controlPressed(Command command)
 *
 * @author stefan
 */
public abstract class CWState extends BasicGameState implements InputProviderListener {

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
  }

  public void controlReleased(Command command) {
  }
}