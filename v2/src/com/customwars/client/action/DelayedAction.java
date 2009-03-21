package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;

/**
 * An action that is fired after a delay
 * Subclasses should set actionCompleted to true when the action has finished.
 *
 * @author stefan
 */
public abstract class DelayedAction extends AbstractCWAction {
  private int time;
  private int delay;
  private boolean running;

  protected DelayedAction(String name, int delay) {
    super(name);
    this.delay = delay;
  }

  public final void invoke(InGameContext context) {
    if (!running) {
      init(context);
      running = true;
    }
  }

  public final void update(int elapsedTime) {
    if (running) {
      time += elapsedTime;

      if (time >= delay) {
        updateAction();
        time = 0;
      }
    }
  }

  /**
   * Keep invoking the action until it is complete
   */
  public final void updateAction() {
    if (!isCompleted())
      invokeAction();

    if (isCompleted()) {
      running = false;
    }
  }

  /**
   * Init the delayed action, called once before updating this action
   */
  protected abstract void init(InGameContext context);

  /**
   * This method is called everytime the delay has passed
   */
  protected abstract void invokeAction();
}

