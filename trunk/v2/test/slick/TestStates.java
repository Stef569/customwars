package slick;

import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.testdata.TestData;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.StateLogic;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;

public class TestStates extends StateBasedGame implements InputProviderListener {
  private static final Logger logger = Logger.getLogger(TestStates.class);
  private static ResourceManager resources;
  private GameContainer gameContainer;
  private CWInput cwInput;
  private StateLogic statelogic;

  public TestStates() {
    super(System.getProperty("game.name"));
  }

  public void initStatesList(GameContainer container) throws SlickException {
    this.gameContainer = container;
    StateSession stateSession = new StateSession();

    TestData.storeTestData();
    stateSession.setMap(HardCodedGame.getMap());  // Later set by a mapSelectState

    cwInput = new CWInput(container.getInput());
    cwInput.addListener(this);

    CWState.setCwInput(cwInput);
    CWState.setResources(resources);
    CWState.setStateSession(stateSession);

    buildStateList();
    mapStateIdsToName();
  }

  private void buildStateList() {
    CWState testMenuMusic = new TestMenuMusic();
    CWState testMapRenderer = new TestMapRenderer();
    CWState remapKeysTest = new RemapKeysTest();

    addState(testMenuMusic);
    addState(testMapRenderer);
    addState(remapKeysTest);

    try {
      resources.loadFromFile();
    } catch (IOException e) {
      logger.fatal(e);
    }
  }

  private void mapStateIdsToName() {
    statelogic = new StateLogic(this);
    statelogic.addState("mainmenu", 0);
    statelogic.addState("MAIN_MENU", 0);
    statelogic.addState("terrainmenu", 1);
    statelogic.addState("keymenu", 2);
    CWState.setStatelogic(statelogic);
  }

  private void handleGlobalInput(Command command) {
    if (cwInput.isExitPressed(command)) {
      gameContainer.exit();
    }
  }

  public void controlPressed(Command command) {
    handleGlobalInput(command);
    CWState state = (CWState) getCurrentState();
    state.controlPressed(command);
  }

  public void controlReleased(Command command) {
    CWState state = (CWState) getCurrentState();
    state.controlReleased(command);
  }

  public void keyPressed(int key, char c) {
    super.keyPressed(key, c);
    if (key == Input.KEY_SPACE) {
      statelogic.changeToNext();
    }

    if (key == Input.KEY_ENTER) {
      statelogic.changeTo("MAIN_MenU");
    }
  }

  public static void main(String[] argv) {
    try {
      LoadingList.setDeferredLoading(false);
      resources = new ResourceManager();

      Config config = new Config(resources);
      config.configure();

      AppGameContainer appGameContainer = new AppGameContainer(new TestStates());
      appGameContainer.setDisplayMode(1024, 800, false);
      appGameContainer.setTargetFrameRate(60);
      appGameContainer.start();
    } catch (SlickException e) {
      logger.fatal("", e);
    } catch (Exception e) {
      logger.fatal("", e);
    }
  }
}
