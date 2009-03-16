package slick;

import com.customwars.client.action.ActionManager;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
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
  private StateSession stateSession;
  private GameContainer gameContainer;
  private CWInput cwInput;
  private StateLogic statelogic;
  private int startID;
  private ActionManager actionManager;

  public TestStates(int startID, StateSession stateSession, ResourceManager resources) {
    super(System.getProperty("game.name"));
    this.startID = startID;
    this.stateSession = stateSession;
    this.resources = resources;
  }

  public void initStatesList(GameContainer container) throws SlickException {
    this.gameContainer = container;

    cwInput = new CWInput(container.getInput());
    cwInput.addListener(this);

    CWState.setCwInput(cwInput);
    CWState.setResources(resources);
    CWState.setStateSession(stateSession);

    buildStateList();
    mapStateIdsToName();
    statelogic.gotoState(startID);
  }

  private void buildStateList() {
    CWState testMenu = new TestMenu();
    CWState testMapRenderer = new TestMapRenderer();
    CWState remapKeysTest = new RemapKeysTest();
    CWState inGameTest = new TestInGameState();
    CWState endTurnState = new EndTurnState(actionManager);

    addState(testMenu);
    addState(testMapRenderer);
    addState(remapKeysTest);
    addState(inGameTest);
    addState(endTurnState);

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
    statelogic.addState("IN_GAME", 3);
    statelogic.addState("END_TURN", 4);
    CWState.setStatelogic(statelogic);
  }

  protected void preUpdateState(GameContainer container, int delta) throws SlickException {
    ActionManager.update(delta);
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
}
