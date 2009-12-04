package com.customwars.client.action;

import com.customwars.client.action.city.LaunchRocketAction;
import com.customwars.client.action.game.EndTurnAction;
import com.customwars.client.action.unit.AddUnitToTileAction;
import com.customwars.client.action.unit.AttackAction;
import com.customwars.client.action.unit.CaptureAction;
import com.customwars.client.action.unit.ConstructCityAction;
import com.customwars.client.action.unit.DiveAction;
import com.customwars.client.action.unit.DropAction;
import com.customwars.client.action.unit.FireFlareAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.action.unit.LoadAction;
import com.customwars.client.action.unit.MoveAnimatedAction;
import com.customwars.client.action.unit.ProduceUnitAction;
import com.customwars.client.action.unit.SelectAction;
import com.customwars.client.action.unit.SupplyAction;
import com.customwars.client.action.unit.SurfaceAction;
import com.customwars.client.action.unit.TakeOffAction;
import com.customwars.client.action.unit.TransformTerrainAction;
import com.customwars.client.action.unit.WaitAction;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.StateChanger;

import java.util.Collection;

/**
 * Create in game actions
 *
 * @author stefan
 */
public class ActionFactory {
  public static CWAction buildDropAction(Unit transport, Location from, Location moveTo, Collection<Unit> unitsToBeDropped) {
    ActionBag dropActions = new ActionBag("Drop");
    dropActions.add(new InitAction());
    dropActions.add(new MoveAnimatedAction(transport, from, moveTo));
    dropActions.add(new WaitAction(transport));

    for (Unit unit : unitsToBeDropped) {
      dropActions.add(new DropAction(transport));
      dropActions.add(new WaitAction(unit));
    }
    dropActions.add(new ClearInGameStateAction());
    return dropActions;
  }

  public static CWAction buildWaitAction(Unit unit, Location moveTo) {
    ActionBag waitActions = new ActionBag("Wait");
    waitActions.add(new InitAction());
    waitActions.add(new MoveAnimatedAction(unit.getLocation(), moveTo));
    waitActions.add(new WaitAction(unit));
    waitActions.add(new ClearInGameStateAction());
    return waitActions;
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

  public static CWAction buildAttackAction(Unit attacker, Unit defender, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack");
    attackActions.add(new InitAction());
    attackActions.add((new MoveAnimatedAction(attacker.getLocation(), moveTo)));
    attackActions.add(new AttackAction(attacker, defender));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    return attackActions;
  }

  public static CWAction buildAddUnitToTileAction(Unit unit, Tile selected, boolean canUndo) {
    ActionBag addToTileAction = new ActionBag("Add unit to tile");
    addToTileAction.add(new InitAction());
    addToTileAction.add(new AddUnitToTileAction(unit, selected, canUndo));
    addToTileAction.add(new WaitAction(unit));
    addToTileAction.add(new ClearInGameStateAction());
    return addToTileAction;
  }

  public static CWAction buildEndTurnAction(StateChanger stateChanger) {
    ActionBag endTurnAction = new ActionBag("End Turn");
    endTurnAction.add(new ClearInGameStateAction());
    endTurnAction.add(new EndTurnAction(stateChanger));
    return endTurnAction;
  }

  public static CWAction buildLaunchRocketAction(Unit unit, City city, Tile to) {
    ActionBag launchRocketAction = new ActionBag("Launch Rocket");
    launchRocketAction.add(new InitAction());
    launchRocketAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    launchRocketAction.add(new WaitAction(unit));
    launchRocketAction.add(new LaunchRocketAction(city, unit));
    launchRocketAction.add(new ClearInGameStateAction());
    return launchRocketAction;
  }

  public static CWAction buildTransformTerrainAction(Unit unit, Tile to) {
    ActionBag transformTerrainAction = new ActionBag("Transform terrain");
    transformTerrainAction.add(new InitAction());
    transformTerrainAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainAction.add(new WaitAction(unit));
    transformTerrainAction.add(new TransformTerrainAction(unit, to));
    transformTerrainAction.add(new ClearInGameStateAction());
    return transformTerrainAction;
  }

  public static CWAction buildFireFlareAction(Unit unit, Tile to, Tile flareCenter) {
    ActionBag transformTerrainAction = new ActionBag("Fire Flare");
    transformTerrainAction.add(new InitAction());
    transformTerrainAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainAction.add(new WaitAction(unit));
    transformTerrainAction.add(new FireFlareAction(flareCenter));
    transformTerrainAction.add(new ClearInGameStateAction());
    return transformTerrainAction;
  }

  public static CWAction buildCityAction(Unit unit, City city, Tile to, Player cityOwner) {
    ActionBag buildCityAction = new ActionBag("Build City");
    buildCityAction.add(new InitAction());
    buildCityAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    buildCityAction.add(new WaitAction(unit));
    buildCityAction.add(new ConstructCityAction(unit, city, to, cityOwner));
    buildCityAction.add(new ClearInGameStateAction());
    return buildCityAction;
  }

  public static CWAction buildDiveAction(Unit unit, Location to) {
    ActionBag diveAction = new ActionBag("Dive");
    diveAction.add(new InitAction());
    diveAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    diveAction.add(new WaitAction(unit));
    diveAction.add(new DiveAction(unit));
    diveAction.add(new ClearInGameStateAction());
    return diveAction;
  }

  public static CWAction buildSurfaceAction(Unit unit, Location to) {
    ActionBag diveAction = new ActionBag("Surface");
    diveAction.add(new InitAction());
    diveAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    diveAction.add(new WaitAction(unit));
    diveAction.add(new SurfaceAction(unit));
    diveAction.add(new ClearInGameStateAction());
    return diveAction;
  }

  public static CWAction buildProduceUnitAction(Unit producer, Unit unitToBuild, Tile to) {
    ActionBag buildUnitAction = new ActionBag("Produce");
    buildUnitAction.add(new InitAction());
    buildUnitAction.add(new MoveAnimatedAction(producer.getLocation(), to));
    buildUnitAction.add(new ProduceUnitAction(producer, unitToBuild));
    buildUnitAction.add(new WaitAction(producer));
    buildUnitAction.add(new ClearInGameStateAction());
    return buildUnitAction;
  }

  public static CWAction buildTakeOffUnitAction(Unit launcher, Unit unitToLaunch) {
    ActionBag buildUnitAction = new ActionBag("Take off");
    buildUnitAction.add(new InitAction());
    buildUnitAction.add(new TakeOffAction(launcher, unitToLaunch));
    buildUnitAction.add(new WaitAction(launcher));
    buildUnitAction.add(new ClearInGameStateAction());
    buildUnitAction.add(new SelectAction(launcher.getLocation()));
    return buildUnitAction;
  }
}