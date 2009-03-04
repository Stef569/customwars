package com.customwars.client.action;

/**
 * Base class for each Action in the game
 * an Action can be done and undone.
 *
 * @author stefan
 */
public abstract class CWAction {
  String name;
  boolean actionCompleted;
  boolean canUndo;

  protected CWAction(String name) {
    this(name, true);
  }

  protected CWAction(String name, boolean canUndo) {
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
  abstract void doActionImpl();

  abstract void undoAction();

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
