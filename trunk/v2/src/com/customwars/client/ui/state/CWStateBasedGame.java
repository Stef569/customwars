package com.customwars.client.ui.state;

import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;
import test.testData.HardCodedGame;

/**
 * @author stefan
 */
public class CWStateBasedGame extends StateBasedGame implements InputProviderListener {
  private static final Logger logger = Logger.getLogger(CWStateBasedGame.class);
  private static ResourceManager resources;
  private GameContainer gameContainer;
  private CWInput cwInput;

  public CWStateBasedGame() {
    super(System.getProperty("game.name"));
  }

  public void initStatesList(GameContainer container) throws SlickException {
    this.gameContainer = container;
    StateSession stateSession = new StateSession();
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
    CWState startupState = new StartupState();
    CWState inGame = new InGameState();
    addState(startupState);
    addState(inGame);
  }

  private void mapStateIdsToName() {
    StateLogic statelogic = new StateLogic(this);
    statelogic.addState("IN_GAME", 10);
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

  public static void main(String[] argv) {
    try {
      LoadingList.setDeferredLoading(true);
      resources = new ResourceManager();

      Config config = new Config(resources);
      config.configure();

      AppGameContainer appGameContainer = new AppGameContainer(new CWStateBasedGame());
      appGameContainer.setDisplayMode(250, 250, false);
      appGameContainer.setTargetFrameRate(60);
      appGameContainer.start();
    } catch (SlickException e) {
      logger.fatal("", e);
    } catch (Exception e) {
      logger.fatal(e);
    }
  }
}
