package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Allow Actions to be executed
 * Keep a history of actions that can be undone -> undoManager
 * Keep a history of actions that have an action command -> actionHistory
 * Allow actions to be put in a queue.
 */
public class ActionManager {
  private static final Logger logger = Logger.getLogger(ActionManager.class);
  private final UndoManager undoManager;
  private final InGameContext inGameContext;
  private final List<CWAction> actionHistory;
  private CWAction currentAction;
  private Queue<CWAction> queue;

  public ActionManager(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.undoManager = new UndoManager();
    this.actionHistory = new ArrayList<CWAction>(50);
  }

  /**
   * Executes an action
   * If the action can be undone then it is added to the undo history, and {@link #canUndo()} will return true
   * If the action had an action command then it is added to the action history.
   *
   * @param action The action to execute
   */
  public void doAction(CWAction action) {
    if (action == null) {
      logger.warn("Trying to execute null action");
      return;
    }

    if (currentAction == null) {
      invokeAction(action);

      if (action.canUndo()) {
        undoManager.addUndoAction(action, inGameContext);
      }
    } else {
      logger.debug("Skipping action -> " + action.getName() + " other action " + currentAction.getName() + " is still executing.");
    }
  }

  private void invokeAction(CWAction action) {
    logger.debug("Launching action -> " + action.getName());
    currentAction = action;
    action.invoke(inGameContext);

    if (action.getActionCommand() != null) {
      actionHistory.add(action);
    }
  }

  /**
   * Creates a new queue containing the given actions.
   * The actions will begin to execute right away.
   * No undo history will be stored. Queued actions cannot be undone.
   * Each executed action will be stored in the action history.
   * To stop the queued actions from executing, call clearQueue()
   *
   * @param actions the actions to be executed
   */
  public void queue(List<CWAction> actions) {
    this.queue = new LinkedList<CWAction>(actions);
  }

  /**
   * Clears all actions from the queue
   */
  public void clearQueue() {
    if (queue != null) {
      queue.clear();
      queue = null;
    }
  }

  public void update(int elapsedTime) {
    if (currentAction != null) {
      updateSingleAction(elapsedTime);
    } else {
      if (queue != null) {
        updateQueue(elapsedTime);
      }
    }
  }

  private void updateSingleAction(int elapsedTime) {
    currentAction.update(elapsedTime);

    if (currentAction.isCompleted()) {
      currentAction = null;
    }
  }

  private void updateQueue(int elapsedTime) {
    CWAction action = queue.peek();

    if (!action.isStarted()) {
      action.invoke(inGameContext);

      if (action.getActionCommand() != null) {
        actionHistory.add(action);
      }
    }

    if (action.isCompleted()) {
      queue.remove();

      if (queue.isEmpty()) {
        queue = null;
      }
    } else {
      action.update(elapsedTime);
    }
  }

  public boolean canUndo() {
    return undoManager.canUndo();
  }

  public void undoLastAction() {
    undoManager.undo();
    logger.debug("Undo");
  }

  public void clearUndoHistory() {
    undoManager.discartAllEdits();
  }

  public boolean isActionCompleted() {
    return currentAction == null;
  }

  public List<CWAction> getExecutedActions() {
    return Collections.unmodifiableList(actionHistory);
  }
}
