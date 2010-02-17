package com.customwars.client.action;

import com.customwars.client.model.map.Location;
import com.customwars.client.ui.state.InGameContext;

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
  private boolean doAll, started;
  private final List<CWAction> actions;
  private final String actionName;
  private InGameContext context;
  private String actionText;

  public ActionBag(String actionName) {
    this.actionName = actionName;
    actions = new ArrayList<CWAction>();
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
      started = false;
    }
  }

  public void add(CWAction action) {
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

  public void setActionText(String actionText) {
    this.actionText = actionText;
  }

  /**
   * Set the action text where the action happens on 1 tile
   */
  public void setActionText(Location tile, Integer... params) {
    setActionText(tile, null, params);
  }

  /**
   * Set the action text where the action starts at <from> and ends on <to>
   */
  public void setActionText(Location from, Location to, Integer... params) {
    StringBuilder actionTextBuilder = new StringBuilder(actionName.toLowerCase());
    actionTextBuilder
      .append(' ').append(from.getCol())
      .append(' ').append(from.getRow());

    if (to != null) {
      actionTextBuilder
        .append(' ').append(to.getCol())
        .append(' ').append(to.getRow());
    }

    for (Integer param : params) {
      actionTextBuilder
        .append(' ')
        .append(param);
    }
    this.actionText = actionTextBuilder.toString();
  }

  public String getName() {
    return actionName;
  }

  public boolean isCompleted() {
    return !started;
  }

  public String getActionText() {
    return actionText;
  }

  @Override
  public String toString() {
    StringBuilder strBuilder = new StringBuilder(actionName + ":");
    for (CWAction action : actions) {
      strBuilder.append(action.getName()).append(" - ");
    }
    return strBuilder.toString();
  }
}
