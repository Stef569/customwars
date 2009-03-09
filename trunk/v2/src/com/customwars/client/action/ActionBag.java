package com.customwars.client.action;

import java.util.ArrayList;
import java.util.List;

/**
 * A List of CWActions
 * If the list contains delayed actions then this class
 * Will wait with processing the next actions until the delayed action has been completed.
 *
 * undo doesn't wait for the action to be completed, undo code should not be delayed.
 *
 * @author stefan
 */
public class ActionBag implements CWAction {
  private static final int CHECK_ACTION_COMPLETE_DELAY = 250;
  private int time;
  private int index;
  private boolean doAll, undoAll, started;
  private List<CWAction> actions;
  private String actionName;

  public ActionBag(String actionName) {
    this.actionName = actionName;
    actions = new ArrayList<CWAction>();
  }

  public void doAction() {
    doAll = true;
    started = true;
  }

  public void undoAction() {
    undoAll = true;
    started = true;
  }


  /**
   * Keep looping until all actions have finished
   */
  public void update(int elapsedTime) {
    if (started && canPerformAction(elapsedTime)) {
      CWAction currentAction = actions.get(index);

      if (!currentAction.isActionCompleted()) {
        performAction(currentAction);
      }

      if (currentAction.isActionCompleted()) {
        gotoNextAction();
        currentAction.setActionCompleted(false);
      }
    }
  }

  private boolean canPerformAction(int elapsedTime) {
    time += elapsedTime;
    return time >= CHECK_ACTION_COMPLETE_DELAY;
  }

  private void performAction(CWAction action) {
    if (doAll) {
      action.doAction();
    } else if (undoAll) {
      action.undoAction();
      action.setActionCompleted(true);
    }
  }

  private void gotoNextAction() {
    index++;
    if (index >= actions.size()) {
      index = 0;
      doAll = false;
      undoAll = false;
      started = false;
    }
  }

  public void addAction(CWAction action) {
    actions.add(action);
  }

  /**
   * @return if all actions can be undone
   */
  public boolean canUndo() {
    for (CWAction action : actions) {
      if (!action.canUndo())
        return false;
    }
    return true;
  }

  public void setActionCompleted(boolean b) {
    // Wait for update to finish
  }

  public String getName() {
    return actionName;
  }

  public boolean isActionCompleted() {
    return !started;
  }
}
