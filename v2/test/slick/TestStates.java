package slick;

import com.customwars.client.ui.CWInput;
import org.apache.log4j.BasicConfigurator;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

/**
 * Tests a state based game by iterating over all the tests
 * shows different transitions between states
 * input is generic and is put into cwInput
 */
public class TestStates extends StateBasedGame implements InputProviderListener {
  private static final int NUM_TEST_STATES = 4;
  private static AppGameContainer appGameContainer;
  private CWInput cwInput;
  private int startStateID;

  public TestStates() {
    super("Tests");
    startStateID = 0;
  }

  public TestStates(int startStateID) {
    this();
    this.startStateID = startStateID;
  }

  public void initStatesList(GameContainer gameContainer) throws SlickException {
    cwInput = new CWInput(appGameContainer.getInput());
    cwInput.addListener(this);

    CWState testMenuMusic = new TestMenuMusic(cwInput);
    CWState testMapRenderer = new TestMapRenderer(cwInput);
    CWState remapKeysTest = new RemapKeysTest(cwInput);
    CWState recolorTest = new RecolorTest();

    addState(testMenuMusic);
    addState(testMapRenderer);
    addState(remapKeysTest);
    addState(recolorTest);
    gotoState(startStateID);
  }

  // Delegate Command Press/releases to the current state
  public void controlPressed(Command command) {
    if (cwInput.isExitPressed(command)) {
      appGameContainer.exit();
    }

    CWState state = (CWState) getCurrentState();
    state.controlPressed(command);
  }

  public void controlReleased(Command command) {
    CWState state = (CWState) getCurrentState();
    state.controlReleased(command);
  }

  public void keyPressed(int key, char c) {
    if (key == Input.KEY_SPACE) {
      gotoState(getNextStateID());
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
    int startStateID = 0;

    if (argv.length > 0) {
      startStateID = Integer.parseInt(argv[0]);
    }
    try {
      BasicConfigurator.configure();
      appGameContainer = new AppGameContainer(new TestStates(startStateID));
      appGameContainer.setDisplayMode(800, 600, false);
      appGameContainer.setTargetFrameRate(60);
      appGameContainer.start();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }
}
