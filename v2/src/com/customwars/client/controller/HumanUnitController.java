package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.action.city.StartLaunchRocketAction;
import com.customwars.client.action.unit.SelectAction;
import com.customwars.client.action.unit.ShowAttackZoneAction;
import com.customwars.client.action.unit.StartAttackAction;
import com.customwars.client.action.unit.StartDropAction;
import com.customwars.client.action.unit.StartFlareAction;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Allows a human to control a unit
 * by using a gui
 *
 * @author stefan
 */
public class HumanUnitController extends UnitController {
  private static final Logger logger = Logger.getLogger(HumanUnitController.class);
  private static final int DROP_LIMIT = 4;
  private InGameContext context;
  private MapRenderer mapRenderer;
  private List<Unit> unitsInTransport;
  private ShowPopupMenuAction showMenuAction;
  private boolean canStartDropGroundUnit, canTakeOff, canCapture, canSupply, canStartAttack, canWait, canJoin, canLoad;
  private boolean canLaunchRocketFromCity, canTransformTerrain;
  private boolean canFireFlare;
  private boolean canBuildCity, canBuildUnit;
  private boolean canDive, canSurface;

  public HumanUnitController(Unit unit, InGameContext gameContext) {
    super(unit, gameContext);
    this.context = gameContext;
    this.mapRenderer = gameContext.getMapRenderer();
    unitsInTransport = new LinkedList<Unit>();
  }

  public void handleAPress() {
    Tile selected = mapRenderer.getCursorLocation();
    Tile to = context.getClick(2);

    if (context.isUnitDropMode() && canDrop(selected)) {
      addDropLocation(selected);
      initUnitActionMenu(selected);
    } else if (context.isUnitAttackMode() && canAttack(selected)) {
      Unit defender = (Unit) selected.getLastLocatable();
      CWAction attackAction = ActionFactory.buildAttackAction(unit, defender, to);
      context.doAction(attackAction);
    } else if (context.isRocketLaunchMode()) {
      City city = map.getCityOn(context.getClick(2));
      CWAction launchRocket = ActionFactory.buildLaunchRocketAction(unit, city, to);
      context.doAction(launchRocket);
    } else if (context.isUnitFlareMode() && unit.getAttackZone().contains(selected)) {
      CWAction fireFlare = ActionFactory.buildFireFlareAction(unit, to, selected);
      context.doAction(fireFlare);
    } else if (unit.getMoveZone().contains(selected)) {
      if (canShowMenu()) {
        context.setClick(2, selected);
        initUnitActionMenu(selected);
      } else if (canSelect(selected)) {
        logger.debug("Selecting " + unit);
        context.clearClicks();
        context.discartAllEdits();
        context.setClick(1, selected);
        context.doAction(new SelectAction(selected));
      }
    } else {
      SFX.playSound("cancel");
      context.undo();
    }
  }

  private void addDropLocation(Tile location) {
    // The menu option clicked on is the index of the unit in the transport
    int popupOption = showMenuAction.getCurrentOption();
    context.addDropLocation(location, unitsInTransport.get(popupOption));
  }

  public void handleBPress() {
    Tile cursorLocation = mapRenderer.getCursorLocation();
    Unit selectedUnit = map.getUnitOn(cursorLocation);
    Player activePlayer = game.getActivePlayer();

    if (isUnitVisibleTo(activePlayer) && isUnitOn(cursorLocation) && selectedUnit.canFire()) {
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

    if (showMenuAction.atLeastHasOneItem()) {
      context.doAction(showMenuAction);
    }
  }

  private ShowPopupMenuAction buildUnitActionMenu(Tile from, Tile selected) {
    ShowPopupMenuAction showMenuAction = null;
    if (isActiveUnit() && canMove(from, selected)) {
      if (context.isUnitDropMode()) {
        // In drop mode teleport the transporter to the 2ND selected tile
        Tile to = context.getClick(2);
        map.teleport(from, to, unit);
        showMenuAction = buildDropModeMenu(from, to, selected);
        map.teleport(to, from, unit);
      } else {
        // Temporarily teleport the active unit to the selected tile
        // to determine what actions are available
        map.teleport(from, selected, unit);
        initUnitActions(selected);
        map.teleport(selected, from, unit);
        showMenuAction = buildUnitActionMenu(selected);
      }
    }
    return showMenuAction;
  }

  private ShowPopupMenuAction buildDropModeMenu(Tile from, Tile to, Tile selected) {
    showMenuAction = new ShowPopupMenuAction("Unit drop menu", selected);
    unitsInTransport.clear();

    if (canWait(to)) {
      for (int dropCount = 0; dropCount < DROP_LIMIT; dropCount++) {
        if (dropCount < unit.getLocatableCount() && !context.isUnitDropped(unit.getLocatable(dropCount))) {
          Unit unitInTransport = (Unit) unit.getLocatable(dropCount);
          unitsInTransport.add(unitInTransport);
          if (canStartDrop(to, dropCount + 1)) {
            CWAction dropAction = new StartDropAction(to, unit);
            addToMenu(dropAction, App.translate("drop") + " " + unitInTransport.getStats().getName());
          }
        }
      }

      // In drop mode the wait Button acts as the drop Action
      if (context.isUnitDropMode()) {
        CWAction dropAction = ActionFactory.buildDropAction(unit, from, to, context.getDropCount(), context.getUnitsToBeDropped());
        MenuItem waitItem = new MenuItem(App.translate("wait"), context.getContainer());
        showMenuAction.addAction(dropAction, waitItem);
      }
    }
    return showMenuAction;
  }

  /**
   * The unit is on the selected tile
   */
  private void initUnitActions(Tile selected) {
    canStartDropGroundUnit = false;
    canTakeOff = false;
    canCapture = false;
    canSupply = false;
    canStartAttack = false;
    canWait = false;
    canJoin = false;
    canLoad = false;
    canTransformTerrain = false;
    canFireFlare = false;
    canBuildCity = false;
    canBuildUnit = false;
    canDive = false;
    canSurface = false;
    Tile from = context.getClick(1);

    if (canWait(selected)) {
      canTakeOff = canAirplaneTakeOffFromUnit();
      canStartDropGroundUnit = canStartDrop(selected, 1);
      canCapture = canCapture(selected);
      canSupply = canSupply(selected);
      canStartAttack = canStartAttack(from, selected);
      canLaunchRocketFromCity = canLaunchRocket(selected);
      canWait = canWait(selected);
      canTransformTerrain = canTransformTerrain(selected);
      canFireFlare = canFireFlare(from);
      canBuildCity = canBuildCity(selected);
      canBuildUnit = canBuildUnit();
      canDive = canDive();
      canSurface = canSurface();
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
  private ShowPopupMenuAction buildUnitActionMenu(Tile selected) {
    showMenuAction = new ShowPopupMenuAction("Unit context menu", selected);
    Tile from = context.getClick(1);
    Tile to = context.getClick(2);

    if (canStartDropGroundUnit) {
      map.teleport(from, to, unit);
      buildDropModeMenu(from, to, selected);
      map.teleport(to, from, unit);
    } else if (canTakeOff) {
      Unit unitToTakeOff = (Unit) unit.getLastLocatable();
      CWAction buildUnitAction = ActionFactory.buildTakeOffUnitAction(unit, unitToTakeOff);
      addToMenu(buildUnitAction, App.translate("launch") + " - " + unitToTakeOff.getStats().getName());
    }

    if (canBuildUnit) {
      for (int unitID : unit.getStats().getUnitsThatCanBeBuild()) {
        Unit unitThatCanBeBuild = UnitFactory.getUnit(unitID);
        CWAction buildUnitAction = ActionFactory.buildProduceUnitAction(unit, unitThatCanBeBuild, to);
        addToMenu(buildUnitAction, App.translate("build") + " - " + unitThatCanBeBuild.getStats().getName());
      }
    }

    if (canCapture) {
      CWAction captureAction = ActionFactory.buildCaptureAction(unit, (City) to.getTerrain());
      addToMenu(captureAction, App.translate("capture"));
    }

    if (canSupply) {
      CWAction supplyAction = ActionFactory.buildSupplyAction(unit, to);
      addToMenu(supplyAction, App.translate("supply"));
    }

    if (canStartAttack) {
      CWAction startAttackAction = new StartAttackAction(unit, to);
      addToMenu(startAttackAction, App.translate("fire"));
    }

    if (canJoin) {
      Unit unitToJoin = (Unit) to.getLastLocatable();
      CWAction joinAction = ActionFactory.buildJoinAction(unit, unitToJoin);
      addToMenu(joinAction, App.translate("join"));
    }

    if (canLoad) {
      Unit transport = (Unit) to.getLocatable(0);
      CWAction loadAction = ActionFactory.buildLoadAction(unit, transport);
      addToMenu(loadAction, App.translate("load"));
    }

    if (canLaunchRocketFromCity) {
      CWAction startLaunchAction = new StartLaunchRocketAction();
      addToMenu(startLaunchAction, App.translate("launch"));
    }

    if (canTransformTerrain) {
      CWAction transformAction = ActionFactory.buildTransformTerrainAction(unit, to);
      addToMenu(transformAction, App.translate("build") + " " + getTransformToTerrain(to).getName());
    }

    if (canFireFlare) {
      CWAction fireFlareAction = new StartFlareAction();
      addToMenu(fireFlareAction, App.translate("flare"));
    }

    if (canBuildCity) {
      City city = getCityThatCanBeBuildOn(to);
      CWAction buildCityAction = ActionFactory.buildCityAction(unit, city, to, unit.getOwner());
      addToMenu(buildCityAction, App.translate("build") + " " + city.getName());
    }

    if (canDive) {
      CWAction diveAction = ActionFactory.buildDiveAction(unit, to);
      addToMenu(diveAction, App.translate("dive"));
    }

    if (canSurface) {
      CWAction surfaceAction = ActionFactory.buildSurfaceAction(unit, to);
      addToMenu(surfaceAction, App.translate("surface"));
    }

    if (canWait) {
      CWAction waitAction = ActionFactory.buildWaitAction(unit, to);
      addToMenu(waitAction, App.translate("wait"));
    }
    return showMenuAction;
  }

  private Terrain getTransformToTerrain(Tile tile) {
    int tranformID = unit.getStats().getTransformTerrainFor(tile.getTerrain());
    return TerrainFactory.getTerrain(tranformID);
  }

  private City getCityThatCanBeBuildOn(Tile tile) {
    int cityID = unit.getStats().getCityToBuildOnTerrain(tile.getTerrain());
    return CityFactory.getCity(cityID);
  }

  /**
   * Add a menu item to the menu backed by a CWAction
   *
   * @param action       The action to perform when clicked on the menu item
   * @param menuItemName The menu item name
   */
  private void addToMenu(CWAction action, String menuItemName) {
    MenuItem menuItem = new MenuItem(menuItemName, context.getContainer());
    showMenuAction.addAction(action, menuItem);
  }
}
