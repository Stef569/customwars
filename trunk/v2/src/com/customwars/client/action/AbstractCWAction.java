package com.customwars.client.action;

/**
 * Default implementation of a CWAction
 */
public abstract class AbstractCWAction implements CWAction {
  private String name;
  private boolean canUndo;
  private boolean actionCompleted;

  public AbstractCWAction(String name) {
    this(name, true);
  }

  public AbstractCWAction(String name, boolean canUndo) {
    this.name = name;
    this.canUndo = canUndo;
  }

  public void undo() {
  }

  public boolean canUndo() {
    return canUndo;
  }

  public String getName() {
    return name;
  }

  protected void setActionCompleted(boolean actionCompleted) {
    this.actionCompleted = actionCompleted;
  }

  public boolean isCompleted() {
    return actionCompleted;
  }

  @Override
  public String getActionCommand() {
    return null;
  }

  public String toString() {
    return String.format("%s canUndo=%s Completed=%s actionTxt=%s", name, canUndo, actionCompleted, getActionCommand());
  }
}
