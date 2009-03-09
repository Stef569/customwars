package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
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
    if (!isUnitVisible()) return;

    Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();

    if (inGameSession.isUnitDropMode() && canDrop(cursorLocation)) {
      inGameSession.setClick(3, cursorLocation);
      actionManager.doAction("UNIT_MOVE_DROP_WAIT");
    } else if (inGameSession.isUnitAttackMode() && canAttack(cursorLocation)) {
      inGameSession.setClick(3, cursorLocation);
      actionManager.doAction("UNIT_MOVE_ATTACK_WAIT");
    } else if (unit.getMoveZone().contains(cursorLocation)) {
      if (canShowMenu()) {
        inGameSession.setClick(2, cursorLocation);
        initUnitActionMenu(cursorLocation);
      } else if (canSelect(cursorLocation)) {
        inGameSession.discartAllEdits();
        inGameSession.clearClicks();
        inGameSession.setClick(1, cursorLocation);
        actionManager.doAction("SELECT_UNIT");
      }
    } else {
      inGameSession.undo();
    }
  }

  public void handleBPress() {
    Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();
    Unit selectedUnit = map.getUnitOn(cursorLocation);
    if (isUnitVisible() && isUnitOn(cursorLocation) && selectedUnit.canFire()) {
      actionManager.doAction("ATTACK_ZONE_UNIT");
    }
  }

  private boolean canShowMenu() {
    Unit activeUnit = game.getActiveUnit();
    Location clicked = mapRenderer.getCursorLocation();
    return activeUnit != null && (activeUnit.getLocation() == clicked || activeUnit.isWithinMoveZone(clicked));
  }

  private void initUnitActionMenu(Tile selected) {
    Unit activeUnit = game.getActiveUnit();
    Location origin = activeUnit.getLocation();

    map.teleport(origin, selected, activeUnit);
    buildUnitActionMenu(selected);
    if (showUnitPopupMenu.atLeastHasOneItem()) {
      inGameSession.doAction(showUnitPopupMenu);
    }
    map.teleport(selected, origin, activeUnit);
  }

  private void buildUnitActionMenu(Tile selected) {
    Tile firstSelection = inGameSession.getClick(1);
    showUnitPopupMenu.clearActions();
    addToMenu(canCapture(selected), "UNIT_MOVE_CAPTURE_WAIT", "Capture");
    addToMenu(canSupply(selected), "UNIT_MOVE_SUPPLY_WAIT", "Supply");
    addToMenu(canLoad(selected), "UNIT_MOVE_LOAD_WAIT", "Load");
    addToMenu(canStartAttack(firstSelection, selected), "UNIT_START_ATTACK_MODE", "Fire");
    addToMenu(canStartDrop(selected), "UNIT_START_DROP_MODE", "Drop");
    addToMenu(canWait(selected), "UNIT_MOVE_WAIT", "Wait");
  }

  /**
   * Add a menu item to the menu backed by a AbstractCWAction
   *
   * @param condition  if the action should be added to the showpopup action
   * @param actionName The action to perform when clicked on the menu item
   * @param menuName   The menu item name
   */
  private void addToMenu(boolean condition, String actionName, String menuName) {
    if (condition)
      showUnitPopupMenu.addAction(actionManager.getAction(actionName), menuName);
  }
}
