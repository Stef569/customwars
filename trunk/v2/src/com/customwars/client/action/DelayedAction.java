package com.customwars.client.action;

/**
 * An action that is fired after a delay
 * Subclasses should set actionCompleted to true when the action has finished.
 *
 * @author stefan
 */
public abstract class DelayedAction extends AbstractCWAction {
  private String actionName;
  private boolean actionCompleted;
  private int time;
  private int delay;
  private boolean running;

  public DelayedAction(String actionName, boolean canUndo, int delay) {
    super(actionName, canUndo);
    this.actionName = actionName;
    this.delay = delay;
  }

  public void doAction() {
    if (!running) {
      init();
      running = true;
    }
  }

  /**
   * Init the delayed action, called once before doActionImpl
   */
  protected abstract void init();

  public void update(int elapsedTime) {
    if (running) {
      time += elapsedTime;

      if (time >= delay) {
        updateAction();
        time = 0;
      }
    }
  }

  public final void updateAction() {
    if (!actionCompleted)
      doActionImpl();

    if (actionCompleted) {
      running = false;
    }
  }

  /**
   * The delayed action code
   */
  public abstract void doActionImpl();

  public void undoAction() {
  }

  public void setActionCompleted(boolean actionCompleted) {
    this.actionCompleted = actionCompleted;
  }

  public String getName() {
    return actionName;
  }

  public boolean isActionCompleted() {
    return actionCompleted;
  }
}
