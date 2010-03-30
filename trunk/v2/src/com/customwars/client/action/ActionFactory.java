package com.customwars.client.action;

import com.customwars.client.action.city.LaunchRocketAction;
import com.customwars.client.action.game.EndGameAction;
import com.customwars.client.action.game.EndTurnAction;
import com.customwars.client.action.unit.AddUnitToTileAction;
import com.customwars.client.action.unit.AttackCityAction;
import com.customwars.client.action.unit.AttackUnitAction;
import com.customwars.client.action.unit.CaptureAction;
import com.customwars.client.action.unit.ConstructCityAction;
import com.customwars.client.action.unit.DiveAction;
import com.customwars.client.action.unit.DropAction;
import com.customwars.client.action.unit.FireFlareAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.action.unit.LoadAction;
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
import com.customwars.client.model.map.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create in game actions
 *
 * @author stefan
 */
public class ActionFactory {
  public static CWAction buildDropAction(Unit transport, Location from, Location moveTo, DropLocationsQueue dropQueue) {
    ActionBag dropActions = new ActionBag("Drop");
    dropActions.add(new InitAction());
    dropActions.add(new MoveAnimatedAction(transport, from, moveTo));
    dropActions.add(new WaitAction(transport));

    // Drop each unit in the drop queue to the user chosen drop location
    List<Integer> dropAction = new ArrayList<Integer>();
    for (DropLocation dropLocation : dropQueue.getDropLocations()) {
      dropActions.add(new DropAction(transport, dropLocation));
      dropActions.add(new WaitAction(dropLocation.getUnit()));
      buildDropActionText(dropAction, transport, dropLocation);
    }
    dropActions.add(new ClearInGameStateAction());
    dropActions.setActionText(from, moveTo, dropAction.toArray(new Integer[]{dropAction.size()}));
    return dropActions;
  }

  private static void buildDropActionText(Collection<Integer> actionTextParam, Unit transport, DropLocation dropLocation) {
    Unit unitToDrop = dropLocation.getUnit();
    int unitInTransportIndex = transport.indexOf(unitToDrop);
    actionTextParam.add(unitInTransportIndex);
    actionTextParam.add(dropLocation.getLocation().getCol());
    actionTextParam.add(dropLocation.getLocation().getRow());
  }

  public static CWAction buildMoveAction(Unit unit, Location moveTo) {
    ActionBag moveActions = new ActionBag("Move");
    moveActions.add(new InitAction());
    moveActions.add(new MoveAnimatedAction(unit.getLocation(), moveTo));
    moveActions.add(new WaitAction(unit));
    moveActions.add(new ClearInGameStateAction());
    moveActions.setActionText(unit, moveTo);
    return moveActions;
  }

  public static CWAction buildCaptureAction(Unit unit, City city) {
    ActionBag captureActions = new ActionBag("Capture");
    captureActions.add(new InitAction());
    captureActions.add(new MoveAnimatedAction(unit.getLocation(), city.getLocation()));
    captureActions.add(new CaptureAction(unit, city));
    captureActions.add(new WaitAction(unit));
    captureActions.add(new ClearInGameStateAction());
    captureActions.setActionText(unit, city.getLocation());
    return captureActions;
  }

  public static CWAction buildLoadAction(Unit unit, Unit transport) {
    ActionBag loadActions = new ActionBag("Load");
    loadActions.add(new InitAction());
    loadActions.add(new MoveAnimatedAction(unit.getLocation(), transport.getLocation()));
    loadActions.add(new LoadAction(unit, transport));
    loadActions.add(new WaitAction(unit));
    loadActions.add(new ClearInGameStateAction());
    loadActions.setActionText(unit, transport);
    return loadActions;
  }

  public static CWAction buildSupplyAction(Unit supplier, Location moveTo) {
    ActionBag supplyActions = new ActionBag("Supply");
    supplyActions.add(new InitAction());
    supplyActions.add(new MoveAnimatedAction(supplier.getLocation(), moveTo));
    supplyActions.add(new SupplyAction(supplier));
    supplyActions.add(new WaitAction(supplier));
    supplyActions.add(new ClearInGameStateAction());
    supplyActions.setActionText(supplier, moveTo);
    return supplyActions;
  }

  public static CWAction buildJoinAction(Unit unit, Unit target) {
    ActionBag joinActions = new ActionBag("Join");
    joinActions.add(new InitAction());
    joinActions.add(new MoveAnimatedAction(unit.getLocation(), target.getLocation()));
    joinActions.add(new JoinAction(unit, target));
    joinActions.add(new WaitAction(target));
    joinActions.add(new ClearInGameStateAction());
    joinActions.setActionText(unit, target);
    return joinActions;
  }

  public static CWAction buildUnitVsUnitAttackAction(Unit attacker, Unit defender, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack_Unit");
    attackActions.add(new InitAction());
    attackActions.add(new MoveAnimatedAction(attacker.getLocation(), moveTo));
    attackActions.add(new AttackUnitAction(attacker, defender));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    attackActions.setActionText(attacker, moveTo, defender.getCol(), defender.getRow());
    return attackActions;
  }

  public static CWAction buildUnitVsCityAttackAction(Unit attacker, City city, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack_City");
    attackActions.add(new InitAction());
    attackActions.add(new MoveAnimatedAction(attacker.getLocation(), moveTo));
    attackActions.add(new AttackCityAction(attacker, city));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    attackActions.setActionText(attacker, moveTo, city.getLocation().getCol(), city.getLocation().getRow());
    return attackActions;
  }

  public static CWAction buildAddUnitToTileAction(Unit unit, Tile selected, boolean canUndo) {
    ActionBag buildUnitActions = new ActionBag("Build_Unit");
    buildUnitActions.add(new InitAction());
    buildUnitActions.add(new AddUnitToTileAction(unit, selected, canUndo));
    buildUnitActions.add(new WaitAction(unit));
    buildUnitActions.add(new ClearInGameStateAction());
    buildUnitActions.setActionText(selected, unit.getStats().getID(), unit.getOwner().getId());
    return buildUnitActions;
  }

  public static CWAction buildEndTurnAction() {
    ActionBag endTurnAction = new ActionBag("End Turn");
    endTurnAction.add(new ClearInGameStateAction());
    endTurnAction.add(new EndTurnAction());
    endTurnAction.setActionText("end_turn");
    return endTurnAction;
  }

  public static CWAction buildLaunchRocketAction(Unit unit, City city, Location rocketDestination) {
    ActionBag launchRocketAction = new ActionBag("Launch_Rocket");
    launchRocketAction.add(new InitAction());
    launchRocketAction.add(new MoveAnimatedAction(unit.getLocation(), city.getLocation()));
    launchRocketAction.add(new WaitAction(unit));
    launchRocketAction.add(new LaunchRocketAction(city, unit, rocketDestination));
    launchRocketAction.add(new ClearInGameStateAction());
    launchRocketAction.setActionText(unit, city.getLocation(), rocketDestination.getCol(), rocketDestination.getRow());
    return launchRocketAction;
  }

  public static CWAction buildTransformTerrainAction(Unit unit, Location to, Terrain transformTo) {
    ActionBag transformTerrainActions = new ActionBag("Transform_terrain");
    transformTerrainActions.add(new InitAction());
    transformTerrainActions.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainActions.add(new WaitAction(unit));
    transformTerrainActions.add(new TransformTerrainAction(to, transformTo));
    transformTerrainActions.add(new ClearInGameStateAction());
    transformTerrainActions.setActionText(unit, to, transformTo.getID());
    return transformTerrainActions;
  }

  public static CWAction buildFireFlareAction(Unit unit, Location to, Location flareCenter) {
    ActionBag transformTerrainAction = new ActionBag("Flare");
    transformTerrainAction.add(new InitAction());
    transformTerrainAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    transformTerrainAction.add(new WaitAction(unit));
    transformTerrainAction.add(new FireFlareAction(flareCenter));
    transformTerrainAction.add(new ClearInGameStateAction());
    transformTerrainAction.setActionText(unit.getLocation(), to, flareCenter.getCol(), flareCenter.getRow());
    return transformTerrainAction;
  }

  public static CWAction buildConstructCityAction(Unit unit, City city, Location to, Player cityOwner) {
    ActionBag buildCityAction = new ActionBag("Build_City");
    buildCityAction.add(new InitAction());
    buildCityAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    buildCityAction.add(new WaitAction(unit));
    buildCityAction.add(new ConstructCityAction(unit, city, to, cityOwner));
    buildCityAction.add(new ClearInGameStateAction());
    buildCityAction.setActionText(unit, to, city.getID(), cityOwner.getId());
    return buildCityAction;
  }

  public static CWAction buildDiveAction(Unit unit, Location to) {
    ActionBag diveAction = new ActionBag("Dive");
    diveAction.add(new InitAction());
    diveAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    diveAction.add(new WaitAction(unit));
    diveAction.add(new DiveAction(unit));
    diveAction.add(new ClearInGameStateAction());
    diveAction.setActionText(unit, to);
    return diveAction;
  }

  public static CWAction buildSurfaceAction(Unit unit, Location to) {
    ActionBag surfaceAction = new ActionBag("Surface");
    surfaceAction.add(new InitAction());
    surfaceAction.add(new MoveAnimatedAction(unit.getLocation(), to));
    surfaceAction.add(new WaitAction(unit));
    surfaceAction.add(new SurfaceAction(unit));
    surfaceAction.add(new ClearInGameStateAction());
    surfaceAction.setActionText(unit, to);
    return surfaceAction;
  }

  public static CWAction buildProduceUnitAction(Unit producer, Unit unitToBuild, Location to) {
    ActionBag produceUnitAction = new ActionBag("Produce");
    produceUnitAction.add(new InitAction());
    produceUnitAction.add(new MoveAnimatedAction(producer.getLocation(), to));
    produceUnitAction.add(new ProduceUnitAction(producer, unitToBuild));
    produceUnitAction.add(new WaitAction(producer));
    produceUnitAction.add(new ClearInGameStateAction());
    produceUnitAction.setActionText(producer.getLocation(), to, unitToBuild.getStats().getID());
    return produceUnitAction;
  }

  public static CWAction buildEndGameAction() {
    return new EndGameAction();
  }
}
