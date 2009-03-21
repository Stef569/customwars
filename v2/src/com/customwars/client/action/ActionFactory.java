package com.customwars.client.action;

import com.customwars.client.action.unit.AttackAction;
import com.customwars.client.action.unit.CaptureAction;
import com.customwars.client.action.unit.DropAction;
import com.customwars.client.action.unit.JoinAction;
import com.customwars.client.action.unit.LoadAction;
import com.customwars.client.action.unit.MoveAnimatedAction;
import com.customwars.client.action.unit.SupplyAction;
import com.customwars.client.action.unit.WaitAction;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;

import java.util.List;

/**
 * Create in game actions
 *
 * @author stefan
 */
public class ActionFactory {
  public static CWAction buildDropAction(Unit transport, Location from, Location moveTo, int dropCount, List<Unit> unitsToBeDropped) {
    ActionBag dropActions = new ActionBag("Drop Actions");
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
    waitActions.add(new MoveAnimatedAction(unit.getLocation(), moveTo));
    waitActions.add(new WaitAction(unit));
    waitActions.add(new ClearInGameStateAction());
    return waitActions;
  }

  public static CWAction buildCaptureAction(Unit unit, City city) {
    ActionBag captureActions = new ActionBag("Capture actions");
    captureActions.add(new MoveAnimatedAction(unit.getLocation(), city.getLocation()));
    captureActions.add(new CaptureAction(unit, city));
    captureActions.add(new WaitAction(unit));
    captureActions.add(new ClearInGameStateAction());
    return captureActions;
  }

  public static CWAction buildLoadAction(Unit unit, Unit transport) {
    ActionBag loadActions = new ActionBag("Load Actions");
    loadActions.add(new MoveAnimatedAction(unit.getLocation(), transport.getLocation()));
    loadActions.add(new LoadAction(unit, transport));
    loadActions.add(new WaitAction(unit));
    loadActions.add(new ClearInGameStateAction());
    return loadActions;
  }

  public static CWAction buildSupplyAction(Unit supplier, Location moveTo) {
    ActionBag supplyActions = new ActionBag("Supply Actions");
    supplyActions.add(new MoveAnimatedAction(supplier.getLocation(), moveTo));
    supplyActions.add(new SupplyAction(supplier));
    supplyActions.add(new WaitAction(supplier));
    supplyActions.add(new ClearInGameStateAction());
    return supplyActions;
  }

  public static CWAction buildJoinAction(Unit unit, Unit target) {
    ActionBag joinActions = new ActionBag("Join Actions");
    joinActions.add(new MoveAnimatedAction(unit.getLocation(), target.getLocation()));
    joinActions.add(new JoinAction(unit, target));
    joinActions.add(new WaitAction(unit));
    joinActions.add(new ClearInGameStateAction());
    return joinActions;
  }

  public static CWAction buildAttackAction(Unit attacker, Unit defender, Location moveTo) {
    ActionBag attackActions = new ActionBag("Attack Actions");
    attackActions.add((new MoveAnimatedAction(attacker.getLocation(), moveTo)));
    attackActions.add(new AttackAction(attacker, defender));
    attackActions.add(new WaitAction(attacker));
    attackActions.add(new ClearInGameStateAction());
    return attackActions;
  }
}
