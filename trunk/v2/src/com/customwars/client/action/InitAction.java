package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;
import org.newdawn.slick.gui.GUIContext;

/**
 * This action is invoked first before the main action
 * Input is blocked when an action is executed
 *
 * @author stefan
 */
public class InitAction extends DirectAction {
  private GUIContext guiContext;

  public InitAction() {
    super("Init Action");
  }

  protected void init(InGameContext inGameContext) {
    guiContext = inGameContext.getObj(GUIContext.class);
  }

  protected void invokeAction() {
    guiContext.getInput().pause();
  }

  @Override
  public void undo() {
    guiContext.getInput().resume();
  }
}
