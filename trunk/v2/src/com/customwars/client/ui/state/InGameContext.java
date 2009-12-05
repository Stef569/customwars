package com.customwars.client.ui.state;

import com.customwars.client.action.ActionManager;
import com.customwars.client.action.CWAction;
import com.customwars.client.controller.ClickHistory;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.DropLocationsQueue;
import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.renderer.MapRenderer;
import org.newdawn.slick.GameContainer;

import java.util.LinkedList;
import java.util.List;

/**
 * Allows to share information while in the Game
 * Stores
 * A history of clicks in a tileMap and
 * A collection of CWActions that are executed on each update call and can be undone
 * The droplocations and units to be dropped on those locations.
 * The current input mode, this changes the way a click on the map is interpreted
 * References to any object that is available in the game: ControllerManager, Game, MoveTraverse, MapRenderer, Hud
 * this allows to pass 1 context object instead of all the above as a parameter.
 *
 * @author stefan
 */
public class InGameContext {
  private static final ClickHistory clickHistory = new ClickHistory(3);

  public enum INPUT_MODE {
    DEFAULT,        // Clicking shows a Menu or selects a unit
    GUI,            // Input is handled by the GUI
    UNIT_SELECT,    // Clicking on a unit will select it
    UNIT_ATTACK,    // Clicking on a unit will attack it
    UNIT_DROP,      // Clicking on empty space drops the unit
    LAUNCH_ROCKET,  // Clicking on a tile fires the rocket
    UNIT_FLARE      // Clicking on a tile fires a flare
  }

  private INPUT_MODE inputMode;
  private boolean trapped;
  private boolean moving;
  private final List<Unit> unitsInTransport;
  private final DropLocationsQueue dropQueue;
  private final ActionManager actionManager;

  private Game game;
  private MoveTraverse moveTraverse;
  private GameRenderer gameRenderer;
  private GameController gameControl;
  private HUD hud;
  private ControllerManager controllerManager;
  private ResourceManager resources;
  private GameContainer container;

  public InGameContext() {
    dropQueue = new DropLocationsQueue();
    actionManager = new ActionManager(this);
    inputMode = INPUT_MODE.DEFAULT;
    unitsInTransport = new LinkedList<Unit>();
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
   * Convenient method to executes an action
   * If the action can be undone then a call to undo will undo the action
   */
  public void doAction(CWAction action) {
    actionManager.doAction(action);
  }

  /**
   * Register a click to be saved in the clickHistory
   *
   * @param index         base 1 index of the click(was it the first, second, ...)
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

  public void setControllerManager(ControllerManager controllerManager) {
    this.controllerManager = controllerManager;
  }

  public void setTrapped(boolean trapped) {
    this.trapped = trapped;
  }

  public void setMoving(boolean moving) {
    this.moving = moving;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public void setHud(HUD hud) {
    this.hud = hud;
  }

  public void setMoveTraverse(MoveTraverse moveTraverse) {
    this.moveTraverse = moveTraverse;
  }

  public void setGameRenderer(GameRenderer gameRenderer) {
    this.gameRenderer = gameRenderer;
  }

  public void setResources(ResourceManager resources) {
    this.resources = resources;
  }

  public void setContainer(GameContainer container) {
    this.container = container;
  }

  public void setGameController(GameController gameControl) {
    this.gameControl = gameControl;
  }

  public Game getGame() {
    return game;
  }

  public HUD getHud() {
    return hud;
  }

  public MoveTraverse getMoveTraverse() {
    return moveTraverse;
  }

  public MapRenderer getMapRenderer() {
    return gameRenderer.getMapRenderer();
  }

  public GameRenderer getGameRenderer() {
    return gameRenderer;
  }

  public ControllerManager getControllerManager() {
    return controllerManager;
  }

  public ResourceManager getResourceManager() {
    return resources;
  }

  public GameContainer getContainer() {
    return container;
  }

  public GameController getGameController() {
    return gameControl;
  }

  public CursorController getCursorController() {
    return gameControl.getCursorController();
  }

  public boolean isTrapped() {
    return trapped;
  }

  public boolean isMoving() {
    return moving;
  }

  public boolean isActionCompleted() {
    return actionManager.isActionCompleted();
  }

  /**
   * Get a click from the click history eg:
   * getClick(1) returns the first click
   *
   * @param index The click index, base 1
   *              null if there is no click registered for the given index
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

  public void addUnitInTransport(Unit unitInTransport) {
    unitsInTransport.add(unitInTransport);
  }

  public Unit getUnitInTransport(int index) {
    return unitsInTransport.get(index);
  }

  public void clearUnitsInTransport() {
    unitsInTransport.clear();
  }

  public void addDropLocation(Tile location, Unit unit) {
    dropQueue.addDropLocation(location, unit);
  }

  /**
   * Has this unit already been assigned to a drop location by a previous drop
   */
  public boolean isUnitDropped(Unit unit) {
    return dropQueue.isUnitDropped(unit);
  }

  /**
   * Is this dropLocation already taken by a previous drop
   */
  public boolean isDropLocationTaken(Tile dropLocation) {
    return dropQueue.isDropLocationTaken(dropLocation);
  }

  public void clearDropHistory() {
    unitsInTransport.clear();
    dropQueue.clearDropLocations();
  }

  public Tile removeNextDropLocation() {
    return (Tile) dropQueue.removeNextDropLocation();
  }

  public Unit removeNextUnitToBeDropped() {
    return dropQueue.removeNextUnitToBeDropped();
  }

  public List<Unit> getUnitsToBeDropped() {
    return dropQueue.getUnitsToBeDropped();
  }

  public List<Location> getDropLocations() {
    return dropQueue.getDropLocations();
  }

  public String toString() {
    return "Mode=" + inputMode + " [clicks=" + clickHistory + "]";
  }
}
