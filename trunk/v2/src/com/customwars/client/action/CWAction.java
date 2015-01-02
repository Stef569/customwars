package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;

/**
 * Interface for each Action in the game
 * an Action can be done and undone.
 * <p/>
 * When the action has been performed {@link #isCompleted()} will return true.
 */
public interface CWAction {
  /**
   * Invokes/executes this action
   *
   * @param context the game context in where this action is executed
   */
  void invoke(InGameContext context);

  /**
   * Updates an action until it is finished
   *
   * @param elapsedTime The time that has passed in ms
   */
  void update(int elapsedTime);

  /**
   * @return Can this action be undone
   */
  boolean canUndo();

  /**
   * Undo this action, reverting any changes made
   */
  void undo();

  /**
   * @return The name of this action for debugging purposes
   */
  String getName();

  /**
   * @return Has this action been started.
   * In other words is invoke called.
   */
  boolean isStarted();

  /**
   * @return Has this action completed all it's goals
   */
  boolean isCompleted();

  /**
   * The action command that can be used to recreate this action. Each parameter is separated with a space.
   * Following pattern is used:
   * capture 0 0
   * build_unit 0 0
   * move 0 0 1 1
   * <action_name> <param1> <param2>
   *
   * @return The action command, null if this action has no action command.
   */
  String getActionCommand();
}
