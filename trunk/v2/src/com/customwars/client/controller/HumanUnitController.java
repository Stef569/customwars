package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

/**
 * Allows a human to control a unit
 * by using a gui
 *
 * @author stefan
 */
public class HumanUnitController extends UnitController {
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private ShowPopupMenu showUnitPopupMenu;

  public HumanUnitController(Game game, Unit unit, ActionManager actionManager, MoveTraverse moveTraverse, InGameSession inGameSession, MapRenderer mapRenderer, HUD hud) {
    super(game, unit, actionManager, moveTraverse);
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
    this.showUnitPopupMenu = new ShowPopupMenu("Unit menu", hud, inGameSession, mapRenderer);
  }

  public void handleAPress() {
    Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();

    if (unit.getMoveZone().contains(cursorLocation) && isUnitVisible()) {
      if (canSelect(cursorLocation)) {
        doAction("SELECT_UNIT");
      } else if (canShowMenu()) {
        inGameSession.setClick(2, cursorLocation);
        initUnitActionMenu(cursorLocation);
      }
    } else {
      inGameSession.undo();
    }
  }

  public void handleBPress() {
    Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();
    Unit selectedUnit = map.getUnitOn(cursorLocation);
    if (isUnitVisible() && isUnitOn(cursorLocation) && selectedUnit.canFire()) {
      doAction("ATTACK_ZONE_UNIT");
    }
  }

  private boolean canShowMenu() {
    Unit activeUnit = game.getActiveUnit();
    Location clicked = mapRenderer.getCursorLocation();
    return activeUnit != null && (activeUnit.getLocation() == clicked || activeUnit.isWithinMoveZone(clicked));
  }

  private void initUnitActionMenu(Tile clicked) {
    Unit activeUnit = game.getActiveUnit();
    Location origin = activeUnit.getLocation();

    map.teleport(origin, clicked, activeUnit);
    buildUnitActionMenu(clicked);
    doAction(showUnitPopupMenu);
    map.teleport(clicked, origin, activeUnit);
  }

  private void buildUnitActionMenu(Tile clicked) {
    showUnitPopupMenu.clearActions();
    addToMenu(canWait(clicked), "UNIT_MOVE_WAIT", "Wait");
    addToMenu(canCapture(clicked), "UNIT_MOVE_CAPTURE_WAIT", "Capture");
    addToMenu(canSupply(clicked), "UNIT_MOVE_SUPPLY_WAIT", "Supply");
    addToMenu(canLoad(clicked), "UNIT_MOVE_LOAD_WAIT", "load");
//    addToMenu(canFire(clicked), "fire");
//    addToMenu(canDrop(clicked), "drop");
  }

  /**
   * Add a menu item to the menu backed by a CWAction
   *
   * @param condition  if the action should be added to the showpopup action
   * @param actionName The action to perform when clicked on the menu item
   * @param menuName   The menu item name
   */
  private void addToMenu(boolean condition, String actionName, String menuName) {
    if (condition)
      showUnitPopupMenu.addAction(actionManager.getAction(actionName), menuName);
  }

  private void doAction(String actionName) {
    CWAction action = actionManager.getAction(actionName);
    doAction(action);
  }

  private void doAction(CWAction action) {
    action.doAction();
    if (action.canUndo()) {
      inGameSession.addUndoAction(action);
    }
  }
}