package com.customwars.client.ui.state;

import com.customwars.client.action.CWAction;
import com.customwars.client.action.UndoManager;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.renderer.MapRenderer;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;

import java.util.LinkedList;
import java.util.List;

/**
 * Allows to share information in the inGame State
 * Stores
 * a history of clicks in a tileMap and
 * a history of CWActions that can be undone
 * The droplocations and units to be dropped on those locations.
 *
 * It contains commonly used objects: ControllerManager, Game, MoveTraverse, MapRenderer, Hud
 * this allows to pass this object instead of all the above as a parameter.
 *
 * @author stefan
 */
public class InGameContext {
  private static final Logger logger = Logger.getLogger(InGameContext.class);

  // Input mode:
  public enum INPUT_MODE {
    DEFAULT,        // Clicking shows a Menu or selects a unit
    GUI,            // Input is handled by the GUI
    UNIT_SELECT,    // Clicking on a unit will select it
    UNIT_ATTACK,    // Clicking on a unit will attack it
    UNIT_DROP,      // Clicking on empty space drops the unit
    LAUNCH_ROCKET,  // Clicking on a tile fires the rocket
    UNIT_FLARE      // Clicking on a tile fires a flare
  }

  private static final int MAX_CLICK_HISTORY = 3;
  private static final Tile[] clicks = new Tile[MAX_CLICK_HISTORY];

  private final UndoManager undoManager;
  private CWAction action;

  private INPUT_MODE inputMode;
  private boolean trapped;
  private boolean moving;
  private final LinkedList<Tile> dropLocations;
  private final LinkedList<Unit> unitsToBeDropped;

  private Game game;
  private MoveTraverse moveTraverse;
  private GameRenderer gameRenderer;
  private GameController gameControl;
  private HUD hud;
  private ControllerManager controllerManager;
  private ResourceManager resources;
  private GameContainer container;

  public InGameContext() {
    undoManager = new UndoManager();
    unitsToBeDropped = new LinkedList<Unit>();
    dropLocations = new LinkedList<Tile>();
    setInputMode(INPUT_MODE.DEFAULT);
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

  public void clearClicks() {
    for (int i = 0; i < clicks.length; i++) {
      clicks[i] = null;
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
    undoManager.undo();
    logger.debug("Undo");
  }

  public void discartAllEdits() {
    undoManager.discartAllEdits();
  }

  public void doAction(String actionCommand) {
    // todo parse an actionCommand into a CWAction
  }

  /**
   * Convenient method to executes an action
   * If the action can be undone then it is added to the undo manager.
   */
  public void doAction(CWAction action) {
    if (action == null) {
      logger.warn("Trying to execute null action");
      return;
    }

    if (this.action == null) {
      logger.debug("Launching action -> " + action.getName());
      this.action = action;
      action.invoke(this);

      if (action.canUndo()) {
        undoManager.addUndoAction(action, this);
      }
    } else {
      logger.debug("Skipping action -> " + action.getName() + " other action " + this.action.getName() + " is still executing.");
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

  /**
   * @param index   base 1 index of the click(was it the first, second, ...) can't be higher then MAX_CLICK_HISTORY
   * @param clicked the tile that was clicked on
   */
  public void setClick(int index, Tile clicked) {
    clicks[index - 1] = clicked;
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
    return action == null;
  }

  /**
   * @param pos index to get a clicked Tile, base = 1
   */
  public Tile getClick(int pos) {
    return clicks[pos - 1];
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
    String toString = "Mode=" + inputMode + " [clicks=";

    for (Tile clicked : clicks) {
      toString += clicked + ", ";
    }
    return toString + "]";
  }
}
