package slick;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.ControlBindingState;
import com.customwars.client.ui.state.EndTurnState;
import com.customwars.client.ui.state.GameOverState;
import com.customwars.client.ui.state.InGameState;
import com.customwars.client.ui.state.MapEditorState;
import com.customwars.client.ui.state.StateChanger;
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
  private StateChanger stateChanger;
  private String startStateName;

  public TestStates(String startStateName, StateSession stateSession, ResourceManager resources, Config config) {
    super(App.get("game.name") + " - Slick tests");
    this.startStateName = startStateName == null ? "MAIN_MENU" : startStateName;
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
    CWState.setDefaultFont(container);
    stateChanger.changeTo(startStateName);
    config.loadInputBindings(cwInput);

    logger.debug("Startup complete starting state=" + (startStateName == null ? "Default" : startStateName));
  }

  private void buildStateList() {
    CWState testMenu = new TestMenu();
    CWState testMapRenderer = new TestMapRenderer();
    CWState remapKeysTest = new ControlBindingState();
    CWState inGameTest = new InGameState();
    CWState endTurnState = new EndTurnState();
    CWState mapParser = new TestMapParser();
    CWState gameOver = new GameOverState();
    CWState mapEditorState = new MapEditorState();

    addState(testMenu);
    addState(testMapRenderer);
    addState(remapKeysTest);
    addState(inGameTest);
    addState(endTurnState);
    addState(mapParser);
    addState(gameOver);
    addState(mapEditorState);
  }

  private void mapStateIdsToName() {
    stateChanger = new StateChanger(this);
    stateChanger.addState("mainmenu", 0);
    stateChanger.addState("MAIN_MENU", 0);
    stateChanger.addState("terrainmenu", 1);
    stateChanger.addState("keymenu", 5);
    stateChanger.addState("IN_GAME", 3);
    stateChanger.addState("END_TURN", 4);
    stateChanger.addState("MAP_PARSER", 6);
    stateChanger.addState("GAME_OVER", 10);
    stateChanger.addState("MAP_EDITOR", 50);
    CWState.setStateChanger(stateChanger);
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
      SFX.toggleMusic();
    } else if (cwInput.isToggleConsole(command)) {
      GUI.toggleConsoleFrame();
    } else if (cwInput.isToggleEventViewer(command)) {
      GUI.toggleEventFrame();
    } else if (cwInput.isToggleFPS(command)) {
      gameContainer.setShowFPS(!gameContainer.isShowingFPS());
    }
  }

  public void keyPressed(int key, char c) {
    super.keyPressed(key, c);
    if (cwInput.isActive()) {
      if (key == Input.KEY_SPACE) {
        stateChanger.changeToNext();
      }
    }

    if (key == Input.KEY_ENTER) {
      stateChanger.changeTo("MAIN_MenU");
    }
  }
}