package com.customwars.client.controller;

import com.customwars.client.SFX;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.action.unit.SelectAction;
import com.customwars.client.action.unit.ShowAttackZoneAction;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Allows a human to control a unit
 * by using a gui
 *
 * @author stefan
 */
public class HumanUnitController extends UnitController {
  private static final Logger logger = Logger.getLogger(HumanUnitController.class);
  private final InGameContext inGameContext;
  private final MapRenderer mapRenderer;
  private ShowPopupMenuAction menu;

  public HumanUnitController(Unit unit, InGameContext gameContext) {
    super(unit, gameContext);
    this.inGameContext = gameContext;
    this.mapRenderer = gameContext.getMapRenderer();
  }

  public void handleAPress() {
    Tile selected = mapRenderer.getCursorLocation();
    Tile to = inGameContext.getClick(2);

    if (inGameContext.isUnitDropMode()) {
      dropUnit(selected);
    } else if (inGameContext.isUnitAttackMode()) {
      attack(selected, to);
    } else if (inGameContext.isRocketLaunchMode()) {
      launchRocket(selected, to);
    } else if (inGameContext.isUnitFlareMode() && unit.getAttackZone().contains(selected)) {
      fireFlare(selected, to);
    } else if (unit.getMoveZone().contains(selected)) {
      clickInMoveZone(selected);
    } else {
      cancelPressed();
    }
  }

  private void dropUnit(Tile selected) {
    // The menu option clicked on is the index of the unit in the transport List
    int unitInTransportIndex = menu.getCurrentOption();

    if (canDrop(selected, unitInTransportIndex)) {
      Unit unitInTransport = inGameContext.getUnitInTransport(unitInTransportIndex);
      inGameContext.addDropLocation(selected, unitInTransport);
      this.menu = new UnitMenuBuilder(this, unit, inGameContext, selected).getMenu();
      showMenu(menu);
    } else {
      logger.warn("Trying to drop unit on " + selected + " failed");
    }
  }

  private void attack(Tile selected, Location to) {
    if (canAttackUnit(selected)) {
      attackUnit(selected, to);
    } else if (canAttackCity(selected)) {
      attackCity(selected, to);
    }
  }

  private void attackUnit(Location selected, Location to) {
    Unit defender = map.getUnitOn(selected);
    CWAction attackAction = ActionFactory.buildUnitAttackAction(unit, defender, to);
    inGameContext.doAction(attackAction);
  }

  private void attackCity(Location selected, Location to) {
    City city = map.getCityOn(selected);
    CWAction attackAction = ActionFactory.buildUnitVsCityAttackAction(unit, city, to);
    inGameContext.doAction(attackAction);
  }

  private void launchRocket(Location selected, Location to) {
    City city = map.getCityOn(inGameContext.getClick(2));
    CWAction launchRocket = ActionFactory.buildLaunchRocketAction(unit, city, to, selected);
    inGameContext.doAction(launchRocket);
  }

  private void fireFlare(Location selected, Location to) {
    CWAction fireFlare = ActionFactory.buildFireFlareAction(unit, to, selected);
    inGameContext.doAction(fireFlare);
  }

  private void clickInMoveZone(Tile selected) {
    if (canShowMenu()) {
      showMenu(selected);
    } else if (canSelect(selected)) {
      select(selected);
    } else {
      throw new AssertionError("A click has been made in the move zone. " +
        "Either a click has been made on a unit -> select " +
        "or next to the unit -> show menu");
    }
  }

  private boolean canShowMenu() {
    Unit activeUnit = game.getActiveUnit();
    Location selected = mapRenderer.getCursorLocation();
    return activeUnit != null && activeUnit.isWithinMoveZone(selected);
  }

  private void showMenu(Tile selected) {
    inGameContext.registerClick(2, selected);
    this.menu = new UnitMenuBuilder(this, unit, inGameContext, selected).getMenu();
    showMenu(menu);
  }

  private void showMenu(ShowPopupMenuAction showMenuAction) {
    if (showMenuAction.atLeastHasOneItem()) {
      inGameContext.doAction(showMenuAction);
    } else {
      logger.warn("No menu items to show");
    }
  }

  private void select(Tile selected) {
    inGameContext.clearClickHistory();
    inGameContext.clearUndoHistory();
    inGameContext.registerClick(1, selected);
    inGameContext.doAction(new SelectAction(selected));
  }

  private void cancelPressed() {
    SFX.playSound("cancel");
    inGameContext.undo();
  }

  public void handleBPress() {
    Tile cursorLocation = mapRenderer.getCursorLocation();
    Unit selectedUnit = map.getUnitOn(cursorLocation);
    Player activePlayer = game.getActivePlayer();

    if (isUnitVisibleTo(activePlayer) && isUnitOn(cursorLocation) && selectedUnit.canFire()) {
      showAttackZone(selectedUnit);
    }
  }

  private void showAttackZone(Unit selectedUnit) {
    inGameContext.doAction(new ShowAttackZoneAction(selectedUnit));
  }
}
