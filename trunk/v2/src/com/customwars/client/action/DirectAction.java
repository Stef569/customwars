package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;

/**
 * A direct Action is completed after the first invocation.
 *
 * @author stefan
 */
public abstract class DirectAction extends AbstractCWAction {
  public DirectAction(String name) {
    super(name);
  }

  public DirectAction(String name, boolean canUndo) {
    super(name, canUndo);
  }

  public void invoke(InGameContext context) {
    init(context);
    invokeAction();
    setActionCompleted(true);
  }

  protected abstract void init(InGameContext context);

  protected abstract void invokeAction();

  public void update(int elapsedTime) {
    // NOOP
  }
}
