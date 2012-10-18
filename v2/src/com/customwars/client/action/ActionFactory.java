package com.customwars.client.action;

import com.customwars.client.action.city.LaunchRocketAction;
import com.customwars.client.action.game.DeleteUnitAction;
import com.customwars.client.action.game.EndGameAction;
import com.customwars.client.action.game.EndTurnAction;
import com.customwars.client.action.unit.AddUnitToTileAction;
import com.customwars.client.action.unit.AttackCityAction;
import com.customwars.client.action.unit.AttackUnitAction;
import com.customwars.client.action.unit.COPowerAction;
import com.customwars.client.action.unit.COSuperPowerAction;
import com.customwars.client.action.unit.CaptureAction;
import com.customwars.client.action.unit.ConstructCityAction;
import com.customwars.client.action.unit.DiveAction;
import com.customwars.client.action.unit.DropAction;
import com.customwars.client.action.unit.FireFlareAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.action.unit.LoadAction;
import com.customwars.client.action.unit.LoadCOAction;
import com.customwars.client.action.unit.MoveAnimatedAction;
import com.customwars.client.action.unit.ProduceUnitAction;
import com.customwars.client.action.unit.SupplyAction;
import com.customwars.client.action.unit.SurfaceAction;
import com.customwars.client.action.unit.TransformTerrainAction;
import com.customwars.client.action.unit.WaitAction;
import com.customwars.client.model.drop.DropLocation;
import com.customwars.client.model.drop.DropLocationsQueue;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;

/**
 * Create in game actions
 */
public class ActionFactory {
  public static CWAction buildDropAction(Unit transport, Location from, Location moveTo, DropLocationsQueue dropQueue) {
    ActionBag dropActions = new ActionBag("Drop");
    dropActions.add(new InitAction());
    dropActions.add(new MoveAnimatedAction(transport, from, moveTo));
    dropActions.add(new WaitAction(transport));

    // Drop each unit in the drop queue to the user chosen drop location
    for (DropLocation dropLocation : dropQueue.getDropLocations()) {
      dropActions.add(new DropAction(transport, dropLocation));
      dropActions.add(new WaitAction(dropLocation.getUnit()));
    }
    dropActions.add(new ClearInGameStateAction());
    return dropActions;
  }

  public static CWAction buildMoveAction(Unit unit, Location moveTo) {
    ActionBag moveActions = new ActionBag("Move");
    moveActions.add(new InitAction());
    moveActions.add(new MoveAnimatedAction(unit.getLocation(), moveTo));
    moveActions.add(new WaitAction(unit));
    moveActions.add(new ClearInGameStateAction());
    return moveActions;
  }

  public static CWAction buildCaptureAction(Unit unit, City city) {
    ActionBag captureActions = new ActionBag("Capture");
    captureActions.add(new InitAction());
    captureActions.add(new MoveAnimatedAction(unit.getLocation(), city.getLocation()));
    captureActions.add(new CaptureAction(unit, city));
    captureActions.add(new WaitAction(unit));
    captureActions.add(new ClearInGameStateAction());
    return captureActions;
  }

  public static CWAction buildLoadAction(Unit unit, Unit transport) {
    ActionBag loadActions = new ActionBag("Load");
    loadActions.add(new InitAction());
    loadActions.add(new MoveAnimatedAction(unit.getLocation(), transport.getLocation()));
    loadActions.add(new LoadAction(unit, transport));
    loadActions.add(new WaitAction(unit));
    loadActions.add(new ClearInGameStateAction());
    return loadActions;
  }

  public static CWAction buildSupplyAction(Unit supplier, Location moveTo) {
    ActionBag supplyActions = new ActionBag("Supply");
    supplyActions.add(new InitAction());
    supplyActions.add(new MoveAnimatedAction(supplier.getLocation(), moveTo));
    supplyActions.add(new SupplyAction(supplier));
    supplyActions.add(new WaitAction(supplier));
    supplyActions.add(new ClearInGameStateAction());
    return supplyActions;
  }

  public static CWAction buildJoinAction(Unit unit, Unit target) {
    ActionBag joinActions = new ActionBag("Join");
    joinActions.add(new InitAction());
    joinActions.add(new MoveAnimatedAction(unit.getLocation(), target.getLocation()));
    joinActions.add(new JoinAction(unit, target));
    joinActions.add(new WaitAction(target));
    joinActions.add(new ClearInGameStateAction());
    return joinActions;
  }

  public static CWAction buildUnitVsUnitAttackAction(Unit attacker, Unit defender, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack_Unit");
    attackActions.add(new InitAction());
    attackActions.add(new MoveAnimatedAction(attacker.getLocation(), moveTo));
    attackActions.add(new AttackUnitAction(attacker, defender));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    return attackActions;
  }

  public static CWAction buildUnitVsCityAttackAction(Unit attacker, City city, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack_City");
    attackActions.add(new InitAction());
    attackActions.add(new MoveAnimatedAction(attacker.getLocation(), moveTo));
    attackActions.add(new AttackCityAction(attacker, city));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    return attackActions;
  }

  public static CWAction buildAddUnitToTileAction(Unit unit, Player newUnitOwner, Location location) {
    ActionBag buildUnitActions = new ActionBag("Build_Unit");
    buildUnitActions.add(new InitAction());
    buildUnitActions.add(new AddUnitToTileAction(unit, newUnitOwner, location));
    buildUnitActions.add(new WaitAction(unit));
    buildUnitActions.add(new ClearInGameStateAction());
    return buildUnitActions;
  }

  public static CWAction buildEndTurnAction() {
    ActionBag endTurnActions = new ActionBag("End_Turn");
    endTurnActions.add(new ClearInGameStateAction());
    endTurnActions.add(new EndTurnAction());
    return endTurnActions;
  }

  public static CWAction buildLaunchRocketAction(Unit unit, City city, Location rocketDestination) {
    ActionBag launchRocketActions = new ActionBag("Launch_Rocket");
    launchRocketActions.add(new InitAction());
    launchRocketActions.add(new MoveAnimatedAction(unit.getLocation(), city.getLocation()));
    launchRocketActions.add(new WaitAction(unit));
    launchRocketActions.add(new LaunchRocketAction(city, unit, rocketDestination));
    launchRocketActions.add(new ClearInGameStateAction());
    return launchRocketActions;
  }

  public static CWAction buildTransformTerrainAction(Unit unit, Location to, Terrain transformTo) {
    ActionBag transformTerrainActions = new ActionBag("Transform_terrain");
    transformTerrainActions.add(new InitAction());
    transformTerrainActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainActions.add(new WaitAction(unit));
    transformTerrainActions.add(new TransformTerrainAction(unit, to, transformTo));
    return transformTerrainActions;
  }

  public static CWAction buildFireFlareAction(Unit unit, Location to, Location flareCenter) {
    ActionBag flareActions = new ActionBag("Flare");
    flareActions.add(new InitAction());
    flareActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    flareActions.add(new WaitAction(unit));
    flareActions.add(new FireFlareAction(unit, flareCenter));
    flareActions.add(new ClearInGameStateAction());
    return flareActions;
  }

  public static CWAction buildConstructCityAction(Unit unit, String cityID, Location to) {
    ActionBag constructCityActions = new ActionBag("Build_City");
    constructCityActions.add(new InitAction());
    constructCityActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    constructCityActions.add(new WaitAction(unit));
    constructCityActions.add(new ConstructCityAction(unit, cityID, to));
    constructCityActions.add(new ClearInGameStateAction());
    return constructCityActions;
  }

  public static CWAction buildDiveAction(Unit unit, Location to) {
    ActionBag diveActions = new ActionBag("Dive");
    diveActions.add(new InitAction());
    diveActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    diveActions.add(new WaitAction(unit));
    diveActions.add(new DiveAction(unit));
    diveActions.add(new ClearInGameStateAction());
    return diveActions;
  }

  public static CWAction buildSurfaceAction(Unit unit, Location to) {
    ActionBag surfaceActions = new ActionBag("Surface");
    surfaceActions.add(new InitAction());
    surfaceActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    surfaceActions.add(new WaitAction(unit));
    surfaceActions.add(new SurfaceAction(unit));
    surfaceActions.add(new ClearInGameStateAction());
    return surfaceActions;
  }

  public static CWAction buildProduceUnitAction(Unit producer, String unitNameToProduce) {
    ActionBag produceUnitActions = new ActionBag("Produce");
    produceUnitActions.add(new InitAction());
    produceUnitActions.add(new ProduceUnitAction(producer, unitNameToProduce));
    produceUnitActions.add(new WaitAction(producer));
    produceUnitActions.add(new ClearInGameStateAction());
    return produceUnitActions;
  }

  public static CWAction buildEndGameAction() {
    return new EndGameAction();
  }

  public static CWAction buildLoadCOAction(Unit unit, Location to) {
    ActionBag loadCOActions = new ActionBag("load_CO");
    loadCOActions.add(new InitAction());
    loadCOActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    loadCOActions.add(new LoadCOAction(unit));
    loadCOActions.add(new ClearInGameStateAction());
    return loadCOActions;
  }

  public static CWAction buildCOPowerAction(Unit unit, Location to) {
    ActionBag coPowerActions = new ActionBag("co_Power");
    coPowerActions.add(new InitAction());
    coPowerActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    coPowerActions.add(new COPowerAction());
    coPowerActions.add(new WaitAction(unit));
    coPowerActions.add(new ClearInGameStateAction());
    return coPowerActions;
  }

  public static CWAction buildCOSuperPowerAction(Unit unit, Location to) {
    ActionBag coSuperPowerActions = new ActionBag("co_Super_Power");
    coSuperPowerActions.add(new InitAction());
    coSuperPowerActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    coSuperPowerActions.add(new COSuperPowerAction());
    coSuperPowerActions.add(new WaitAction(unit));
    coSuperPowerActions.add(new ClearInGameStateAction());
    return coSuperPowerActions;
  }

  public static CWAction buildDeleteUnitAction(Location selected) {
    ActionBag deleteUnitActions = new ActionBag("delete_unit");
    deleteUnitActions.add(new DeleteUnitAction(selected));
    return deleteUnitActions;
  }
}
