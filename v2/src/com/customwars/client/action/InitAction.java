package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.GameContainer;

/**
 * This action is invoked first before the main action
 * Input is blocked when an action is executed
 *
 * @author stefan
 */
public class InitAction extends DirectAction {
  private GameContainer gameContainer;

  public InitAction() {
    super("Init Action");
  }

  protected void init(InGameContext context) {
    gameContainer = context.getContainer();
  }

  protected void invokeAction() {
    gameContainer.getInput().pause();
  }

  @Override
  public void undo() {
    gameContainer.getInput().resume();
  }
}
