package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ShowPopupMenuAction;
import com.customwars.client.action.city.StartLaunchRocketAction;
import com.customwars.client.action.unit.StartAttackAction;
import com.customwars.client.action.unit.StartDropAction;
import com.customwars.client.action.unit.StartFlareAction;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.state.InGameContext;

/**
 * Build a menu for a given unit once and allow to retrieve that menu see getMenu()
 * 2 menu's can be build:
 * When in drop mode a drop menu is build else a context menu is build
 */
public class UnitMenuBuilder {
  private static final int DROP_LIMIT = 4;
  private boolean canStartDropGroundUnit, canTakeOff, canCapture, canSupply, canStartAttack, canWait, canJoin, canLoad;
  private boolean canLaunchRocketFromCity, canTransformTerrain;
  private boolean canFireFlare;
  private boolean canBuildCity, canBuildUnit;
  private boolean canDive, canSurface;
  private final InGameContext inGameContext;
  private final Unit unit;
  private final HumanUnitController controller;
  private final Map<Tile> map;
  private ShowPopupMenuAction menu;

  public UnitMenuBuilder(HumanUnitController controller, Unit unit, InGameContext inGamecontext, Tile selected) {
    this.controller = controller;
    this.unit = unit;
    this.inGameContext = inGamecontext;
    this.map = inGamecontext.getGame().getMap();
    Tile from = (Tile) unit.getLocation();
    buildUnitActionMenu(from, selected);
  }

  private void buildUnitActionMenu(Tile from, Tile selected) {
    if (controller.isActiveUnit() && controller.canMove(from, selected)) {
      if (inGameContext.isUnitDropMode()) {
        buildDropMenu(from, selected);
      } else {
        buildUnitContextMenu(from, selected);
      }
    }
  }

  private void buildDropMenu(Tile from, Tile selected) {
    // Temporarily teleport the active unit(transport) to the 2ND tile
    // to determine what actions are available
    Tile to = inGameContext.getClick(2);
    map.teleport(from, to, unit);
    menu = buildDropMenu(from, to, selected);
    map.teleport(to, from, unit);
  }

  private ShowPopupMenuAction buildDropMenu(Tile from, Tile to, Tile selected) {
    menu = new ShowPopupMenuAction("Unit drop menu", selected);
    inGameContext.clearUnitsInTransport();

    if (controller.canWait(to)) {
      for (int dropIndex = 0; dropIndex < DROP_LIMIT && dropIndex < unit.getLocatableCount(); dropIndex++) {
        Unit unitInTransport = (Unit) unit.getLocatable(dropIndex);
        boolean unitIsAlreadyDropped = inGameContext.isUnitDropped(unitInTransport);

        // Build start drop actions for each unit in the transport
        // If there is at least 1 free tile(canStartDrop) and the unit has not already been dropped
        if (controller.canStartDrop() && !unitIsAlreadyDropped) {
          inGameContext.addUnitInTransport(unitInTransport);
          CWAction dropAction = new StartDropAction(to, unit, unitInTransport);
          addToMenu(dropAction, App.translate("drop") + ' ' + App.translate(unitInTransport.getStats().getName()));
        }
      }

      // In drop mode the wait Button acts as the drop Action
      if (inGameContext.isUnitDropMode()) {
        CWAction dropAction = ActionFactory.buildDropAction(unit, from, to, inGameContext.getUnitsToBeDropped());
        addToMenu(dropAction, App.translate("wait"));
      }
    }
    return menu;
  }

  private void buildUnitContextMenu(Tile from, Tile selected) {
    // Temporarily teleport the active unit to the selected tile
    // to determine what actions are available
    map.teleport(from, selected, unit);
    initUnitActions(selected);
    map.teleport(selected, from, unit);
    menu = buildUnitContextMenu(selected);
  }

  /**
   * Determine the actions this unit can perform on the selected tile
   * The unit is on the selected tile
   */
  private void initUnitActions(Tile selected) {
    Tile from = inGameContext.getClick(1);

    if (controller.canWait(selected)) {
      canTakeOff = controller.canAirplaneTakeOffFromUnit();
      canStartDropGroundUnit = controller.canStartDrop();
      canCapture = controller.canCapture(selected);
      canSupply = controller.canSupply(selected);
      canStartAttack = controller.canStartAttack(from);
      canLaunchRocketFromCity = controller.canLaunchRocket(selected);
      canWait = controller.canWait(selected);
      canTransformTerrain = controller.canTransformTerrain(selected);
      canFireFlare = controller.canFireFlare(from);
      canBuildCity = controller.canBuildCity(selected);
      canBuildUnit = controller.canBuildUnit();
      canDive = controller.canDive();
      canSurface = controller.canSurface();
    } else {
      // Actions where the active and selected unit are on the same tile.
      canJoin = controller.canJoin(selected);
      canLoad = controller.canLoad(selected);
    }
  }

  /**
   * Create the actions the unit can perform on the selected tile
   * The unit is on the from Tile
   */
  private ShowPopupMenuAction buildUnitContextMenu(Tile selected) {
    menu = new ShowPopupMenuAction("Unit context menu", selected);
    Tile from = inGameContext.getClick(1);
    Tile to = inGameContext.getClick(2);

    if (canStartDropGroundUnit) {
      map.teleport(from, to, unit);
      buildDropMenu(from, to, selected);
      map.teleport(to, from, unit);
    } else if (canTakeOff) {
      Unit unitToTakeOff = (Unit) unit.getLastLocatable();
      CWAction buildUnitAction = ActionFactory.buildTakeOffUnitAction(unit, unitToTakeOff);
      String menuText = App.translate("launch") + " - " + App.translate(unitToTakeOff.getStats().getName());
      addToMenu(buildUnitAction, menuText);
    }

    if (canBuildUnit) {
      for (int unitID : unit.getStats().getUnitsThatCanBeBuild()) {
        Unit unitThatCanBeBuild = UnitFactory.getUnit(unitID);
        CWAction buildUnitAction = ActionFactory.buildProduceUnitAction(unit, unitThatCanBeBuild, to);
        String menuText = App.translate("build") + " - " + App.translate(unitThatCanBeBuild.getStats().getName());
        addToMenu(buildUnitAction, menuText);
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
      addToMenu(transformAction, App.translate("build") + ' ' + getTransformToTerrain(to).getName());
    }

    if (canFireFlare) {
      CWAction fireFlareAction = new StartFlareAction();
      addToMenu(fireFlareAction, App.translate("flare"));
    }

    if (canBuildCity) {
      City city = getCityThatCanBeBuildOn(to);
      CWAction buildCityAction = ActionFactory.buildCityAction(unit, city, to, unit.getOwner());
      addToMenu(buildCityAction, App.translate("build") + ' ' + city.getName());
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
    return menu;
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
   * @param menuItemName The name of the menu item, as shown in the gui
   */
  private void addToMenu(CWAction action, String menuItemName) {
    MenuItem menuItem = new MenuItem(menuItemName, inGameContext.getContainer());
    menu.addAction(action, menuItem);
  }

  public ShowPopupMenuAction getMenu() {
    return menu;
  }
}
