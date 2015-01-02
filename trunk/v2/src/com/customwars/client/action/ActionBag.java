package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A List of CWActions
 * If the list contains delayed actions then this class
 * Will wait with processing the next actions until the delayed action has been completed.
 * <p/>
 * undo doesn't wait for the action to be completed, undo code should not be delayed.
 */
public class ActionBag implements CWAction {
  private static final int CHECK_ACTION_COMPLETE_DELAY = 250;
  private int time;
  private int index;
  private boolean doAll, started;
  private final List<CWAction> actions;
  private final String actionName;
  private InGameContext context;
  private String actionCommand;
  private boolean completed;

  public ActionBag(String actionName) {
    this.actionName = actionName;
    actions = new ArrayList<CWAction>();
    actionCommand = buildActionCommand();
  }

  public void invoke(InGameContext context) {
    this.context = context;
    doAll = true;
    started = true;
  }

  public void undo() {
    for (CWAction action : actions) {
      action.undo();
    }
  }

  /**
   * Keep looping until all actions have finished
   */
  public void update(int elapsedTime) {
    if (started && canPerformAction(elapsedTime)) {
      CWAction currentAction = actions.get(index);

      if (!currentAction.isCompleted()) {
        performAction(currentAction);
        currentAction.update(elapsedTime);
      }

      if (currentAction.isCompleted()) {
        gotoNextAction();
      }
    }
  }

  private boolean canPerformAction(int elapsedTime) {
    time += elapsedTime;
    return time >= CHECK_ACTION_COMPLETE_DELAY;
  }

  private void performAction(CWAction action) {
    if (doAll) {
      action.invoke(context);
    }
  }

  private void gotoNextAction() {
    if (++index >= actions.size()) {
      index = 0;
      doAll = false;
      completed = true;
    }
  }

  public void add(CWAction action) {
    actions.add(action);
    actionCommand = buildActionCommand();
  }

  private String buildActionCommand() {
    StringBuilder actionCMDBuilder = new StringBuilder(actionName.toLowerCase());
    actionCMDBuilder.append(" ");

    for (CWAction action : actions) {
      String actionCMD = action.getActionCommand();
      if (actionCMD != null) {
        actionCMDBuilder.append(actionCMD);
        actionCMDBuilder.append(" ");
      }
    }

    // Remove last " " space.
    actionCMDBuilder.deleteCharAt(actionCMDBuilder.length() - 1);
    return actionCMDBuilder.toString();
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

  public String getName() {
    return actionName;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isCompleted() {
    return completed;
  }

  public String getActionCommand() {
    return actionCommand;
  }

  @Override
  public String toString() {
    StringBuilder strBuilder = new StringBuilder(actionName + ":");
    for (CWAction action : actions) {
      strBuilder.append(action.getName()).append(" - ");
    }

    // Remove last " - "
    strBuilder.delete(strBuilder.length() - 3, strBuilder.length());
    return strBuilder.toString();
  }
}
