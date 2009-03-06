package com.customwars.client.action;

/**
 * Base class for each Action in the game
 * an Action can be done and undone.
 *
 * When the action has been performed actioncomplete will be true
 *
 * @author stefan
 */
public abstract class CWAction {
  String name;
  boolean actionCompleted;
  boolean canUndo;

  public CWAction(String name) {
    this(name, true);
  }

  public CWAction(String name, boolean canUndo) {
    this.name = name;
    this.canUndo = canUndo;
  }

  public void update(int elapsedTime) {
  }

  public void doAction() {
    doActionImpl();
    actionCompleted = true;
  }

  /**
   * Contains the action code
   */
  protected abstract void doActionImpl();

  public void undoAction() {
  }

  public void setActionCompleted(boolean completed) {
    this.actionCompleted = completed;
  }

  public String getName() {
    return name;
  }

  public boolean isActionCompleted() {
    return actionCompleted;
  }

  /**
   * @return can this action be undone
   */
  public boolean canUndo() {
    return canUndo;
  }

  public String toString() {
    return name;
  }
}
