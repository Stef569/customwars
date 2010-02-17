package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allow Actions to be executed
 * Keep a history of actions that can be undone -> undoManager
 * Keep a history of actions that have an action text -> actionHistory
 */
public class ActionManager {
  private static final Logger logger = Logger.getLogger(ActionManager.class);
  private final UndoManager undoManager;
  private final InGameContext inGameContext;
  private final List<CWAction> actionHistory;
  private CWAction action;

  public ActionManager(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.undoManager = new UndoManager();
    this.actionHistory = new ArrayList<CWAction>(50);
  }

  /**
   * Executes an action
   * If the action can be undone then it is added to the undo history, and canUndo will return true
   */
  public void doAction(CWAction action) {
    if (action == null) {
      logger.warn("Trying to execute null action");
      return;
    }

    if (this.action == null) {
      invokeAction(action);

      if (action.canUndo()) {
        undoManager.addUndoAction(action, inGameContext);
      }
    } else {
      logger.debug("Skipping action -> " + action.getName() + " other action " + this.action.getName() + " is still executing.");
    }
  }

  private void invokeAction(CWAction action) {
    logger.debug("Launching action -> " + action.getName());
    this.action = action;
    action.invoke(inGameContext);

    if (action.getActionText() != null) {
      actionHistory.add(action);
    }
  }

  public void update(int elapsedTime) {
    if (action != null) {
      action.update(elapsedTime);

      if (action.isCompleted()) {
        action = null;
      }
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
    return action == null;
  }

  public List<CWAction> getExecutedActions() {
    return Collections.unmodifiableList(actionHistory);
  }
}
