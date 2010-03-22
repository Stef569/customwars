package com.customwars.client.ui.state;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A state that CW can be in, examples MainMenu, EndTurn, InGame
 * each state can listen for input commands(Select, Cancel, menuUp,menuDown etc)
 * by overwriting controlPressed(Command command)
 *
 * @author stefan
 */
public abstract class CWState extends BasicGameState {
  protected static StateChanger stateChanger;   // Allows to change to another state
  protected static ResourceManager resources;   // All the resources
  protected static CWInput cwInput;             // Handles input
  protected static StateSession stateSession;   // Stores Data between states
  protected boolean entered;

  public final void controlPressed(CWCommand command) {
    if (entered) {
      controlPressed(command, cwInput);
    }
  }

  public void controlPressed(CWCommand command, CWInput cwInput) {
  }

  public final void controlReleased(CWCommand command) {
    if (entered) {
      controlReleased(command, cwInput);
    }
  }

  public void controlReleased(CWCommand command, CWInput cwInput) {
  }

  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    entered = true;
  }

  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    entered = false;
  }

  public final void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
    g.setColor(Color.white);
    render(container, g);
  }

  public abstract void render(GameContainer container, Graphics g) throws SlickException;

  public final void update(GameContainer container, StateBasedGame stateBasedGame, int delta) throws SlickException {
    update(container, delta);
  }

  public abstract void update(GameContainer container, int delta) throws SlickException;

  /**
   * Change to another state
   *
   * @param stateName the name of the state to change to case insensitive
   */
  public void changeToState(String stateName) {
    stateChanger.changeTo(stateName);
  }

  protected void changeToPreviousState() {
    stateChanger.changeToPrevious();
  }

  public void setStateChanger(StateChanger stateChanger) {
    CWState.stateChanger = stateChanger;
  }

  public void setResources(ResourceManager resources) {
    CWState.resources = resources;
  }

  public void setCwInput(CWInput cwInput) {
    CWState.cwInput = cwInput;
  }

  public void setStateSession(StateSession stateSession) {
    CWState.stateSession = stateSession;
  }
}
