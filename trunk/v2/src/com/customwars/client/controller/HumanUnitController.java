package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

import java.util.LinkedList;
import java.util.List;

/**
 * Allows a human to control a unit
 * by using a gui
 *
 * @author stefan
 */
public class HumanUnitController extends UnitController {
  private static final int DROP_LIMIT = 4;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private ShowPopupMenu showPopupMenu;
  private List<Unit> unitsInTransport;

  public HumanUnitController(Game game, Unit unit, ActionManager actionManager, MoveTraverse moveTraverse, InGameSession inGameSession, MapRenderer mapRenderer) {
    super(game, unit, actionManager, moveTraverse);
    this.inGameSession = inGameSession;
    this.mapRenderer = mapRenderer;
    this.showPopupMenu = (ShowPopupMenu) actionManager.getAction("POPUP_MENU");
    unitsInTransport = new LinkedList<Unit>();
  }

  public void handleAPress() {
    Tile selected = (Tile) mapRenderer.getCursorLocation();

    if (inGameSession.isUnitDropMode() && canDrop(selected)) {
      addDropLocation(selected);
      initUnitActionMenu(selected);
    } else if (inGameSession.isUnitAttackMode() && canAttack(selected)) {
      inGameSession.setClick(3, selected);
      actionManager.doAction("UNIT_MOVE_ATTACK_WAIT");
    } else if (unit.getMoveZone().contains(selected)) {
      if (canShowMenu()) {
        inGameSession.setClick(2, selected);
        initUnitActionMenu(selected);
      } else if (canSelect(selected)) {
        inGameSession.clearClicks();
        inGameSession.discartAllEdits();
        inGameSession.setClick(1, selected);
        actionManager.doAction("SELECT_UNIT");
      }
    } else {
      inGameSession.undo();
    }
  }

  private void addDropLocation(Tile location) {
    int popupOption = showPopupMenu.getCurrentOption();
    inGameSession.addDropLocation(location, unitsInTransport.get(popupOption));
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
    Location selected = mapRenderer.getCursorLocation();
    return activeUnit != null && activeUnit.isWithinMoveZone(selected);
  }

  private void initUnitActionMenu(Tile selected) {
    Unit activeUnit = game.getActiveUnit();
    Tile origin = (Tile) activeUnit.getLocation();

    showPopupMenu.clear();
    buildActionMenu(origin, selected, activeUnit);

    if (showPopupMenu.atLeastHasOneItem()) {
      inGameSession.doAction(showPopupMenu);
    }
  }

  private void buildActionMenu(Tile origin, Tile selected, Unit activeUnit) {
    if (isActiveUnit() && canMove(origin, selected)) {
      if (inGameSession.isUnitDropMode()) {
        // In drop mode teleport the transporter to the 2ND selected tile
        Tile transportLocation = inGameSession.getClick(2);
        map.teleport(origin, transportLocation, activeUnit);
        buildDropModeMenu(transportLocation, selected);
        map.teleport(transportLocation, origin, activeUnit);
      } else {
        map.teleport(origin, selected, activeUnit);
        buildUnitActionMenu(origin, selected);
        map.teleport(selected, origin, activeUnit);
      }
    }
  }

  private void buildUnitActionMenu(Tile origUnitLocation, Tile selected) {
    if (canWait(selected)) {
      if (canStartDrop(selected, selected, 1)) buildDropModeMenu(selected, selected);
      addToMenu(canCapture(selected), "UNIT_MOVE_CAPTURE_WAIT", "Capture");
      addToMenu(canSupply(selected), "UNIT_MOVE_SUPPLY_WAIT", "Supply");
      addToMenu(canStartAttack(origUnitLocation, selected), "UNIT_START_ATTACK_MODE", "Fire");
      addToMenu(canWait(selected), "UNIT_MOVE_WAIT", "Wait");
    } else {
      // Actions where the active and selected unit are on the same tile.
      addToMenu(canJoin(selected), "UNIT_MOVE_JOIN_WAIT", "Join");
      addToMenu(canLoad(selected), "UNIT_MOVE_LOAD_WAIT", "Load");
    }
  }

  private void buildDropModeMenu(Tile transportLocation, Tile selected) {
    unitsInTransport.clear();

    if (canWait(transportLocation)) {
      for (int dropCount = 0; dropCount < DROP_LIMIT; dropCount++) {
        if (dropCount < unit.getLocatableCount() && !inGameSession.isUnitDropped(unit.getLocatable(dropCount))) {
          Unit unitInTransport = (Unit) unit.getLocatable(dropCount);
          unitsInTransport.add(unitInTransport);
          addToMenu(canStartDrop(transportLocation, selected, dropCount + 1), "UNIT_START_DROP_MODE", "Drop " + unitInTransport.getName());
        }
      }

      // In drop mode the wait Button acts as the drop Action
      if (inGameSession.isUnitDropMode()) {
        CWAction dropAction = actionManager.buildDropAction(inGameSession.getDropCount());
        actionManager.addAction("unitdropmultiple", dropAction);
        showPopupMenu.addAction(dropAction, "Wait");
      }
    }
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
      showPopupMenu.addAction(actionManager.getAction(actionName), menuName);
  }
}
