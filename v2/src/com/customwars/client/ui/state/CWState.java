package com.customwars.client.ui.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A state that CW can be in, examples MainMenu, EndTurn, InGame
 * each state can listen for input commands(Select, Cancel, menuUp,menuDown etc)
 * by implementing controlPressed(Command command)
 *
 * We extend BasicGameState to add cw specific functionality.
 *
 * @author stefan
 */
public abstract class CWState extends BasicGameState implements InputProviderListener {
  private StateLogic statelogic;    // Allows to change to another state
  protected CWInput cwInput;        // Handles input, subclasses should implement controlPressed

  protected CWState(CWInput cwInput, StateLogic statelogic) {
    this.cwInput = cwInput;
    this.statelogic = statelogic;
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
  }

  public final void controlPressed(Command command) {
    controlPressed(command, cwInput);
  }

  public abstract void controlPressed(Command command, CWInput cwInput);

  public void controlReleased(Command command) {
  }

  public void changeGameState(String newStateName) {
    statelogic.changeTo(newStateName);
  }
}
