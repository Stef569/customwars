package com.customwars.client.ui.state;

import com.customwars.client.action.CWAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import org.apache.log4j.Logger;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Iterator;
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
  public enum MODE {
    DEFAULT,        // Clicking shows a Menu or selects a unit
    GUI,            // Input is handled by the GUI
    UNIT_SELECT,    // Clicking on a unit will select it
    UNIT_ATTACK,    // Clicking on a unit will attack it
    UNIT_DROP       // Clicking on empty space drops the unit
  }

  private static final int MAX_CLICK_HISTORY = 3;
  private Tile[] clicks = new Tile[MAX_CLICK_HISTORY];

  private final UndoManager undoManager;
  private final List<CWAction> actionQueue;

  private MODE mode;
  private boolean trapped;
  private boolean moving;
  private final LinkedList<Tile> dropLocations;
  private final LinkedList<Unit> unitsToBeDropped;
  private int undoCount = 0;

  private Game game;
  private MoveTraverse moveTraverse;
  private MapRenderer mapRenderer;
  private HUD hud;
  private ControllerManager controllerManager;

  public InGameContext() {
    undoManager = new UndoManager();
    unitsToBeDropped = new LinkedList<Unit>();
    dropLocations = new LinkedList<Tile>();
    actionQueue = new LinkedList<CWAction>();
    setMode(MODE.DEFAULT);
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
    if (undoManager.canUndo()) {
      logger.debug(" << " + undoManager.getUndoPresentationName() + " >> ");
      undoManager.undo();
      redoLast();
    }
  }

  private void redoLast() {
    if (undoManager.canUndo()) {
      undoManager.undo();
    } else {
      // Last action
      logger.debug(undoCount-- + ". Removing " + undoManager.getPresentationName() + " from undo list");
      undoManager.removeLastEdit();
    }

    if (undoManager.canRedo()) {
      undoManager.redo();
      logger.debug(undoCount-- + ". Removing " + undoManager.getPresentationName() + " from undo list");
      undoManager.removeLastEdit();
    }
  }

  public void doAction(String actionCommand) {
    // todo parse an actionCommand into a CWAction
  }

  public void doAction(CWAction action) {
    action.invoke(this);
    if (action.canUndo()) {
      addUndoAction(action);
    }

    actionQueue.add(action);
  }

  private void addUndoAction(CWAction action) {
    if (action.canUndo()) {
      undoManager.addEdit(new UndoWrapper(action, this));
      logger.debug(++undoCount + ". Adding " + undoManager.getPresentationName() + " to undo list");
    }
  }

  /**
   * Update the actions in the queue, until they are all completed
   */
  public void update(int elapsedTime) {
    if (actionQueue.isEmpty()) return;
    Iterator it = actionQueue.iterator();

    while (it.hasNext()) {
      CWAction action = (CWAction) it.next();
      action.update(elapsedTime);

      if (action.isCompleted()) {
        it.remove();
      }
    }
  }

  public void discartAllEdits() {
    undoCount = 0;
    undoManager.discardAllEdits();
    logger.debug("Undo history cleared");
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

  public void setMapRenderer(MapRenderer mapRenderer) {
    this.mapRenderer = mapRenderer;
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
    return mapRenderer;
  }

  public ControllerManager getControllerManager() {
    return controllerManager;
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

  public boolean isInUnitMode() {
    return isUnitSelectMode() || isUnitAttackMode() || isUnitDropMode();
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
    private InGameContext inGameContext;

    public UndoWrapper(CWAction action, InGameContext inGameContext) {
      this.action = action;
      this.inGameContext = inGameContext;
    }

    public String getPresentationName() {
      return action.getName();
    }

    public void undo() throws CannotUndoException {
      super.undo();
      action.undo();
    }

    public void redo() throws CannotRedoException {
      super.redo();
      action.invoke(inGameContext);
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
