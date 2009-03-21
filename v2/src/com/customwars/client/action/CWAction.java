package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;

/**
 * Interface for each Action in the game
 * an Action can be done and undone.
 *
 * When the action has been performed isActionCompleted will return true
 *
 * @author stefan
 */
public interface CWAction {
  void invoke(InGameContext context);

  void update(int elapsedTime);

  boolean canUndo();

  void undo();

  String getName();

  boolean isCompleted();
}
