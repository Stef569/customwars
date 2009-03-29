package com.customwars.client.ui.state;

import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.StateBasedGame;
import slick.EndTurnState;

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
  private int startID;

  public CWStates(int startID, StateSession stateSession, ResourceManager resources, Config config) {
    super(System.getProperty("game.name") + " - " + System.getProperty("plugin.name"));
    this.startID = startID;
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

    statelogic.gotoState(startID);
    config.configureAfterStartup(cwInput);
  }

  private void buildStateList() {
    CWState startup = new StartupState();
    CWState inGame = new InGameState();
    CWState endTurnState = new EndTurnState();
    CWState gameOver = new GameOverState();

    addState(startup);
    addState(inGame);
    addState(endTurnState);
    addState(gameOver);
  }

  private void mapStateIdsToName() {
    statelogic = new StateLogic(this);
    statelogic.addState("STARTUP", 0);
    statelogic.addState("KEY_MAP", 2);
    statelogic.addState("IN_GAME", 10);
    statelogic.addState("END_TURN", 4);
    statelogic.addState("GAME_OVER", 10);
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
    if (cwInput.isExitPressed(command)) {
      logger.info("Exit pressed");
      gameContainer.exit();
    } else if (cwInput.isToggleMusicPressed(command)) {
      gameContainer.setMusicOn(!gameContainer.isMusicOn());
    }
  }
}
