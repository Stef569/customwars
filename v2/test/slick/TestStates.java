package test.slick;

import client.ui.CWInput;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

/**
 * Tests a state based game by iterating over all the tests
 * shows different transitions between states
 * input is generic and is put into cwInput
 */
public class TestStates extends StateBasedGame implements InputProviderListener {
  private static final int NUM_TEST_STATES = 2;
  private static AppGameContainer appGameContainer;
  private CWInput cwInput;
  private int startState;

  public TestStates(int startState) {
    super("Tests");
    this.startState = startState;
  }

  public void initStatesList(GameContainer gameContainer) throws SlickException {
    cwInput = new CWInput(appGameContainer.getInput());
    cwInput.addCommandListener(this);

    BasicGameState testMenuMusic = new TestMenuMusic(cwInput);
    BasicGameState testMapRenderer = new TestMapRenderer(cwInput);

    addState(testMenuMusic);
    addState(testMapRenderer);
    gotoState(startState);
  }

  public void controlPressed(Command command) {
    if (cwInput.isSelectPressed(command)) {
      gotoState(getNextStateID());
    }
  }

  public void controlReleased(Command command) {
    if (cwInput.isExitPressed(command)) {
      appGameContainer.exit();
    }
  }

  private void gotoState(int index) {
    enterState(index, null, new FadeInTransition(Color.black));
    appGameContainer.setTitle(getCurrentState().getClass().toString());
  }

  private int getNextStateID() {
    int nextID = getCurrentStateID() + 1;
    return nextID < NUM_TEST_STATES ? nextID : 0;
  }

  /**
   * Entry point to our state test
   *
   * @param argv First argument provided is the startState as a number.
   */
  public static void main(String[] argv) {
    int startState = 0;

    if (argv.length > 0) {
      startState = Integer.parseInt(argv[0]);
    }

    try {
      appGameContainer = new AppGameContainer(new TestStates(startState));
      appGameContainer.setDisplayMode(800, 600, false);
      appGameContainer.setTargetFrameRate(60);
      appGameContainer.start();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }
}
