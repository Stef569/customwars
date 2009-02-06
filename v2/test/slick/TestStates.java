package test.slick;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.FadeInTransition;

/**
 * Tests a state based game, by iterating over all the tests
 * shows different transitions between states
 */
public class TestStates extends StateBasedGame {
  private static final int MAX_TEST_STATES = 1;
  private static AppGameContainer container;

  public TestStates() {
    super("Tests");
  }

  public void initStatesList(GameContainer gameContainer) throws SlickException {
    addState(new TestMenuMusic());
  }

  public void keyReleased(int key, char c) {
    if (key == Input.KEY_SPACE) {
      gotoNextState(new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }
  }

  public void mouseClicked(int button, int x, int y, int clickCount) {
    gotoNextState(new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
  }

  public void gotoNextState(Transition leave, Transition enter) {
    enterState(getNextStateID(), enter, leave);
    container.setTitle(getCurrentState().getClass().toString());
  }

  private int getNextStateID() {
    int currentID = getCurrentStateID();
    return currentID++ > MAX_TEST_STATES - 1 ? currentID : 0;
  }

  /**
   * Entry point to our state test
   */
  public static void main(String[] argv) {
    try {
      container = new AppGameContainer(new TestStates());
      container.setDisplayMode(800, 600, false);
      container.setTargetFrameRate(60);
      container.start();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }
}
