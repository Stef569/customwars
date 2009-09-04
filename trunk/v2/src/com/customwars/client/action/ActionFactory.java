package com.customwars.client.action;

import com.customwars.client.action.city.LaunchRocketAction;
import com.customwars.client.action.game.EndTurnAction;
import com.customwars.client.action.unit.AddUnitToTileAction;
import com.customwars.client.action.unit.AttackAction;
import com.customwars.client.action.unit.CaptureAction;
import com.customwars.client.action.unit.ConstructCityAction;
import com.customwars.client.action.unit.DropAction;
import com.customwars.client.action.unit.FireFlareAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.action.unit.LoadAction;
import com.customwars.client.action.unit.MoveAnimatedAction;
import com.customwars.client.action.unit.SupplyAction;
import com.customwars.client.action.unit.TransformTerrainAction;
import com.customwars.client.action.unit.WaitAction;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.StateLogic;

import java.util.List;

/**
 * Create in game actions
 *
 * @author stefan
 */
public class ActionFactory {
  public static CWAction buildDropAction(Unit transport, Location from, Location moveTo, int dropCount, List<Unit> unitsToBeDropped) {
    ActionBag dropActions = new ActionBag("Drop Actions");
    dropActions.add(new InitAction());
    dropActions.add(new MoveAnimatedAction(transport, from, moveTo));
    dropActions.add(new WaitAction(transport));

    for (int drop = 0; drop < dropCount; drop++) {
      dropActions.add(new DropAction(transport));
      dropActions.add(new WaitAction(unitsToBeDropped.get(unitsToBeDropped.size() - 1 - drop)));
    }
    dropActions.add(new ClearInGameStateAction());
    return dropActions;
  }

  public static CWAction buildWaitAction(Unit unit, Location moveTo) {
    ActionBag waitActions = new ActionBag("Wait actions");
    waitActions.add(new InitAction());
    waitActions.add(new MoveAnimatedAction(unit.getLocation(), moveTo));
    waitActions.add(new WaitAction(unit));
    waitActions.add(new ClearInGameStateAction());
    return waitActions;
  }

  public static CWAction buildCaptureAction(Unit unit, City city) {
    ActionBag captureActions = new ActionBag("Capture actions");
    captureActions.add(new InitAction());
    captureActions.add(new MoveAnimatedAction(unit.getLocation(), city.getLocation()));
    captureActions.add(new CaptureAction(unit, city));
    captureActions.add(new WaitAction(unit));
    captureActions.add(new ClearInGameStateAction());
    return captureActions;
  }

  public static CWAction buildLoadAction(Unit unit, Unit transport) {
    ActionBag loadActions = new ActionBag("Load Actions");
    loadActions.add(new InitAction());
    loadActions.add(new MoveAnimatedAction(unit.getLocation(), transport.getLocation()));
    loadActions.add(new LoadAction(unit, transport));
    loadActions.add(new WaitAction(unit));
    loadActions.add(new ClearInGameStateAction());
    return loadActions;
  }

  public static CWAction buildSupplyAction(Unit supplier, Location moveTo) {
    ActionBag supplyActions = new ActionBag("Supply Actions");
    supplyActions.add(new InitAction());
    supplyActions.add(new MoveAnimatedAction(supplier.getLocation(), moveTo));
    supplyActions.add(new SupplyAction(supplier));
    supplyActions.add(new WaitAction(supplier));
    supplyActions.add(new ClearInGameStateAction());
    return supplyActions;
  }

  public static CWAction buildJoinAction(Unit unit, Unit target) {
    ActionBag joinActions = new ActionBag("Join Actions");
    joinActions.add(new InitAction());
    joinActions.add(new MoveAnimatedAction(unit.getLocation(), target.getLocation()));
    joinActions.add(new JoinAction(unit, target));
    joinActions.add(new WaitAction(target));
    joinActions.add(new ClearInGameStateAction());
    return joinActions;
  }

  public static CWAction buildAttackAction(Unit attacker, Unit defender, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack Actions");
    attackActions.add(new InitAction());
    attackActions.add((new MoveAnimatedAction(attacker.getLocation(), moveTo)));
    attackActions.add(new AttackAction(attacker, defender));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    return attackActions;
  }

  public static CWAction buildAddUnitToTileAction(Unit unit, Tile selected, boolean canUndo) {
    ActionBag addToTileAction = new ActionBag("Add unit to tile actions");
    addToTileAction.add(new InitAction());
    addToTileAction.add(new AddUnitToTileAction(unit, selected, canUndo));
    addToTileAction.add(new WaitAction(unit));
    addToTileAction.add(new ClearInGameStateAction());
    return addToTileAction;
  }

  public static CWAction buildEndTurnAction(StateLogic statelogic) {
    ActionBag endTurnAction = new ActionBag("End Turn actions");
    endTurnAction.add(new ClearInGameStateAction());
    endTurnAction.add(new EndTurnAction(statelogic));
    return endTurnAction;
  }

  public static CWAction buildLaunchRocketAction(Unit unit, City city, Tile to) {
    ActionBag launchRocketAction = new ActionBag("Launch Rocket actions");
    launchRocketAction.add(new InitAction());
    launchRocketAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    launchRocketAction.add(new WaitAction(unit));
    launchRocketAction.add(new LaunchRocketAction(city, unit));
    launchRocketAction.add(new ClearInGameStateAction());
    return launchRocketAction;
  }

  public static CWAction buildTransformTerrainAction(Unit unit, Tile to) {
    ActionBag transformTerrainAction = new ActionBag("Transform terrain actions");
    transformTerrainAction.add(new InitAction());
    transformTerrainAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainAction.add(new WaitAction(unit));
    transformTerrainAction.add(new TransformTerrainAction(unit, to));
    transformTerrainAction.add(new ClearInGameStateAction());
    return transformTerrainAction;
  }

  public static CWAction buildFireFlareAction(Unit unit, Tile to, Tile flareCenter) {
    ActionBag transformTerrainAction = new ActionBag("Fire Flare actions");
    transformTerrainAction.add(new InitAction());
    transformTerrainAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainAction.add(new WaitAction(unit));
    transformTerrainAction.add(new FireFlareAction(flareCenter));
    transformTerrainAction.add(new ClearInGameStateAction());
    return transformTerrainAction;
  }

  public static CWAction buildCityAction(Unit unit, City city, Tile to, Player cityOwner) {
    ActionBag buildCityAction = new ActionBag("Build City actions");
    buildCityAction.add(new InitAction());
    buildCityAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    buildCityAction.add(new WaitAction(unit));
    buildCityAction.add(new ConstructCityAction(unit, city, to, cityOwner));
    buildCityAction.add(new ClearInGameStateAction());
    return buildCityAction;
  }
}