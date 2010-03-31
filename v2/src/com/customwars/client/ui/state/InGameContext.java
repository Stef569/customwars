package com.customwars.client.ui.state;

import com.customwars.client.action.ActionManager;
import com.customwars.client.action.CWAction;
import com.customwars.client.controller.ClickHistory;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.InGameInputHandler;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.drop.DropLocationsQueue;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.network.MessageSender;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.renderer.MapRenderer;
import org.newdawn.slick.GameContainer;

import java.util.LinkedList;
import java.util.List;

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

  private Game game;
  private MoveTraverse moveTraverse;
  private GameRenderer gameRenderer;
  private InGameInputHandler inGameInputHandler;
  private HUD hud;
  private ControllerManager controllerManager;
  private ResourceManager resources;
  private GameContainer container;
  private StateChanger stateChanger;
  private StateSession stateSession;
  private CursorController cursorController;
  private MessageSender messageSender;

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

  public void setControllerManager(ControllerManager controllerManager) {
    this.controllerManager = controllerManager;
  }

  public void setTrapped(boolean trapped) {
    this.trapped = trapped;
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

  public void setInGameInputHandler(InGameInputHandler inGameInputHandler) {
    this.inGameInputHandler = inGameInputHandler;
  }

  public void setCursorController(CursorController cursorController) {
    this.cursorController = cursorController;
  }

  public void setStateChanger(StateChanger stateChanger) {
    this.stateChanger = stateChanger;
  }

  public void setStateSession(StateSession stateSession) {
    this.stateSession = stateSession;
  }

  public void setMessageSender(MessageSender messageSender) {
    this.messageSender = messageSender;
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

  public InGameInputHandler getInGameInputHandler() {
    return inGameInputHandler;
  }

  public StateChanger getStateChanger() {
    return stateChanger;
  }

  public CursorController getCursorController() {
    return cursorController;
  }

  public StateSession getSession() {
    return stateSession;
  }

  public MessageSender getMessageSender() {
    return messageSender;
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
