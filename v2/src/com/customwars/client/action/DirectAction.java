package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;

/**
 * A direct Action is executed only once
 * isCompleted() return true after the first invocation.
 */
public abstract class DirectAction extends AbstractCWAction {
  public DirectAction(String name) {
    super(name);
  }

  public DirectAction(String name, boolean canUndo) {
    super(name, canUndo);
  }

  public void invoke(InGameContext context) {
    super.invoke(context);
    init(context);
    invokeAction();
    setActionCompleted(true);
  }

  protected abstract void init(InGameContext inGameContext);

  protected abstract void invokeAction();

  public void update(int elapsedTime) {
    // NOOP
  }
}
