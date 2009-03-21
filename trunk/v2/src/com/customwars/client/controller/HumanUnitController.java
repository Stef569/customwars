package com.customwars.client.controller;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.action.unit.SelectAction;
import com.customwars.client.action.unit.ShowAttackZoneAction;
import com.customwars.client.action.unit.StartAttackAction;
import com.customwars.client.action.unit.StartDropAction;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

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
  private InGameContext context;
  private MapRenderer mapRenderer;
  private List<Unit> unitsInTransport;
  private ShowPopupMenu showMenu;
  private boolean canStartDrop, canCapture, canSupply, canStartAttack, canWait, canJoin, canLoad;

  public HumanUnitController(Unit unit, InGameContext inGameContext) {
    super(unit, inGameContext);
    this.context = inGameContext;
    this.mapRenderer = inGameContext.getMapRenderer();
    unitsInTransport = new LinkedList<Unit>();
  }

  public void handleAPress() {
    Tile selected = (Tile) mapRenderer.getCursorLocation();

    if (context.isUnitDropMode() && canDrop(selected)) {
      addDropLocation(selected);
      initUnitActionMenu(selected);
    } else if (context.isUnitAttackMode() && canAttack(selected)) {
      Unit defender = (Unit) selected.getLastLocatable();
      Location to = context.getClick(2);
      CWAction attackAction = ActionFactory.buildAttackAction(unit, defender, to);
      context.doAction(attackAction);
    } else if (unit.getMoveZone().contains(selected)) {
      if (canShowMenu()) {
        context.setClick(2, selected);
        initUnitActionMenu(selected);
      } else if (canSelect(selected)) {
        context.clearClicks();
        context.discartAllEdits();
        context.setClick(1, selected);
        context.doAction(new SelectAction(selected));
      }
    } else {
      context.undo();
    }
  }

  private void addDropLocation(Tile location) {
    // The menu option clicked on is the index of the unit in the transport
    int popupOption = showMenu.getCurrentOption();
    context.addDropLocation(location, unitsInTransport.get(popupOption));
  }

  public void handleBPress() {
    Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();
    Unit selectedUnit = map.getUnitOn(cursorLocation);
    if (isUnitVisible() && isUnitOn(cursorLocation) && selectedUnit.canFire()) {
      context.doAction(new ShowAttackZoneAction(selectedUnit));
    }
  }

  private boolean canShowMenu() {
    Unit activeUnit = game.getActiveUnit();
    Location selected = mapRenderer.getCursorLocation();
    return activeUnit != null && activeUnit.isWithinMoveZone(selected);
  }

  private void initUnitActionMenu(Tile selected) {
    Tile from = (Tile) unit.getLocation();

    buildUnitActionMenu(from, selected);

    if (showMenu.atLeastHasOneItem()) {
      context.doAction(showMenu);
    }
  }

  private ShowPopupMenu buildUnitActionMenu(Tile from, Tile selected) {
    ShowPopupMenu showMenu = null;
    if (isActiveUnit() && canMove(from, selected)) {
      if (context.isUnitDropMode()) {
        // In drop mode teleport the transporter to the 2ND selected tile
        Tile to = context.getClick(2);
        map.teleport(from, to, unit);
        showMenu = buildDropModeMenu(from, to, selected);
        map.teleport(to, from, unit);
      } else {
        // Teleport the unit to the selected tile
        map.teleport(from, selected, unit);
        initUnitActions(selected);
        map.teleport(selected, from, unit);
        showMenu = buildUnitActionMenu(selected);
      }
    }
    return showMenu;
  }

  private ShowPopupMenu buildDropModeMenu(Tile from, Tile to, Tile selected) {
    showMenu = new ShowPopupMenu("Unit actions", selected);
    unitsInTransport.clear();

    if (canWait(to)) {
      for (int dropCount = 0; dropCount < DROP_LIMIT; dropCount++) {
        if (dropCount < unit.getLocatableCount() && !context.isUnitDropped(unit.getLocatable(dropCount))) {
          Unit unitInTransport = (Unit) unit.getLocatable(dropCount);
          unitsInTransport.add(unitInTransport);
          if (canStartDrop(to, selected, dropCount + 1)) {
            CWAction dropAction = new StartDropAction(to);
            addToMenu(dropAction, "Drop " + unitInTransport.getName());
          }
        }
      }

      // In drop mode the wait Button acts as the drop Action
      if (context.isUnitDropMode()) {
        CWAction dropAction = ActionFactory.buildDropAction(unit, from, to, context.getDropCount(), context.getUnitsToBeDropped());
        showMenu.addAction(dropAction, "Wait");
      }
    }
    return showMenu;
  }

  /**
   * The unit is on the selected tile
   */
  private void initUnitActions(Tile selected) {
    canStartDrop = false;
    canCapture = false;
    canSupply = false;
    canStartAttack = false;
    canWait = false;
    canJoin = false;
    canLoad = false;
    Tile from = context.getClick(1);

    if (canWait(selected)) {
      canStartDrop = canStartDrop(selected, selected, 1);
      canCapture = canCapture(selected);
      canSupply = canSupply(selected);
      canStartAttack = canStartAttack(from, selected);
      canWait = canWait(selected);
    } else {
      // Actions where the active and selected unit are on the same tile.
      canJoin = canJoin(selected);
      canLoad = canLoad(selected);
    }
  }

  /**
   * Create the actions the unit can perform on the selected tile
   * The unit is on the from Tile
   */
  private ShowPopupMenu buildUnitActionMenu(Tile selected) {
    showMenu = new ShowPopupMenu("Unit actions", selected);
    Tile from = context.getClick(1);
    Tile to = context.getClick(2);

    if (canStartDrop) {
      map.teleport(from, to, unit);
      buildDropModeMenu(from, to, selected);
      map.teleport(to, from, unit);
    }

    if (canCapture) {
      CWAction captureAction = ActionFactory.buildCaptureAction(unit, (City) to.getTerrain());
      addToMenu(captureAction, "Capture");
    }

    if (canSupply) {
      CWAction supplyAction = ActionFactory.buildSupplyAction(unit, to);
      addToMenu(supplyAction, "Supply");
    }

    if (canStartAttack) {
      CWAction startAttackAction = new StartAttackAction(unit, to);
      addToMenu(startAttackAction, "Fire");
    }

    if (canWait) {
      CWAction waitAction = ActionFactory.buildWaitAction(unit, to);
      addToMenu(waitAction, "Wait");
    }

    if (canJoin) {
      Unit unitToJoin = (Unit) to.getLastLocatable();
      CWAction joinAction = ActionFactory.buildJoinAction(unit, unitToJoin);
      addToMenu(joinAction, "Join");
    }

    if (canLoad) {
      Unit transport = (Unit) to.getLastLocatable();
      CWAction loadAction = ActionFactory.buildLoadAction(unit, transport);
      addToMenu(loadAction, "Load");
    }
    return showMenu;
  }

  /**
   * Add a menu item to the menu backed by a CWAction
   *
   * @param action   The action to perform when clicked on the menu item
   * @param menuName The menu item name
   */
  private void addToMenu(CWAction action, String menuName) {
    showMenu.addAction(action, menuName);
  }
}
