package com.customwars.client.ui.slick;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.loading.ControlsConfigurator;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import com.thoughtworks.xstream.core.util.Fields;
import org.apache.log4j.Logger;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.lang.reflect.Field;

public abstract class CWStateBasedGame extends StateBasedGame implements InputProviderListener {
  private Logger logger = Logger.getLogger(CWStateBasedGame.class);
  protected final ResourceManager resources;
  protected final Config config;
  protected final StateSession stateSession;
  private final StateChanger stateChanger;
  private final String startStateName;

  protected CWInput cwInput;
  protected GameContainer gameContainer;

  public CWStateBasedGame(String title, String startStateName, ResourceManager resources, Config config) {
    super(title);
    this.startStateName = startStateName;
    this.resources = resources;
    this.config = config;
    this.stateSession = new StateSession();
    this.stateChanger = new StateChanger(this);
  }

  public final void initStatesList(GameContainer gameContainer) throws SlickException {
    this.gameContainer = gameContainer;
    loadInput();
    loadDefaultFont();
    initStatesList();
    changeToState(startStateName);

    logger.debug("Startup complete starting state=" + (startStateName == null ? "Default" : startStateName));
  }

  /**
   * Retrieve the Command -> control binds from the user properties
   * bind each command -> control in cwInput
   * An event is send when a control that is binded to a command is pressed.
   */
  private void loadInput() {
    this.cwInput = new CWInput(gameContainer.getInput());
    ControlsConfigurator controlsConfigurator = new ControlsConfigurator(cwInput);
    controlsConfigurator.configure(App.getUserProperties());
    cwInput.addListener(this);
  }

  private void loadDefaultFont() {
    Font defaultFont;

    try {
      defaultFont = resources.loadDefaultFont();
      setDefaultFont(defaultFont);
    } catch (IOException e) {
      logger.warn("Could not load default font");
    }
  }

  /**
   * Overwrite the default slick font with our own.
   */
  private void setDefaultFont(Font defaultFont) {
    if (defaultFont != null) {
      gameContainer.setDefaultFont(defaultFont);

      try {
        Field f = Graphics.class.getDeclaredField("DEFAULT_FONT");
        f.setAccessible(true);
        Fields.write(f, null, defaultFont);
        f.setAccessible(false);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
    }
  }

  public abstract void initStatesList();

  /**
   * Add a CWState to this state based game,
   * changing to another state is simply changeToState("stateName");
   *
   * @param stateName The unique name of the given cwState
   * @param cwState   The state to add to this state based game
   */
  public void addState(String stateName, CWState cwState) {
    super.addState(cwState);
    initCWState(cwState);
    stateChanger.addState(stateName, cwState.getID());
  }

  private void initCWState(CWState gameState) {
    gameState.setCwInput(cwInput);
    gameState.setResources(resources);
    gameState.setStateSession(stateSession);
    gameState.setStateChanger(stateChanger);
  }

  public void changeToState(String stateName) {
    stateChanger.changeTo(stateName);
  }

  /**
   * Delegate the pressed control to the current state
   */
  public void controlPressed(Command command) {
    CWCommand cwCommand = (CWCommand) command;
    CWState state = (CWState) getCurrentState();
    state.controlPressed(cwCommand);
  }

  /**
   * Delegate the released control to the current state
   */
  public void controlReleased(Command command) {
    CWCommand cwCommand = (CWCommand) command;
    handleGlobalInput(cwCommand);
    CWState state = (CWState) getCurrentState();
    state.controlReleased(cwCommand);
  }

  protected abstract void handleGlobalInput(CWCommand command);

  public abstract void shutDownHook();

  public CWInput getInput() {
    return cwInput;
  }
}