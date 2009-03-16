package com.customwars.client.ui.state;

import com.customwars.client.action.CWAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows to share information in the inGame State
 * Stores
 * a history of clicks in a tileMap and
 * a history of CWActions that can be undone
 * The droplocations and units to be dropped on those locations.
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
  private LinkedList<Tile> dropLocations;
  private LinkedList<Unit> unitsToBeDropped;
  private int undoCount = 0;
  private ControllerManager controllerManager;

  public InGameSession() {
    undoManager = new UndoManager();
    unitsToBeDropped = new LinkedList<Unit>();
    dropLocations = new LinkedList<Tile>();
    setMode(MODE.DEFAULT);
  }

  public void addHumanUnitController(Unit unit) {
    controllerManager.addHumanUnitController(unit);
  }

  public void addHumanCityController(City city) {
    controllerManager.addHumanCityController(city);
  }

  public void handleUnitAPress(Unit unit) {
    controllerManager.handleUnitAPress(unit);
  }

  public void handleCityAPress(City city) {
    controllerManager.handleCityAPress(city);
  }

  public void handleUnitBPress(Unit unit) {
    controllerManager.handleUnitBPress(unit);
  }

  public void addDropLocation(Tile location, Unit transporter) {
    unitsToBeDropped.add(transporter);
    dropLocations.add(location);
  }

  public void removeDropLocation(Unit unit, Tile location) {
    unitsToBeDropped.remove(unit);
    dropLocations.remove(location);
  }

  public void clearClicks() {
    for (int i = 0; i < clicks.length; i++) {
      clicks[i] = null;
    }
  }

  public void addUndoAction(CWAction action) {
    if (action.canUndo()) {
      undoManager.addEdit(new UndoWrapper(action));
      System.out.println(++undoCount + ". Adding " + undoManager.getPresentationName() + " to undo list");
    }
  }

  public void undoAll() {
    while (canUndo()) {
      undo();
    }
  }

  public boolean canUndo() {
    return undoManager.canUndo();
  }

  public void undo() {
    if (undoManager.canUndo()) {
      System.out.println(" -- " + undoManager.getUndoPresentationName() + " -- ");
      undoManager.undo();
      redoLast();
    }
  }

  private void redoLast() {
    if (undoManager.canUndo()) {
      undoManager.undo();
    } else {
      // Last action
      System.out.println(undoCount-- + ". Removing " + undoManager.getPresentationName() + " from undo list");
      undoManager.removeLastEdit();
    }

    if (undoManager.canRedo()) {
      undoManager.redo();
      System.out.println(undoCount-- + ". Removing " + undoManager.getPresentationName() + " from undo list");
      undoManager.removeLastEdit();
    }
  }

  public void doAction(CWAction action) {
    action.doAction();
    if (action.canUndo()) {
      addUndoAction(action);
    }
  }

  public void discartAllEdits() {
    undoCount = 0;
    System.out.println("Undo history cleared");
    undoManager.discardAllEdits();
  }

  public void setControllerManager(ControllerManager controllerManager) {
    this.controllerManager = controllerManager;
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

  public boolean isUnitDropped(Locatable unit) {
    return unitsToBeDropped.contains(unit);
  }

  public boolean isDropLocationTaken(Tile dropLocation) {
    return dropLocations.contains(dropLocation);
  }

  public void clearDropLocations() {
    dropLocations.clear();
    unitsToBeDropped.clear();
  }

  public void removeLastDropLocation() {
    if (!dropLocations.isEmpty()) {
      dropLocations.removeLast();
      unitsToBeDropped.removeLast();
    }
  }

  public List<Tile> getDropLocations() {
    return dropLocations;
  }

  public Tile getNextDropLocation() {
    return dropLocations.removeLast();
  }

  public Unit getNextUnitToBeDropped() {
    return unitsToBeDropped.removeLast();
  }

  public List<Unit> getUnitsToBeDropped() {
    return unitsToBeDropped;
  }

  public int getDropCount() {
    return dropLocations.size();
  }

  public String toString() {
    String toString = "Mode=" + mode + " [clicks=";

    for (Tile clicked : clicks) {
      toString += clicked + ", ";
    }
    return toString + "]";
  }

  /**
   * Wrap a CWAction in a AbstractUndoableEdit
   * so it can be used by the undoManager
   */
  class UndoWrapper extends AbstractUndoableEdit {
    private CWAction action;

    public UndoWrapper(CWAction action) {
      this.action = action;
    }

    public String getPresentationName() {
      return action.getName();
    }

    public void undo() throws CannotUndoException {
      super.undo();
      action.undoAction();
    }

    public void redo() throws CannotRedoException {
      super.redo();
      action.doAction();
    }
  }

  /**
   * Extend the swing undo manager
   * so we can access the protected methods/fields.
   */
  private class UndoManager extends javax.swing.undo.UndoManager {
    public void removeLastEdit() {
      int last = edits.size() - 1;

      if (last >= 0)
        trimEdits(last, last);
    }
  }
}
