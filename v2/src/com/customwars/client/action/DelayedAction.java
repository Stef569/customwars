package com.customwars.client.action;

/**
 * An action that is fired after a delay
 * Subclasses should set actionCompleted to true when the action has finished.
 *
 * @author stefan
 */
public abstract class DelayedAction extends CWAction {
  private int time, delay;
  private boolean running;

  public DelayedAction(String actionName, int delay) {
    super(actionName);
    this.delay = delay;
  }

  public void doAction() {
    if (!running) {
      init();
      running = true;
    }
  }

  public void undoAction() {
  }

  /**
   * Init the delayed action, called once before doActionImpl
   */
  protected abstract void init();

  public void update(int elapsedTime) {
    if (running) {
      time += elapsedTime;

      if (time >= delay) {
        actionPerformed();
        time = 0;
      }
    }
  }

  public final void actionPerformed() {
    if (!actionCompleted)
      doActionImpl();

    if (actionCompleted) {
      running = false;
    }
  }

  public void setActionCompleted(boolean completed) {
    super.actionCompleted = completed;
  }
}
