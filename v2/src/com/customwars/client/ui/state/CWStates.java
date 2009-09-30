package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.menu.MainMenuState;
import com.customwars.client.ui.state.menu.OptionMenuState;
import com.customwars.client.ui.state.menu.SingleMenuState;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Create each State and link it to a name
 *
 * @author stefan
 */
public class CWStates extends StateBasedGame implements InputProviderListener {
  private static final Logger logger = Logger.getLogger(CWStates.class);
  private ResourceManager resources;
  private Config config;
  private StateSession stateSession;
  private GameContainer gameContainer;
  private CWInput cwInput;
  private StateLogic statelogic;
  private String startStateName;

  public CWStates(String startStateName, StateSession stateSession, ResourceManager resources, Config config) {
    super(App.get("game.name") + " - " + App.get("plugin.name"));
    this.startStateName = startStateName;
    this.stateSession = stateSession;
    this.resources = resources;
    this.config = config;
  }

  public void initStatesList(GameContainer container) throws SlickException {
    this.gameContainer = container;

    // listen for command input(pressed, release)
    cwInput = new CWInput(container.getInput());
    cwInput.addListener(this);

    // Global data for each state
    CWState.setCwInput(cwInput);
    CWState.setResources(resources);
    CWState.setStateSession(stateSession);

    // Create and map states to a string
    buildStateList();
    mapStateIdsToName();

    config.loadInputBindings(cwInput);
    statelogic.changeTo(startStateName);
    logger.debug("Startup complete starting state=" + (startStateName == null ? "Default" : startStateName));
  }

  private void buildStateList() {
    CWState startup = new StartupState();

    // Menu
    CWState mainMenu = new MainMenuState();
    CWState optionMenu = new OptionMenuState();
    CWState singleMenu = new SingleMenuState();
    CWState remapKeysTest = new ControlBindingState();

    // Game
    CWState mapEditorState = new MapEditorState();
    CWState inGame = new InGameState();
    CWState endTurnState = new EndTurnState();
    CWState gameOver = new GameOverState();

    addState(startup);
    addState(mainMenu);
    addState(optionMenu);
    addState(singleMenu);
    addState(inGame);
    addState(endTurnState);
    addState(gameOver);
    addState(remapKeysTest);
    addState(mapEditorState);
  }

  private void mapStateIdsToName() {
    statelogic = new StateLogic(this);
    statelogic.addState("STARTUP", 0);
    statelogic.addState("MAIN_MENU", 2);
    statelogic.addState("KEY_MENU", 5);
    statelogic.addState("SINGLE", 6);
    statelogic.addState("OPTION", 7);
    statelogic.addState("IN_GAME", 3);
    statelogic.addState("GAME_OVER", 10);
    statelogic.addState("END_TURN", 4);
    statelogic.addState("MAP_EDITOR", 50);
    CWState.setStatelogic(statelogic);
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
}
