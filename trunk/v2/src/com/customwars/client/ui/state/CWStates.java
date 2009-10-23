package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.input.CWCommand;
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
  private StateChanger stateChanger;
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
    CWInput cwInput = new CWInput(container.getInput());
    cwInput.addListener(this);

    // Global data for each state
    CWState.setCwInput(cwInput);
    CWState.setResources(resources);
    CWState.setStateSession(stateSession);

    // Create and map states to a string
    buildStateList();
    mapStateIdsToName();

    config.loadInputBindings(cwInput);
    stateChanger.changeTo(startStateName);
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
    stateChanger = new StateChanger(this);
    stateChanger.addState("STARTUP", 0);
    stateChanger.addState("MAIN_MENU", 2);
    stateChanger.addState("KEY_MENU", 5);
    stateChanger.addState("SINGLE", 6);
    stateChanger.addState("OPTION", 7);
    stateChanger.addState("IN_GAME", 3);
    stateChanger.addState("GAME_OVER", 10);
    stateChanger.addState("END_TURN", 4);
    stateChanger.addState("MAP_EDITOR", 50);
    CWState.setStateChanger(stateChanger);
  }

  /**
   * Delegate the pressed control to the current state
   */
  public void controlPressed(Command command) {
    CWState state = (CWState) getCurrentState();
    state.controlPressed((CWCommand) command);
  }

  /**
   * Delegate the released control to the current state
   */
  public void controlReleased(Command command) {
    handleGlobalInput((CWCommand) command);
    CWState state = (CWState) getCurrentState();
    state.controlReleased((CWCommand) command);
  }

  protected void handleGlobalInput(CWCommand command) {
    switch (command.getEnum()) {
      case EXIT:
        logger.info("Exit pressed");
        gameContainer.exit();
        break;
      case TOGGLE_MUSIC:
        SFX.toggleMusic();
        break;
      case TOGGLE_CONSOLE:
        GUI.toggleConsoleFrame();
        break;
      case TOGGLE_EVENTVIEWER:
        GUI.toggleEventFrame();
        break;
      case TOGGLE_FPS:
        gameContainer.setShowFPS(!gameContainer.isShowingFPS());
        break;
    }
  }
}
