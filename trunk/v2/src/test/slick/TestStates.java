package test.slick;

import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.StateLogic;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Tests a state based game by iterating over all the tests
 * shows different transitions between states
 * input is generic and is put into cwInput
 */
public class TestStates extends StateBasedGame implements InputProviderListener {
  private static final Logger logger = Logger.getLogger(TestStates.class);
  private static AppGameContainer appGameContainer;
  private CWInput cwInput;
  private int startStateID;
  private StateLogic statelogic;

  public TestStates() {
    super("Slick Tests!!");
    startStateID = 0;
  }

  public TestStates(int startStateID) {
    this();
    this.startStateID = startStateID;
  }

  public void initStatesList(GameContainer gameContainer) throws SlickException {
    cwInput = new CWInput(appGameContainer.getInput());
    cwInput.addListener(this);

    statelogic = new StateLogic(this, appGameContainer);
    statelogic.addState("mainmenu", 0);
    statelogic.addState("MAIN_MENU", 0);
    statelogic.addState("terrainmenu", 1);
    statelogic.addState("keymenu", 2);
    statelogic.addState("spritemenu", 3);

    CWState testMenuMusic = new TestMenuMusic(cwInput, statelogic);
    CWState testMapRenderer = new TestMapRenderer(cwInput, statelogic);
    CWState remapKeysTest = new RemapKeysTest(cwInput, statelogic);
    CWState recolorTest = new RecolorTest(cwInput, statelogic);

    addState(testMenuMusic);
    addState(testMapRenderer);
    addState(remapKeysTest);
    addState(recolorTest);
    statelogic.gotoState(startStateID);
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

  public void update(GameContainer gcontainer, Graphics g) {
  }

  public void keyPressed(int key, char c) {
    if (key == Input.KEY_SPACE) {
      statelogic.changeToNext();
    }

    if (key == Input.KEY_ENTER) {
      statelogic.changeTo("MAIN_MenU");
    }
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
      loadAndApplyLoggerProperties();
      logger.info("Starting up");
      appGameContainer = new AppGameContainer(new TestStates(startStateID));
      appGameContainer.setDisplayMode(261, 250, false);
      appGameContainer.setTargetFrameRate(60);
      appGameContainer.start();
    } catch (SlickException e) {
      logger.fatal("", e);
    } catch (IOException e) {
      logger.fatal("", e);
    }
  }

  private static void loadAndApplyLoggerProperties() throws IOException {
    Properties props = new Properties();
    InputStream in = ResourceLoader.getResourceAsStream("res/data/config/log4j.properties");
    props.load(in);
    PropertyConfigurator.configure(props);
  }
}
