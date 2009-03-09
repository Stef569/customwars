package com.customwars.client.action;

/**
 * Interface for each Action in the game
 * an Action can be done and undone.
 *
 * When the action has been performed isActionCompleted will return true
 *
 * @author stefan
 */
public interface CWAction {
  public void update(int elapsedTime);

  public void doAction();

  public void undoAction();

  public String getName();

  public boolean isActionCompleted();

  /**
   * @return Can this action be undone
   */
  boolean canUndo();

  void setActionCompleted(boolean completed);
}
