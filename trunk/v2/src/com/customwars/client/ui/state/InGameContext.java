package com.customwars.client.ui.state;

import com.customwars.client.action.ActionManager;
import com.customwars.client.action.CWAction;
import com.customwars.client.controller.ClickHistory;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.drop.DropLocationsQueue;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This object uses the Service locator pattern.
 * To lookup objects that are shared while playing a Game
 * Execution and undo of CWActions are delegated to the ActionManager.
 *
 * Objects that are stored:
 * A history of clicks in a tileMap
 * The drop locations and units to be dropped on those locations.
 * The current input mode, this changes the way a click on the map is interpreted
 * ControllerManager, Game, MoveTraverse, MapRenderer, Hud
 *
 * @author stefan
 */
public class InGameContext {
  public enum INPUT_MODE {
    DEFAULT,        // Clicking shows a Menu or selects a unit
    GUI,            // Input is handled by the GUI
    UNIT_SELECT,    // Clicking on a unit will select it
    UNIT_ATTACK,    // Clicking on a unit will attack it
    UNIT_DROP,      // Clicking on empty space drops the unit
    LAUNCH_ROCKET,  // Clicking on a tile fires the rocket
    UNIT_FLARE,     // Clicking on a tile fires a flare
    UNIT_CYCLE      // Start Iterating between units
  }

  private INPUT_MODE inputMode;
  private boolean trapped;
  private final List<Unit> unitsInTransport;
  private final DropLocationsQueue dropQueue;
  private final ActionManager actionManager;
  private final ClickHistory clickHistory = new ClickHistory(3);

  private final Map<Class, Object> objectLookup;

  public InGameContext() {
    dropQueue = new DropLocationsQueue();
    actionManager = new ActionManager(this);
    inputMode = INPUT_MODE.DEFAULT;
    unitsInTransport = new LinkedList<Unit>();
    objectLookup = new HashMap<Class, Object>();
  }

  public <T> void registerObj(Class<T> objType, T obj) {
    objectLookup.put(objType, obj);
  }

  @SuppressWarnings("unchecked")
  public <T> T getObj(Class<T> objType) {
    Object obj = objectLookup.get(objType);
    return (T) obj;
  }

  public void handleUnitAPress(Unit unit) {
    ControllerManager controllerManager = getObj(ControllerManager.class);
    controllerManager.handleUnitAPress(unit);
  }

  public void handleCityAPress(City city) {
    ControllerManager controllerManager = getObj(ControllerManager.class);
    controllerManager.handleCityAPress(city);
  }

  public void handleUnitBPress(Unit unit) {
    ControllerManager controllerManager = getObj(ControllerManager.class);
    controllerManager.handleUnitBPress(unit);
  }

  public void update(int elapsedTime) {
    actionManager.update(elapsedTime);
  }

  public boolean canUndo() {
    return actionManager.canUndo();
  }

  public void undo() {
    actionManager.undoLastAction();
  }

  public void clearUndoHistory() {
    actionManager.clearUndoHistory();
  }

  /**
   * Executes an action
   * If the action can be undone then a call to undo will undo the action
   *
   * @param action The action to perform immediately
   */
  public void doAction(CWAction action) {
    actionManager.doAction(action);
  }

  /**
   * Register a click to be saved in the clickHistory
   *
   * @param index         index of the click starting at 1
   * @param tileClickedOn the tile that was clicked on
   */
  public void registerClick(int index, Tile tileClickedOn) {
    clickHistory.registerClick(index, tileClickedOn);
  }

  public void clearClickHistory() {
    clickHistory.clear();
  }

  public void setInputMode(INPUT_MODE inputMode) {
    this.inputMode = inputMode;
  }

  public void setTrapped(boolean trapped) {
    this.trapped = trapped;
  }

  public List<CWAction> getExecutedActions() {
    return actionManager.getExecutedActions();
  }

  public boolean isTrapped() {
    return trapped;
  }

  public boolean isActionCompleted() {
    return actionManager.isActionCompleted();
  }

  /**
   * Get a click from the click history eg:
   * getClick(1) returns the first click
   *
   * @param index The click index, starting at 1
   *              null if there is no click registered for the given index
   * @return The tile at the given click index
   */
  public Tile getClick(int index) {
    return clickHistory.getClick(index);
  }

  public boolean isInUnitMode() {
    return isUnitSelectMode() || isUnitAttackMode() || isUnitDropMode() || isRocketLaunchMode();
  }

  public boolean isDefaultMode() {
    return inputMode == INPUT_MODE.DEFAULT;
  }

  public boolean isGUIMode() {
    return inputMode == INPUT_MODE.GUI;
  }

  public boolean isUnitSelectMode() {
    return inputMode == INPUT_MODE.UNIT_SELECT;
  }

  public boolean isUnitAttackMode() {
    return inputMode == INPUT_MODE.UNIT_ATTACK;
  }

  public boolean isUnitDropMode() {
    return inputMode == INPUT_MODE.UNIT_DROP;
  }

  public boolean isRocketLaunchMode() {
    return inputMode == INPUT_MODE.LAUNCH_ROCKET;
  }

  public boolean isUnitFlareMode() {
    return inputMode == INPUT_MODE.UNIT_FLARE;
  }

  public boolean isUnitCycleMode() {
    return inputMode == INPUT_MODE.UNIT_CYCLE;
  }

  public void addUnitInTransport(Unit unitInTransport) {
    unitsInTransport.add(unitInTransport);
  }

  public Unit getUnitInTransport(int index) {
    return unitsInTransport.get(index);
  }

  public void clearUnitsInTransport() {
    unitsInTransport.clear();
  }

  /**
   * @see DropLocationsQueue#addDropLocation(Location, Unit)
   */
  public void addDropLocation(Location location, Unit unit) {
    dropQueue.addDropLocation(location, unit);
  }

  /**
   * @see DropLocationsQueue#isUnitDropped(Unit)
   */
  public boolean isUnitDropped(Unit unit) {
    return dropQueue.isUnitDropped(unit);
  }

  /**
   * @see DropLocationsQueue#isDropLocationTaken(Tile)
   */
  public boolean isDropLocationTaken(Tile dropLocation) {
    return dropQueue.isDropLocationTaken(dropLocation);
  }

  public void clearDropHistory() {
    unitsInTransport.clear();
    dropQueue.clearDropLocations();
  }

  public DropLocationsQueue getDropQueue() {
    return dropQueue;
  }

  public String toString() {
    return "Mode=" + inputMode + " [clicks=" + clickHistory + "]";
  }
}
