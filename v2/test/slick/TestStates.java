package slick;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.GameOverState;
import com.customwars.client.ui.state.MapMakerState;
import com.customwars.client.ui.state.StateLogic;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;

public class TestStates extends StateBasedGame implements InputProviderListener {
  private static final Logger logger = Logger.getLogger(TestStates.class);
  private ResourceManager resources;
  private Config config;
  private StateSession stateSession;
  private GameContainer gameContainer;
  private CWInput cwInput;
  private StateLogic statelogic;
  private String startStateName;

  public TestStates(String startStateName, StateSession stateSession, ResourceManager resources, Config config) {
    super(App.get("game.name") + " - " + App.get("plugin.name"));
    this.startStateName = startStateName;
    this.stateSession = stateSession;
    this.resources = resources;
    this.config = config;
  }

  public void initStatesList(GameContainer container) throws SlickException {
    this.gameContainer = container;

    // Create input commands
    // listen when they are pressed
    cwInput = new CWInput(container.getInput());
    cwInput.addListener(this);

    // Global data for each state
    CWState.setCwInput(cwInput);
    CWState.setResources(resources);
    CWState.setStateSession(stateSession);

    // Create and map states
    buildStateList();
    mapStateIdsToName();
    loadResources();
    CWState.setDefaultFont(resources.getFont("DEFAULT"));
    statelogic.changeTo(startStateName);
    config.configureAfterStartup(cwInput);
  }

  private void buildStateList() {
    CWState testMenu = new TestMenu();
    CWState testMapRenderer = new TestMapRenderer();
    CWState remapKeysTest = new RemapKeysTest();
    CWState inGameTest = new TestInGameState();
    CWState endTurnState = new EndTurnState();
    CWState mapParser = new TestMapParser();
    CWState gameOver = new GameOverState();
    CWState mapMaker = new MapMakerState();

    addState(testMenu);
    addState(testMapRenderer);
    addState(remapKeysTest);
    addState(inGameTest);
    addState(endTurnState);
    addState(mapParser);
    addState(gameOver);
    addState(mapMaker);
  }

  private void mapStateIdsToName() {
    statelogic = new StateLogic(this);
    statelogic.addState("mainmenu", 0);
    statelogic.addState("MAIN_MENU", 0);
    statelogic.addState("terrainmenu", 1);
    statelogic.addState("keymenu", 2);
    statelogic.addState("IN_GAME", 3);
    statelogic.addState("END_TURN", 4);
    statelogic.addState("MAP_PARSER", 6);
    statelogic.addState("GAME_OVER", 10);
    statelogic.addState("MAP_EDITOR", 50);
    CWState.setStatelogic(statelogic);
  }

  private void loadResources() {
    logger.info("Loading resources");
    try {
      resources.loadResources();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delegate the pressed control to the current state
   */
  public void controlPressed(Command command) {
    CWState state = (CWState) getCurrentState();
    state.controlPressed(command);
  }

  /**
   * Delegate the released control to the current state
   */
  public void controlReleased(Command command) {
    handleGlobalInput(command);
    CWState state = (CWState) getCurrentState();
    state.controlReleased(command);
  }

  private void handleGlobalInput(Command command) {
    if (cwInput.isExit(command)) {
      logger.info("Exit pressed");
      gameContainer.exit();
    } else if (cwInput.isToggleMusic(command)) {
      gameContainer.setMusicOn(!gameContainer.isMusicOn());
    }
  }

  public void keyPressed(int key, char c) {
    super.keyPressed(key, c);
    if (cwInput.isActive()) {
      if (key == Input.KEY_SPACE) {
        statelogic.changeToNext();
      }
    }

    if (key == Input.KEY_ENTER) {
      statelogic.changeTo("MAIN_MenU");
    }
  }
}