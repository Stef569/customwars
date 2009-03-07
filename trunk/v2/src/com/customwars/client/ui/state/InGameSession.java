package com.customwars.client.ui.state;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.map.Tile;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Allows to share information in the inGameState
 * Stores
 * a history of clicks in a tileMap and
 * a history of CWactions
 *
 * @author stefan
 */
public class InGameSession {
  // Input mode:
  public enum MODE {
    DEFAULT,        // Clicking shows a Menu or selects a unit
    GUI,            // Input is handled by the GUI
    UNIT_SELECT,    // Clicking on a unit will select it
    UNIT_ATTACK,    // Clicking on a unit will attack it
    UNIT_DROP       // Clicking on empty space drops the unit
  }

  private static final int MAX_CLICK_HISTORY = 3;
  private Tile[] clicks = new Tile[MAX_CLICK_HISTORY];
  private UndoManager undoManager;
  private MODE mode;
  private boolean trapped;
  private boolean moving;
  private int undoCount = 0;

  public InGameSession() {
    this.undoManager = new UndoManager();
    setMode(MODE.DEFAULT);
  }

  public void clearClicks() {
    for (int i = 0; i < clicks.length; i++) {
      clicks[i] = null;
    }
  }

  /**
   * @param index   base 1 index of the click(was it the first, second, ...) can't be higher then MAX_CLICK_HISTORY
   * @param clicked the tile that was clicked on
   */
  public void setClick(int index, Tile clicked) {
    clicks[index - 1] = clicked;
  }

  public void setMode(MODE mode) {
    this.mode = mode;
  }

  public void setTrapped(boolean trapped) {
    this.trapped = trapped;
  }

  public void setMoving(boolean moving) {
    this.moving = moving;
  }

  public void addUndoAction(CWAction action) {
    undoManager.addEdit(new Undo(action));
    System.out.println("adding " + undoManager.getPresentationName() + " to undo list Undocount=" + ++undoCount);
  }

  public void undoAll() {
    while (undoManager.canUndo()) {
      undo();
    }
  }

  /**
   * Undo the last added action
   * Actions that can be undo can be added by invoking addUndoAction(CWAction)
   */
  public void undo() {
    if (undoManager.canUndo()) {
      System.out.println(undoManager.getUndoPresentationName() + " Undocount=" + --undoCount);
      undoManager.undo();
    }
  }

  public void discartAllEdits() {
    undoCount = 0;
    System.out.println("Undo history cleared");
    undoManager.discardAllEdits();
  }

  public boolean isTrapped() {
    return trapped;
  }

  public boolean isMoving() {
    return moving;
  }

  /**
   * @param pos index to get a clicked Tile, base = 1
   */
  public Tile getClick(int pos) {
    return clicks[pos - 1];
  }

  public boolean isDefaultMode() {
    return mode == MODE.DEFAULT;
  }

  public boolean isGUIMode() {
    return mode == MODE.GUI;
  }

  public boolean isUnitSelectMode() {
    return mode == MODE.UNIT_SELECT;
  }

  public boolean isUnitAttackMode() {
    return mode == MODE.UNIT_ATTACK;
  }

  public boolean isUnitDropMode() {
    return mode == MODE.UNIT_DROP;
  }

  public String toString() {
    String toString = "Mode=" + mode + " [clicks=";

    for (Tile clicked : clicks) {
      toString += clicked + ", ";
    }
    return toString + "]";
  }

  class Undo extends AbstractUndoableEdit {
    private CWAction action;

    public Undo(CWAction action) {
      this.action = action;
    }

    public String getPresentationName() {
      return action.getName();
    }

    public void undo() throws CannotUndoException {
      super.undo();
      action.undoAction();
    }
  }
}
