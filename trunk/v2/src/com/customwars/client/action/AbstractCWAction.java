package com.customwars.client.action;

/**
 * Default implementation of a CWAction
 *
 * @author stefan
 */
public abstract class AbstractCWAction implements CWAction {
  String name;
  boolean actionCompleted;
  boolean canUndo;

  public AbstractCWAction(String name) {
    this(name, true);
  }

  public AbstractCWAction(String name, boolean canUndo) {
    this.name = name;
    this.canUndo = canUndo;
  }

  public void update(int elapsedTime) {
  }

  public void doAction() {
    doActionImpl();
    actionCompleted = true;
  }

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

  public boolean canUndo() {
    return canUndo;
  }

  public String toString() {
    return name;
  }
}
