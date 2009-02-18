package com.customwars.client.ui;

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

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
  }

  public void controlReleased(Command command) {
  }
}
